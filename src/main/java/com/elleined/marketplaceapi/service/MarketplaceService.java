package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.dto.AddressDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.*;
import com.elleined.marketplaceapi.mapper.AddressMapper;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.address.AddressValidator;
import com.elleined.marketplaceapi.service.product.CropService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.product.UnitService;
import com.elleined.marketplaceapi.service.user.*;
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


    public void deleteProduct(int currentUserId, int productId)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        productService.delete(productId);
    }

    public ProductDTO getProductById(int currentUserId, int productId)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");
        return productMapper.toDTO(product);
    }

    public ProductDTO saveByDTO(int currentUserId, ProductDTO productDTO) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        if (!userService.isVerified(currentUser)) throw new NotVerifiedException("Cannot list a product because user with id of " + currentUserId + " are not yet been verified");

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        Product product = productService.saveByDTO(productDTO);
        return productMapper.toDTO(product);
    }

    public ProductDTO updateProduct(int currentUserId, int productId, ProductDTO productDTO)
            throws ResourceNotFoundException, NotOwnedException {

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!userService.isVerified(currentUser)) throw new NotVerifiedException("Cannot update a product because user with id of " + currentUserId + " are not yet been verified! Consider register shop first then get verified afterwards");
        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");

        if (productService.isCriticalFieldsChanged(product, productDTO)) product.setState(Product.State.PENDING);
        productService.update(product, productDTO);

        return productMapper.toDTO(product);
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
        if (!suffixService.existsByName(userDTO.getSuffix())) suffixService.save(userDTO.getSuffix());

        User registeringUser = userService.saveByDTO(userDTO);
        addressService.saveUserAddress(registeringUser, userDTO.getAddressDTO());

        return userMapper.toDTO(registeringUser);
    }

    public UserDTO getUserById(int userId) throws ResourceNotFoundException {
        User user = userService.getById(userId);
        return userMapper.toDTO(user);
    }

    public UserDTO updateUser(int currentUserId, UserDTO userDTO)
            throws ResourceNotFoundException,
            HasDigitException,
            PasswordNotMatchException,
            WeakPasswordException,
            MalformedEmailException,
            AlreadExistException,
            MobileNumberException {

        User currentUser = userService.getById(currentUserId);
        addressValidator.validateAddressDetails(userDTO.getAddressDTO());
        userDetailsValidator.validatePhoneNumber(userDTO.getUserDetailsDTO());
        userDetailsValidator.validateFullName(userDTO.getUserDetailsDTO());
        userCredentialValidator.validateEmail(userDTO.getUserCredentialDTO());
        userCredentialValidator.validatePassword(userDTO.getUserCredentialDTO());
        if (!suffixService.existsByName(userDTO.getSuffix())) suffixService.save(userDTO.getSuffix());

        userService.update(userDTO, currentUser);
        return userMapper.toDTO(currentUser);
    }

    public UserDTO resendValidId(int currentUserId, String newValidId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        userService.resendValidId(currentUser, newValidId);
        return userMapper.toDTO(currentUser);
    }

    public UserDTO login(UserDTO.UserCredentialDTO userCredentialDTO)
            throws ResourceNotFoundException, InvalidUserCredentialException {
        User currentUser = userService.login(userCredentialDTO);
        return userMapper.toDTO(currentUser);
    }

    public ShopDTO sendShopRegistration(int currentUserId, ShopDTO shopDTO)
            throws ResourceNotFoundException, NotOwnedException {
        User currentUser = userService.getById(currentUserId);

        if (userService.isUserHasShopRegistration(currentUser)) throw new NotVerifiedException("User with id of " + currentUserId + " already have shop registration! Please wait for email notification. If dont receive an email consider resending your valid id!");

        Shop shop = userService.sendShopRegistration(currentUser, shopDTO);
        return shopDTO;
    }

    public OrderItemDTO orderProduct(int buyerId, OrderItemDTO orderItemDTO)
            throws ResourceNotFoundException {
        int productId = orderItemDTO.getProductId();

        User buyer = userService.getById(buyerId);
        Product product = productService.getById(productId);

        if (userService.hasProduct(buyer, product)) throw new OrderException("You cannot order your own product listing!");
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");
        if (product.getState() == Product.State.SOLD) throw new OrderException("Product with id of " + productId + " are already been sold!");
        if (product.getState() != Product.State.LISTING) throw new OrderException("Product with id of " + productId + " are not yet listed!");
        if (productService.isExceedingToAvailableQuantity(product, orderItemDTO.getOrderQuantity())) throw new OrderException("You are trying to order that exceeds to available amount!");
        if (productService.isNotExactToQuantityPerUnit(product, orderItemDTO.getOrderQuantity())) throw new OrderException("Cannot order! Because you are trying to order an amount that are not compliant to specified quantity per unit!");

        // sendEmailToSeller();
        OrderItem savedOrderItem = buyerService.orderProduct(buyer, orderItemDTO);
        return itemMapper.toOrderItemDTO(savedOrderItem);
    }

    public List<ProductDTO> getAllOrderedProductsByStatus(int currentUserId, String orderItemStatus) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return buyerService.getAllOrderedProductsByStatus(currentUser, OrderItem.OrderItemStatus.valueOf(orderItemStatus)).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public void cancelOrderItem(int currentUserId, int orderId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        if (currentUser.getOrderedItems().stream().noneMatch(orderItem -> orderItem.getId() == orderId))
            throw new NotOwnedException("User with id of " + currentUserId +  " does not have order item with id of " + orderId);
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

    public List<AddressDTO> getAllDeliveryAddress(int currentUserId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return addressService.getAllDeliveryAddress(currentUser).stream()
                .map(addressMapper::toDTO)
                .toList();
    }
}
