package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.dto.AddressDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.*;
import com.elleined.marketplaceapi.mapper.AddressMapper;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.address.AddressValidator;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.product.CropService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.product.UnitService;
import com.elleined.marketplaceapi.service.user.*;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// This is used for orchestrating all transactions instead of using SAGA design pattern
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MarketplaceService implements IMarketplaceService {
    private final EmailService emailService;
    private final PrincipalService principalService;
    private final SellerService sellerService;
    private final BuyerService buyerService;
    private final UserDetailsValidator userDetailsValidator;
    private final UserCredentialValidator userCredentialValidator;
    private final AddressValidator addressValidator;
    private final ProductService productService;
    private final UserService userService;
    private final AddressService addressService;
    private final CartItemService cartItemService;

    private final CropService cropService;
    private final SuffixService suffixService;
    private final UnitService unitService;

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final AddressMapper addressMapper;
    private final FeeService feeService;


    @Override
    public ProductDTO getProductById(int productId) throws ResourceNotFoundException {
        Product product = productService.getById(productId);

        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO saveProduct(int sellerId, ProductDTO productDTO)
            throws ResourceNotFoundException, InsufficientBalanceException {
        User seller = userService.getById(sellerId);

        if (sellerService.isSellerExceedsToMaxListingPerDay(seller)) throw new ProductListingException("You already reached the limit of product listing per day which is " + SellerService.SELLER_MAX_LISTING_PER_DAY);
        if (!userService.isVerified(seller)) throw new NotVerifiedException("Cannot list a product because user with id of " + sellerId + " are not yet been verified");
        double totalPrice = productService.calculateTotalPrice(productDTO.getPricePerUnit(), productDTO.getQuantityPerUnit(), productDTO.getAvailableQuantity());
        double listingFee = productService.getListingFee(totalPrice);
        if (sellerService.isBalanceNotEnoughToPayListingFee(seller, listingFee)) throw new InsufficientBalanceException("Seller with id of " + sellerId + " doesn't have enough balance to pay for the listing fee of " + listingFee + " which is 5% of total price " + totalPrice);

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        Product product = sellerService.saveProduct(productDTO, seller);
        feeService.deductListingFee(seller, listingFee);
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO updateProduct(int currentUserId, int productId, ProductDTO productDTO)
            throws ResourceNotFoundException, NotOwnedException {

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");
        if (!userService.isVerified(currentUser)) throw new NotVerifiedException("Cannot update a product because user with id of " + currentUserId + " are not yet been verified! Consider register shop first then get verified afterwards");

        if (productService.isCriticalFieldsChanged(product, productDTO)) product.setState(Product.State.PENDING);
        sellerService.updateProduct(product, productDTO);

        return productMapper.toDTO(product);
    }

    @Override
    public void deleteProduct(int currentUserId, int productId)
            throws ResourceNotFoundException, NotOwnedException, OrderException {
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        if (productService.isProductHasPendingOrder(product)) throw new OrderException("Cannot delete this product! Because product with id of " + product.getId() + " has a pending orders. Please settle first the pending products to delete this");
        if (productService.isProductHasAcceptedOrder(product)) throw new OrderException("Cannot delete this product! Because product with id of " + product.getId() + " has a pending orders. Please settle first the accepted products to delete this");
        sellerService.deleteProduct(productId);
    }

    @Override
    public List<ProductDTO> getAllProductExcept(int currentUserId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return productService.getAllExcept(currentUser).stream()
                .map(productMapper::toDTO)
                .toList();
    }


    @Override
    @Transactional(noRollbackFor = MessagingException.class)
    public UserDTO saveUser(UserDTO userDTO)
            throws ResourceNotFoundException,
            HasDigitException,
            PasswordNotMatchException,
            WeakPasswordException,
            MalformedEmailException,
            AlreadExistException,
            MobileNumberException {

        addressValidator.validateAddressDetails(userDTO.getAddressDTO());
        userDetailsValidator.validatePhoneNumber(userDTO.getUserDetailsDTO());
        userDetailsValidator.validateFullName(userDTO.getUserDetailsDTO());
        userCredentialValidator.validateEmail(userDTO.getUserCredentialDTO());
        userCredentialValidator.validatePassword(userDTO.getUserCredentialDTO());
        if (!suffixService.existsByName(userDTO.getUserDetailsDTO().getSuffix())) suffixService.save(userDTO.getUserDetailsDTO().getSuffix());

        User registeringUser = userService.saveByDTO(userDTO);
        addressService.saveUserAddress(registeringUser, userDTO.getAddressDTO());
        if (!StringUtil.isNotValid(userDTO.getInvitationReferralCode())) userService.addInvitedUser(userDTO.getInvitationReferralCode(), registeringUser);

        emailService.sendRegistrationEmail(registeringUser);
        return userMapper.toDTO(registeringUser);
    }

    @Override
    public UserDTO getUserById(int userId) throws ResourceNotFoundException {
        User user = userService.getById(userId);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO resendValidId(int currentUserId, String newValidId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        userService.resendValidId(currentUser, newValidId);
        return userMapper.toDTO(currentUser);
    }

    @Override
    public UserDTO login(UserDTO.UserCredentialDTO userCredentialDTO)
            throws ResourceNotFoundException, InvalidUserCredentialException {
        User currentUser = userService.login(userCredentialDTO);
        principalService.setPrincipal(currentUser);
        return userMapper.toDTO(currentUser);
    }

    @Override
    public ShopDTO sendShopRegistration(int currentUserId, ShopDTO shopDTO)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);

        if (userService.isUserHasShopRegistration(currentUser)) throw new NotVerifiedException("User with id of " + currentUserId + " already have shop registration! Please wait for email notification. If dont receive an email consider resending your valid id!");
        userService.sendShopRegistration(currentUser, shopDTO);
        return shopDTO;
    }

    @Override
    public OrderItemDTO orderProduct(int buyerId, OrderItemDTO orderItemDTO)
            throws ResourceNotFoundException, OrderException {
        int productId = orderItemDTO.getProductId();

        User buyer = userService.getById(buyerId);
        Product product = productService.getById(productId);

        if (buyerService.isUserAlreadyOrderedProduct(buyer, product)) throw new  OrderException("User with id of " + buyerId + " already order this product with id of " + productId + " please wait until seller take action in you order request!");
        if (userService.hasProduct(buyer, product)) throw new OrderException("You cannot order your own product listing!");
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");
        if (product.getState() == Product.State.SOLD) throw new OrderException("Product with id of " + productId + " are already been sold!");
        if (product.getState() != Product.State.LISTING) throw new OrderException("Product with id of " + productId + " are not yet listed!");
        if (productService.isExceedingToAvailableQuantity(product, orderItemDTO.getOrderQuantity())) throw new OrderException("You are trying to order that exceeds to available amount!");
        if (productService.isSellerAlreadyRejectedBuyerForThisProduct(buyer, product)) throw new OrderException("Cannot order! Because seller with id of " + product.getSeller().getId() +  " already rejected this buyer for this product! Don't spam bro :)");

        if (cartItemService.isProductAlreadyInCart(buyer, product)) {
            CartItem cartItem = cartItemService.getByProduct(buyer, product);
            cartItemService.delete(cartItem);
        }
        OrderItem savedOrderItem = buyerService.orderProduct(buyer, orderItemDTO);
        return itemMapper.toOrderItemDTO(savedOrderItem);
    }

    @Override
    public List<OrderItemDTO> getAllOrderedProductsByStatus(int currentUserId, String orderItemStatus) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return buyerService.getAllOrderedProductsByStatus(currentUser, OrderItem.OrderItemStatus.valueOf(orderItemStatus)).stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }

    @Override
    public void cancelOrderItem(int buyerId, int orderItemId)
            throws ResourceNotFoundException, NotOwnedException {

        User buyer = userService.getById(buyerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);

        if (!buyerService.isBuyerHasOrder(buyer, orderItem)) throw new NotOwnedException("User with id of " + buyerId +  " does not have order item with id of " + orderItemId);
        if (buyerService.isSellerAcceptedOrder(orderItem)) throw new OrderException("Cannot cancel order because order with id of " + orderItemId + " are already accepted by the seller!");
        
        buyerService.cancelOrderItem(buyer, orderItem);
    }

    @Override
    public List<ProductDTO> getAllProductByState(int currentUserId, String state) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return sellerService.getAllProductByState(currentUser, Product.State.valueOf(state)).stream()
                .map(productMapper::toDTO)
                .toList();
    }


    @Override
    public AddressDTO saveDeliveryAddress(int currentUserId, AddressDTO addressDTO)
            throws ResourceNotFoundException, AlreadExistException {
        User currentUser = userService.getById(currentUserId);

        addressValidator.validateAddressDetails(addressDTO);
        if (addressService.isUserHas5DeliveryAddress(currentUser)) throw new DeliveryAddressLimitException("Cannot save another delivery address! Because you already reached the limit which is 5");

        DeliveryAddress deliveryAddress = addressService.saveDeliveryAddress(currentUser, addressDTO);
        return addressMapper.toDTO(deliveryAddress);
    }

    @Override
    public void deleteDeliveryAddress(int currentUserId, int deliveryAddressId)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);
        if (currentUser.getDeliveryAddresses().stream().noneMatch(deliveryAddress -> deliveryAddress.getId() == deliveryAddressId)) throw new NotOwnedException("User with id of " + currentUser.getId() + " does not have delivery address with id of " + deliveryAddressId);
        addressService.deleteDeliveryAddress(currentUser, deliveryAddressId);
    }

    @Override
    public List<AddressDTO> getAllDeliveryAddress(int currentUserId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return addressService.getAllDeliveryAddress(currentUser).stream()
                .map(addressMapper::toDTO)
                .toList();
    }

    @Override
    public void acceptOrder(int sellerId, int orderItemId, String messageToBuyer)
            throws ResourceNotFoundException, NotValidBodyException {
        User seller = userService.getById(sellerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);

        if (!sellerService.isSellerHasOrder(seller, orderItem)) throw new ResourceNotFoundException("Seller with id of " + sellerId + " doesnt have order with id of " + orderItemId);
        if (StringUtil.isNotValid(messageToBuyer)) throw new NotValidBodyException("Please provide a message for the buyer... can be anything thanks");
        sellerService.acceptOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public void rejectOrder(int sellerId, int orderItemId, String messageToBuyer)
            throws ResourceNotFoundException, NotValidBodyException {
        User seller = userService.getById(sellerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);

        if (!sellerService.isSellerHasOrder(seller, orderItem)) throw new ResourceNotFoundException("Seller with id of " + sellerId + " doesnt have order with id of " + orderItemId);
        if (StringUtil.isNotValid(messageToBuyer)) throw new NotValidBodyException("Please provide a message for the buyer... can be anything thanks");
        sellerService.rejectOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public List<OrderItemDTO> getAllSellerProductOrderByStatus(int sellerId, String orderItemStatus) throws ResourceNotFoundException {
        User seller = userService.getById(sellerId);
        OrderItem.OrderItemStatus status = OrderItem.OrderItemStatus.valueOf(orderItemStatus);
        return sellerService.getAllSellerProductOrderByStatus(seller, status).stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }

    @Override
    public List<String> getAllCrops() {
        return cropService.getAll();
    }

    @Override
    public List<String> getAllUnit() {
        return unitService.getAll();
    }
    @Override
    public List<String> getAllSuffix() {
        return suffixService.getAll();
    }

    @Override
    public List<CartItemDTO> getAllCartItems(int currentUserId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return cartItemService.getAll(currentUser).stream()
                .map(itemMapper::toCartItemDTO)
                .toList();
    }

    @Override
    public void deleteCartItem(int currentUserId, int cartItemId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        cartItemService.delete(currentUser, cartItemId);
    }

    // alias for add to cart
    @Override
    public CartItemDTO saveCartItem(int currentUserId, CartItemDTO cartItemDTO) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(cartItemDTO.getProductId());

        if (buyerService.isUserAlreadyOrderedProduct(currentUser, product)) throw new  OrderException("User with id of " + currentUser.getId() + " already order this product with id of " + product.getId() + " please wait until seller take action in you order request!");
        if (userService.hasProduct(currentUser, product)) throw new OrderException("You cannot order your own product listing!");
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + product.getId() + " does not exists or might already been deleted!");
        if (product.getState() == Product.State.SOLD) throw new OrderException("Product with id of " + product.getId() + " are already been sold!");
        if (product.getState() != Product.State.LISTING) throw new OrderException("Product with id of " + product.getId() + " are not yet listed!");
        if (productService.isExceedingToAvailableQuantity(product, cartItemDTO.getOrderQuantity())) throw new OrderException("You are trying to order that exceeds to available amount!");
        if (productService.isSellerAlreadyRejectedBuyerForThisProduct(currentUser, product)) throw new OrderException("Cannot order! Because seller with id of " + product.getSeller().getId() +  " already rejected this currentUser for this product! Don't spam bro :)");

        if (cartItemService.isProductAlreadyInCart(currentUser, product)) {
            CartItem cartItem = cartItemService.getByProduct(currentUser, product);
            cartItemService.updateOrderQuantity(cartItem);
            return itemMapper.toCartItemDTO(cartItem);
        }
        CartItem cartItem = cartItemService.save(currentUser, cartItemDTO);
        return itemMapper.toCartItemDTO(cartItem);
    }

    @Override
    public OrderItemDTO moveToOrderItem(int cartItemId)
            throws ResourceNotFoundException, OrderException {
        CartItem cartItem = cartItemService.getCartItemById(cartItemId);
        User buyer = cartItem.getPurchaser();
        Product product = cartItem.getProduct();

        if (buyerService.isUserAlreadyOrderedProduct(buyer, product)) throw new  OrderException("User with id of " + buyer.getId() + " already order this product with id of " + product.getId() + " please wait until seller take action in you order request!");
        if (userService.hasProduct(buyer, product)) throw new OrderException("You cannot order your own product listing!");
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + product.getId() + " does not exists or might already been deleted!");
        if (product.getState() == Product.State.SOLD) throw new OrderException("Product with id of " + product.getId() + " are already been sold!");
        if (product.getState() != Product.State.LISTING) throw new OrderException("Product with id of " + product.getId() + " are not yet listed!");
        if (productService.isExceedingToAvailableQuantity(product, cartItem.getOrderQuantity())) throw new OrderException("You are trying to order that exceeds to available amount!");
        if (productService.isSellerAlreadyRejectedBuyerForThisProduct(buyer, product)) throw new OrderException("Cannot order! Because seller with id of " + product.getSeller().getId() +  " already rejected this buyer for this product! Don't spam bro :)");

        OrderItem orderItem = cartItemService.moveToOrderItem(cartItem);
        return itemMapper.toOrderItemDTO(orderItem);
    }

    @Override
    public List<OrderItemDTO> moveAllToOrderItem(List<Integer> cartItemIds) throws ResourceNotFoundException {
        return cartItemIds.stream()
                .map(this::moveToOrderItem)
                .toList();
    }

    @Override
    public void updateProductStateToSold(int sellerId, int productId) throws ResourceNotFoundException {
        User seller = userService.getById(sellerId);
        Product product = productService.getById(productId);
        sellerService.updateProductStateToSold(seller, product);
    }

    @Override
    public AddressDTO getDeliveryAddressById(int currentUserId, int deliveryAddressId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        DeliveryAddress deliveryAddress = currentUser.getDeliveryAddresses().stream()
                .filter(address -> address.getId() == deliveryAddressId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with id of " +  currentUser + " doesnt have delivery address with id of " + deliveryAddressId));
        return addressMapper.toDTO(deliveryAddress);
    }
}
