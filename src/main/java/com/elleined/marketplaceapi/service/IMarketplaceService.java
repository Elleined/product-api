package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.dto.AddressDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.*;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IMarketplaceService {
    ProductDTO getProductById(int productId) throws ResourceNotFoundException;

    ProductDTO saveProduct(int sellerId, ProductDTO productDTO)
            throws ResourceNotFoundException, InsufficientBalanceException;

    ProductDTO updateProduct(int currentUserId, int productId, ProductDTO productDTO)
            throws ResourceNotFoundException, NotOwnedException;

    void deleteProduct(int currentUserId, int productId)
            throws ResourceNotFoundException, NotOwnedException, OrderException;

    List<ProductDTO> getAllProductExcept(int currentUserId) throws ResourceNotFoundException;

    @Transactional(noRollbackFor = MessagingException.class)
    UserDTO saveUser(UserDTO userDTO)
            throws ResourceNotFoundException,
            HasDigitException,
            PasswordNotMatchException,
            WeakPasswordException,
            MalformedEmailException,
            AlreadExistException,
            MobileNumberException;

    UserDTO getUserById(int userId) throws ResourceNotFoundException;

    UserDTO resendValidId(int currentUserId, String newValidId) throws ResourceNotFoundException;

    UserDTO login(UserDTO.UserCredentialDTO userCredentialDTO)
            throws ResourceNotFoundException, InvalidUserCredentialException;

    ShopDTO sendShopRegistration(int currentUserId, ShopDTO shopDTO)
            throws ResourceNotFoundException, NotOwnedException;

    OrderItemDTO orderProduct(int buyerId, OrderItemDTO orderItemDTO)
            throws ResourceNotFoundException, OrderException;

    List<OrderItemDTO> getAllOrderedProductsByStatus(int currentUserId, String orderItemStatus) throws ResourceNotFoundException;

    void cancelOrderItem(int buyerId, int orderItemId)
            throws ResourceNotFoundException, NotOwnedException;

    List<ProductDTO> getAllProductByState(int currentUserId, String state) throws ResourceNotFoundException;

    AddressDTO saveDeliveryAddress(int currentUserId, AddressDTO addressDTO)
            throws ResourceNotFoundException, AlreadExistException;

    List<AddressDTO> getAllDeliveryAddress(int currentUserId) throws ResourceNotFoundException;

    void acceptOrder(int sellerId, int orderItemId, String messageToBuyer)
            throws ResourceNotFoundException, NotValidBodyException;

    void rejectOrder(int sellerId, int orderItemId, String messageToBuyer)
            throws ResourceNotFoundException, NotValidBodyException;

    List<OrderItemDTO> getAllSellerProductOrderByStatus(int sellerId, String orderItemStatus) throws ResourceNotFoundException;

    List<String> getAllCrops();

    List<String> getAllUnit();

    List<String> getAllSuffix();

    List<String> getAllGender();

    List<CartItemDTO> getAllCartItems(int currentUserId) throws ResourceNotFoundException;

    void deleteCartItem(int currentUserId, int cartItemId) throws ResourceNotFoundException;

    // alias for add to cart
    CartItemDTO saveCartItem(int currentUserId, CartItemDTO cartItemDTO) throws ResourceNotFoundException;

    OrderItemDTO moveToOrderItem(int cartItemId)
            throws ResourceNotFoundException, OrderException;

    List<OrderItemDTO> moveAllToOrderItem(List<Integer> cartItemIds) throws ResourceNotFoundException;

    void updateProductStateToSold(int sellerId, int productId) throws ResourceNotFoundException;

    AddressDTO getDeliveryAddressById(int currentUserId, int deliveryAddressId) throws ResourceNotFoundException;

    void buyPremium(int currentUserId) throws InsufficientBalanceException, ResourceNotFoundException;
}
