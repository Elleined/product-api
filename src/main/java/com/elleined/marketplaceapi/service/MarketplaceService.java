package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.*;
import com.elleined.marketplaceapi.mapper.AddressMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.CropService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.product.UnitService;
import com.elleined.marketplaceapi.service.user.UserCredentialValidator;
import com.elleined.marketplaceapi.service.user.UserDetailsValidator;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MarketplaceService {
    private final UserDetailsValidator userDetailsValidator;
    private final UserCredentialValidator userCredentialValidator;
    private final CropService cropService;
    private final UnitService unitService;
    private final ProductService productService;
    private final UserService userService;
    private final AddressService addressService;

    private final ProductMapper productMapper;
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
        if (currentUser.getShop() == null) throw new NotVerifiedException("Cannot list a product because user with id of " + currentUserId + " are not yet been verified");
        if (!userService.isVerified(currentUser)) throw new NotVerifiedException("Cannot list a product because user with id of " + currentUserId + " are not yet been verified");

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        Product product = productService.saveByDTO(productDTO);
        return productMapper.toDTO(product);
    }

    public ProductDTO update(int currentUserId, int productId, ProductDTO productDTO)
            throws ResourceNotFoundException, NotOwnedException {

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (currentUser.getShop() == null) throw new NotVerifiedException("Cannot update a product because user with id of " + currentUserId + " are not yet been verified");
        if (!userService.isVerified(currentUser)) throw new NotVerifiedException("Cannot update a product because user with id of " + currentUserId + " are not yet been verified");
        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);
        if (productService.isDeleted(product)) throw new ResourceNotFoundException("Product with id of " + productId + " does not exists or might already been deleted!");

        productService.update(product, productDTO);
        return productMapper.toDTO(product);
    }


    public List<ProductDTO> getAllProductExcept(int currentUserId) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return productService.getAllExcept(currentUser).stream()
                .map(productMapper::toDTO)
                .toList();
    }


    public List<ProductDTO> getAllProductByState(int currentUserId, String state) throws ResourceNotFoundException {
        User currentUser = userService.getById(currentUserId);
        return productService.getAllProductByState(currentUser, Product.State.valueOf(state)).stream()
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

        userDetailsValidator.validatePhoneNumber(userDTO.getUserDetailsDTO());
        userDetailsValidator.validateFullName(userDTO.getUserDetailsDTO());
        userCredentialValidator.validateEmail(userDTO.getUserCredentialDTO());
        userCredentialValidator.validatePassword(userDTO.getUserCredentialDTO());

        User user = userService.saveByDTO(userDTO);
        UserAddress userAddress = addressMapper.toUserAddressEntity(userDTO.getAddressDTO(), user);
        addressService.saveUserAddress(userAddress);
        // user dto here
        return new UserDTO();
    }
}
