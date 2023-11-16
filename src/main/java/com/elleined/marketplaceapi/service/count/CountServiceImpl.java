package com.elleined.marketplaceapi.service.count;

import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import com.elleined.marketplaceapi.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountServiceImpl implements CountService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public int getAllUsersCount() {
        return userRepository.findAll().size();
    }

    @Override
    public int getAllUsersTransactionsCount() {
        return orderRepository.findAll().size();
    }

    @Override
    public int getAllProductCount() {
        return (int) productRepository.findAll().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() != Product.State.LISTING)
                .count();
    }
}
