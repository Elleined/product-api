package com.elleined.marketplaceapi.service.user.seller.premium;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@Qualifier("premiumSellerProxy")
public class PremiumSellerProxy implements SellerService {
    private final float SUCCESSFUL_TRANSACTION_FEE = 2;
    private final SellerService sellerService;
    private final FeeService feeService;

    public PremiumSellerProxy(@Qualifier("sellerServiceImpl") SellerService sellerService, FeeService feeService) {
        this.sellerService = sellerService;
        this.feeService = feeService;
    }

    @Override
    public Product saveProduct(ProductDTO productDTO, User seller)
            throws NotVerifiedException, ProductExpirationLimitException {
        // add validation for here for premium seller for future
        return sellerService.saveProduct(productDTO, seller);
    }

    @Override
    public void updateProduct(User seller, Product product, ProductDTO productDTO)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ResourceNotFoundException,
            ProductHasAcceptedOrderException,
            ProductHasPendingOrderException  {
        // add validation for here for premium seller for future
        sellerService.updateProduct(seller, product, productDTO);
    }

    @Override
    public void deleteProduct(User seller, Product product) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {
        // add validation for here for premium seller for future
        sellerService.deleteProduct(seller, product);
    }

    @Override
    public void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            ProductRejectedException,
            NotValidBodyException {
        sellerService.acceptOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        sellerService.rejectOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public void soldOrder(User seller, OrderItem orderItem) throws NotOwnedException, InsufficientBalanceException {
        double orderPrice = orderItem.getPrice();
        double successfulTransactionFee = getSuccessfulTransactionFee(orderPrice);
        if (isBalanceNotEnoughToPaySuccessfulTransactionFee(seller, successfulTransactionFee)) throw new InsufficientBalanceException("Seller with id of " + seller.getId() + " doesn't have enough balance to pay for the successful transaction fee of " + successfulTransactionFee + " which is the " + SUCCESSFUL_TRANSACTION_FEE + "% of order price of " + orderPrice);
        feeService.deductSuccessfulTransactionFee(seller, successfulTransactionFee);

        sellerService.soldOrder(seller, orderItem);
    }

    @Override
    public boolean isBalanceNotEnoughToPaySuccessfulTransactionFee(User seller, double successfulTransactionFee) {
        return sellerService.isBalanceNotEnoughToPaySuccessfulTransactionFee(seller, successfulTransactionFee);
    }

    @Override
    public List<Product> getAllProductByState(User seller, Product.State state) {
        return sellerService.getAllProductByState(seller, state);
    }

    @Override
    public List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus) {
        return sellerService.getAllSellerProductOrderByStatus(seller, orderItemStatus);
    }

    @Override
    public double getSuccessfulTransactionFee(double orderPrice) {
        return (orderPrice * (SUCCESSFUL_TRANSACTION_FEE / 100f));
    }
}
