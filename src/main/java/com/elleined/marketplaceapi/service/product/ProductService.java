package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order.Status;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public interface ProductService<T extends Product> {

    T getById(int productId) throws ResourceNotFoundException;

    // Use this to get all the product listing available
    List<T> getAllExcept(User currentUser);

    Set<T> getAllById(Set<Integer> productsToBeListedId);

    List<T> getAllByState(User seller, Product.State state);

    List<T> searchProductByCropName(String cropName);

    List<T> getByDateRange(User seller, LocalDateTime start, LocalDateTime end);

    void updateAllPendingAndAcceptedOrders(T t, Status status);

    boolean isRejectedBySeller(User buyer, T t);

    default int getSalePercentage(double totalPrice, double salePrice) {
        return (int) ((salePrice / totalPrice) * 100);
    }
}
