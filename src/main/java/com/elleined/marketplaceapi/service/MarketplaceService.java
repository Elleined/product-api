package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.dto.*;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.*;
import com.elleined.marketplaceapi.mapper.AddressMapper;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// This is used for orchestrating all transactions instead of using SAGA design pattern
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MarketplaceService {
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

    private final CropService cropService;
    private final SuffixService suffixService;
    private final UnitService unitService;

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final AddressMapper addressMapper;
    private final FeeService feeService;


    public ProductDTO getProductById(int currentUserId, int productId)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");
        return productMapper.toDTO(product);
    }

    public ProductDTO saveProduct(int sellerId, ProductDTO productDTO)
            throws ResourceNotFoundException, InsufficientBalanceException {
        User seller = userService.getById(sellerId);

        if (!userService.isVerified(seller)) throw new NotVerifiedException("Cannot list a product because user with id of " + sellerId + " are not yet been verified");
        double totalPrice = sellerService.getTotalPrice(productDTO);
        double listingFee = sellerService.getListingFee(totalPrice);
        if (sellerService.isBalanceNotEnoughToPayListingFee(seller, listingFee)) throw new InsufficientBalanceException("Seller with id of " + sellerId + " doesn't have enough balance to pay for the listing fee of " + listingFee + " which is 5% of total price " + totalPrice);

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        Product product = sellerService.saveProduct(productDTO, seller);
        feeService.deductListingFee(seller, listingFee);
        return productMapper.toDTO(product);
    }

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

    public void deleteProduct(int currentUserId, int productId)
            throws ResourceNotFoundException, NotOwnedException, OrderException {
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        if (productService.isProductHasPendingOrder(product)) throw new OrderException("Cannot delete this product! Because product with id of " + product.getId() + " has a pending orders. Please settle first the pending products to delete this");
        if (productService.isProductHasAcceptedOrder(product)) throw new OrderException("Cannot delete this product! Because product with id of " + product.getId() + " has a pending orders. Please settle first the accepted products to delete this");
        sellerService.deleteProduct(productId);
    }

    public List<ProductDTO> getAllProductExcept(int currentUserId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return productService.getAllExcept(currentUser).stream()
                .map(productMapper::toDTO)
                .toList();
    }


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

    public UserDTO getUserById(int userId) throws ResourceNotFoundException {
        User user = userService.getById(userId);
        return userMapper.toDTO(user);
    }

    public UserDTO resendValidId(int currentUserId, String newValidId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        userService.resendValidId(currentUser, newValidId);
        return userMapper.toDTO(currentUser);
    }

    public UserDTO login(UserDTO.UserCredentialDTO userCredentialDTO)
            throws ResourceNotFoundException, InvalidUserCredentialException {
        User currentUser = userService.login(userCredentialDTO);
        principalService.setPrincipal(currentUser);
        return userMapper.toDTO(currentUser);
    }

    public ShopDTO sendShopRegistration(int currentUserId, ShopDTO shopDTO)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);

        if (userService.isUserHasShopRegistration(currentUser)) throw new NotVerifiedException("User with id of " + currentUserId + " already have shop registration! Please wait for email notification. If dont receive an email consider resending your valid id!");
        userService.sendShopRegistration(currentUser, shopDTO);
        return shopDTO;
    }

    public OrderItemDTO orderProduct(int buyerId, OrderItemDTO orderItemDTO)
            throws ResourceNotFoundException {
        int productId = orderItemDTO.getProductId();

        User buyer = userService.getById(buyerId);
        Product product = productService.getById(productId);

        if (buyerService.isUserAlreadyOrderedProduct(buyer, product)) throw new  OrderException("User with id of " + buyerId + " already order this product with id of " + productId + " please wait until seller take action in you order request!");
        if (userService.hasProduct(buyer, product)) throw new OrderException("You cannot order your own product listing!");
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");
        if (product.getState() == Product.State.SOLD) throw new OrderException("Product with id of " + productId + " are already been sold!");
        if (product.getState() != Product.State.LISTING) throw new OrderException("Product with id of " + productId + " are not yet listed!");
        if (productService.isExceedingToAvailableQuantity(product, orderItemDTO.getOrderQuantity())) throw new OrderException("You are trying to order that exceeds to available amount!");
        if (productService.isNotExactToQuantityPerUnit(product, orderItemDTO.getOrderQuantity())) throw new OrderException("Cannot order! Because you are trying to order an amount that are not compliant to specified quantity per unit of seller which is " + product.getQuantityPerUnit());
        if (productService.isSellerAlreadyRejectedBuyerForThisProduct(buyer, product)) throw new OrderException("Cannot order! Because seller with id of " + product.getSeller().getId() +  " already rejected this buyer for this product! Don't spam bro :)");

        OrderItem savedOrderItem = buyerService.orderProduct(buyer, orderItemDTO);
        return itemMapper.toOrderItemDTO(savedOrderItem);
    }

    public List<OrderItemDTO> getAllOrderedProductsByStatus(int currentUserId, String orderItemStatus) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return buyerService.getAllOrderedProductsByStatus(currentUser, OrderItem.OrderItemStatus.valueOf(orderItemStatus)).stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }

    public void cancelOrderItem(int buyerId, int orderItemId)
            throws ResourceNotFoundException, NotOwnedException {

        User buyer = userService.getById(buyerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);

        if (!buyerService.isBuyerHasOrder(buyer, orderItem)) throw new NotOwnedException("User with id of " + buyerId +  " does not have order item with id of " + orderItemId);
        if (buyerService.isSellerAcceptedOrder(orderItem)) throw new OrderException("Cannot cancel order because order with id of " + orderItemId + " are already accepted by the seller!");
    }

    public List<ProductDTO> getAllProductByState(int currentUserId, String state) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return sellerService.getAllProductByState(currentUser, Product.State.valueOf(state)).stream()
                .map(productMapper::toDTO)
                .toList();
    }


    public AddressDTO saveDeliveryAddress(int currentUserId, AddressDTO addressDTO)
            throws ResourceNotFoundException, AlreadExistException {
        User currentUser = userService.getById(currentUserId);

        addressValidator.validateAddressDetails(addressDTO);
        if (addressService.isUserHas5DeliveryAddress(currentUser)) throw new DeliveryAddressLimitException("Cannot save another delivery address! Because you already reached the limit which is 5");

        DeliveryAddress deliveryAddress = addressService.saveDeliveryAddress(currentUser, addressDTO);
        return addressMapper.toDTO(deliveryAddress);
    }

    public void deleteDeliveryAddress(int currentUserId, int deliveryAddressId)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);
        if (currentUser.getDeliveryAddresses().stream().noneMatch(deliveryAddress -> deliveryAddress.getId() == deliveryAddressId)) throw new NotOwnedException("User with id of " + currentUser.getId() + " does not have delivery address with id of " + deliveryAddressId);
        addressService.deleteDeliveryAddress(currentUser, deliveryAddressId);
    }

    public List<AddressDTO> getAllDeliveryAddress(int currentUserId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return addressService.getAllDeliveryAddress(currentUser).stream()
                .map(addressMapper::toDTO)
                .toList();
    }

    public void acceptOrder(int sellerId, int orderItemId, String messageToBuyer)
            throws ResourceNotFoundException, NotValidBodyException {
        User seller = userService.getById(sellerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);

        if (!sellerService.isSellerHasOrder(seller, orderItem)) throw new ResourceNotFoundException("Seller with id of " + sellerId + " doesnt have order with id of " + orderItemId);
        if (StringUtil.isNotValid(messageToBuyer)) throw new NotValidBodyException("Please provide a message for the buyer... can be anything thanks");
        sellerService.acceptOrder(seller, orderItem, messageToBuyer);
    }

    public void rejectOrder(int sellerId, int orderItemId, String messageToBuyer)
            throws ResourceNotFoundException, NotValidBodyException {
        User seller = userService.getById(sellerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);

        if (!sellerService.isSellerHasOrder(seller, orderItem)) throw new ResourceNotFoundException("Seller with id of " + sellerId + " doesnt have order with id of " + orderItemId);
        if (StringUtil.isNotValid(messageToBuyer)) throw new NotValidBodyException("Please provide a message for the buyer... can be anything thanks");
        sellerService.rejectOrder(seller, orderItem, messageToBuyer);
    }

    public List<OrderItemDTO> getAllSellerProductOrderByStatus(int sellerId, String orderItemStatus) throws ResourceNotFoundException {
        User seller = userService.getById(sellerId);
        OrderItem.OrderItemStatus status = OrderItem.OrderItemStatus.valueOf(orderItemStatus);
        return sellerService.getAllSellerProductOrderByStatus(seller, status).stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }


    public List<String> getAllCrops() {
        return cropService.getAll();
    }

    public List<String> getAllUnit() {
        return unitService.getAll();
    }
    public List<String> getAllSuffix() {
        return suffixService.getAll();
    }
}
