package com.elleined.marketplaceapi.service.product.retail;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.product.ProductRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RetailProductServiceImpl implements RetailProductService {
    private final RetailProductRepository retailProductRepository;

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    @Override
    public RetailProduct getById(int productId) throws ResourceNotFoundException {
        return retailProductRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Retail product with id of " + productId + " doesn't exists!"));
    }

    @Override
    public List<RetailProduct> getAllExcept(User currentUser) {
        return null;
    }

    @Override
    public Set<RetailProduct> getAllById(Set<Integer> productsToBeListedId) {
        return null;
    }

    @Override
    public void deleteExpiredProducts() {

    }

    @Override
    public List<RetailProduct> searchProductByCropName(String cropName) {
        return null;
    }

    @Override
    public double calculateOrderPrice(RetailProduct retailProduct, int userOrderQuantity) {
        return 0;
    }

    @Override
    public double calculateTotalPrice(double pricePerUnit, int quantityPerUnit, int availableQuantity) {
        return 0;
    }
}
