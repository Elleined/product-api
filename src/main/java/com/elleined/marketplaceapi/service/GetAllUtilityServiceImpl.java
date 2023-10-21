package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.product.CropService;
import com.elleined.marketplaceapi.service.product.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUtilityServiceImpl implements GetAllUtilityService {
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    private final CropService cropService;
    private final UnitService unitService;


    @Override
    public List<String> getAllEmail() {
        return userRepository.fetchAllEmail();
    }

    @Override
    public List<String> getAllMobileNumber() {
        return userRepository.fetchAllMobileNumber();
    }

    @Override
    public List<String> getAllGender() {
        return Arrays.stream(UserDetails.Gender.values())
                .map(Enum::name)
                .sorted()
                .toList();
    }

    @Override
    public List<String> getAllSuffix() {
        return Arrays.stream(UserDetails.Suffix.values())
                .map(Enum::name)
                .sorted()
                .toList();
    }

    @Override
    public int getAllUsersCount() {
        return userRepository.findAll().size();
    }

    @Override
    public int getAllUsersTransactionsCount() {
        return orderItemRepository.findAll().size();
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
    public int getAllProductCount() {
        return (int) productRepository.findAll().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() != Product.State.LISTING)
                .count();
    }
}
