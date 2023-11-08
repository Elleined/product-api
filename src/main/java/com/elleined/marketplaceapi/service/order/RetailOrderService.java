package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RetailOrderService implements OrderService<RetailOrder> {
    private final RetailOrderRepository retailOrderRepository;

    @Override
    public List<RetailOrder> getAllProductOrderByStatus(User seller, Order.Status orderStatus) {
        return null;
    }

    @Override
    public RetailOrder getById(int id) throws ResourceNotFoundException {
        return retailOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Retail order with id of " + id + " does not exists!"));
    }

}
