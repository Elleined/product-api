package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.NotOwnedException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.BaseMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.product.CropService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.product.UnitService;
import com.elleined.marketplaceapi.service.user.SuffixService;
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
    private final CropService cropService;
    private final UnitService unitService;
    private final SuffixService suffixService;
    private final ProductService productService;
    private final UserService userService;

    private final ProductMapper productMapper;

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
        return productMapper.toDTO(product);
    }

    public ProductDTO saveByDTO(ProductDTO productDTO) throws ResourceNotFoundException {
        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());

        Product product = productService.saveByDTO(productDTO);
        return productMapper.toDTO(product);
    }

    public ProductDTO update(int currentUserId, int productId, ProductDTO productDTO)
            throws ResourceNotFoundException, NotOwnedException {

        User currentUser = userService.getById(currentUserId);
        Product product = productService.getById(productId);

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        if (!userService.hasProduct(currentUser, product)) throw new NotOwnedException("Current user with id of " + currentUserId + " does not have product with id of " + productId);

        productService.update(product, productDTO);
        return productMapper.toDTO(product);
    }


    public List<ProductDTO> getAllProductExcept(int verifiedUserId) throws ResourceNotFoundException {
        User verifiedUser = userService.getById(verifiedUserId);
        return productService.getAllExcept(verifiedUser).stream()
                .map(productMapper::toDTO)
                .toList();
    }


    public List<ProductDTO> getAllProductByState(int verifiedUserId, String state) throws ResourceNotFoundException {
        User verifiedUser = userService.getById(verifiedUserId);
        return productService.getAllProductByState(verifiedUser, Product.State.valueOf(state)).stream()
                .map(productMapper::toDTO)
                .toList();
    }
}
