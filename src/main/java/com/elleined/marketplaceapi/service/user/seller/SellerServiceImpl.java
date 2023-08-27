package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductHasAcceptedOrderException;
import com.elleined.marketplaceapi.exception.product.ProductHasPendingOrderException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.service.product.CropService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.product.UnitService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("sellerServiceImpl")
public class SellerServiceImpl implements SellerService, SellerOrderChecker {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final CropService cropService;
    private final UnitService unitService;

    private final OrderItemRepository orderItemRepository;

    @Override
    public Product saveProduct(ProductDTO productDTO, User seller) throws NotVerifiedException {
        if (!seller.isVerified()) throw new NotVerifiedException("Cannot update a product because user with id of " + seller.getId() + " are not yet been verified! Consider register shop first then get verified afterwards");
        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());

        Product product = productMapper.toEntity(productDTO, seller);
        productRepository.save(product);
        log.debug("Product saved successfully with id of {}", product.getId());
        return product;
    }

    @Override
    public void updateProduct(User seller, Product product, ProductDTO productDTO)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ResourceNotFoundException {

        if (!seller.isVerified()) throw new NotVerifiedException("Cannot update a product because user with id of " + seller.getId() + " are not yet been verified! Consider register shop first then get verified afterwards");
        if (!seller.hasProduct(product))  throw new NotOwnedException("Seller user with id of " + seller.getId() + " does not have product with id of " + product.getId());
        if (product.isSold()) throw new ProductAlreadySoldException("Cannot update this product with id of " + product.getId() + " because this product is already sold");
        if (product.isDeleted()) throw new ResourceNotFoundException("Product with id of " + product.getId() + " does not exists or might already been deleted!");

        if (product.isCriticalFieldsChanged(productDTO)) product.setState(Product.State.PENDING);
        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        Product updatedProduct = productMapper.toUpdate(product, productDTO);
        productRepository.save(updatedProduct);
        log.debug("Product with id of {} updated successfully!", updatedProduct.getId());
    }

    @Override
    public void deleteProduct(User seller, Product product)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException {

        if (!seller.isVerified()) throw new NotVerifiedException("Cannot update a product because user with id of " + seller.getId() + " are not yet been verified! Consider register shop first then get verified afterwards");
        if (!seller.hasProduct(product)) throw new NotOwnedException("Seller user with id of " + seller.getId() + " does not have product with id of " + product.getId());
        if (product.isSold()) throw new ProductAlreadySoldException("Cannot update this product with id of " + product.getId() + " because this product is already sold");
        if (product.hasPendingOrder()) throw new ProductHasPendingOrderException("Cannot delete this product! Because product with id of " + product.getId() + " has a pending orders. Please settle first the pending products to delete this");
        if (product.hasAcceptedOrder()) throw new ProductHasAcceptedOrderException("Cannot delete this product! Because product with id of " + product.getId() + " has a pending orders. Please settle first the accepted products to delete this");

        product.setStatus(Product.Status.INACTIVE);
        List<OrderItem> orderItems = product.getOrders();
        updatePendingAndAcceptedOrderStatus(orderItems, OrderItem.OrderItemStatus.CANCELLED);

        productRepository.save(product);
        orderItemRepository.saveAll(orderItems);
        log.debug("Product with id of {} are now inactive", product.getId());
    }

    @Override
    public void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException {

        if (!isSellerHasOrder(seller, orderItem)) throw new NotOwnedException("Seller with id of " + seller.getId() + " doesn't have order with id of " + orderItem.getId());
        if (StringUtil.isNotValid(messageToBuyer)) throw new NotValidBodyException("Please provide a message for the buyer... can be anything thanks");

        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.ACCEPTED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setSellerMessage(messageToBuyer);

        orderItemRepository.save(orderItem);
        log.debug("Seller successfully updated order item with id of {} status from {} to {}", orderItem.getId(), oldStatus, OrderItem.OrderItemStatus.ACCEPTED.name());
    }


    @Override
    public void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException {

        if (!isSellerHasOrder(seller, orderItem)) throw new NotOwnedException("Seller with id of " + seller.getId() + " doesn't have order with id of " + orderItem.getId());
        if (StringUtil.isNotValid(messageToBuyer)) throw new NotValidBodyException("Please provide a message for the buyer... can be anything thanks");

        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.REJECTED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setSellerMessage(messageToBuyer);

        orderItemRepository.save(orderItem);
        log.debug("Seller successfully updated order item with id of {} status from {} to {}", orderItem.getId(), oldStatus, OrderItem.OrderItemStatus.REJECTED.name());
    }

    @Override
    public void soldOrder(User seller, OrderItem orderItem) throws NotOwnedException {
        if (!isSellerHasOrder(seller, orderItem)) throw new NotOwnedException("Seller with id of " + seller.getId() + " doesn't have an order item with id of " + orderItem.getId());

        Product product = orderItem.getProduct();
        product.setState(Product.State.SOLD);
        productRepository.save(product);

        List<OrderItem> orderItems = product.getOrders();
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.SOLD);
        updatePendingAndAcceptedOrderStatus(orderItems, OrderItem.OrderItemStatus.SOLD);

        orderItemRepository.save(orderItem);
        orderItemRepository.saveAll(orderItems);
    }

    @Override
    public boolean isBalanceNotEnoughToPaySuccessfulTransactionFee(User seller, double successfulTransactionFee) {
        return seller.getBalance().compareTo(new BigDecimal(successfulTransactionFee)) <= 0;
    }

    @Override
    public double getSuccessfulTransactionFee(double orderItemPrice) {
        return 1000; // HAHAHA don't use this implementation
    }

    @Override
    public List<Product> getAllProductByState(User seller, Product.State state) {
        return seller.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == state)
                .toList();
    }

    @Override
    public List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus) {
        return seller.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .flatMap(product -> product.getOrders().stream()
                        .filter(productOrder -> productOrder.getOrderItemStatus() == orderItemStatus)
                        .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed()))
                .toList();
    }

    @Override
    public boolean isSellerHasOrder(User seller, OrderItem orderItem) {
        return seller.getProducts().stream()
                .map(Product::getOrders)
                .flatMap(Collection::stream)
                .anyMatch(orderItem::equals);
    }

    private void updatePendingAndAcceptedOrderStatus(List<OrderItem> orderItems, OrderItem.OrderItemStatus orderItemStatus) {
        List<OrderItem> pendingOrders = orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING)
                .toList();

        List<OrderItem> acceptedOrders = orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED)
                .toList();

        pendingOrders.forEach(orderItem -> {
            orderItem.setOrderItemStatus(orderItemStatus);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
        acceptedOrders.forEach(orderItem -> {
            orderItem.setOrderItemStatus(orderItemStatus);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });

        log.debug("Pending order items with ids {} are set to {}", pendingOrders.stream().map(OrderItem::getId).toList(), orderItemStatus);
        log.debug("Accepted order items with ids {} are set to {}", acceptedOrders.stream().map(OrderItem::getId).toList(), orderItemStatus);
    }
}
