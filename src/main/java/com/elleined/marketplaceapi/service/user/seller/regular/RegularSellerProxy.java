package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.dto.product.ProductDTO;
import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.order.MaxOrderRejectionException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.exception.user.seller.SellerMaxAcceptedOrderException;
import com.elleined.marketplaceapi.exception.user.seller.SellerMaxListingException;
import com.elleined.marketplaceapi.exception.user.seller.SellerMaxPendingOrderException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import com.elleined.marketplaceapi.repository.product.ProductRepository;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import com.elleined.marketplaceapi.service.user.seller.premium.PremiumSellerProxy;
import com.elleined.marketplaceapi.utils.Formatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@Transactional
@Primary
public class RegularSellerProxy implements SellerService, RegularSellerRestriction {
    public final static float LISTING_FEE_PERCENTAGE = 2;
    public final static float SUCCESSFUL_TRANSACTION_FEE = 2;

    private final SellerService sellerService;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;
    private final ProductService productService;

    private final FeeService feeService;

    public RegularSellerProxy(@Qualifier("sellerServiceImpl") SellerService sellerService,
                              OrderRepository orderRepository,
                              ProductRepository productRepository,
                              ProductService productService,
                              FeeService feeService) {
        this.sellerService = sellerService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.feeService = feeService;
    }

    @Override
    public boolean isExceedsToMaxListingPerDay(User seller) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        return productRepository.fetchSellerProductListingCount(
                currentDateTimeMidnight,
                tomorrowMidnight,
                seller
        ) >= MAX_LISTING_PER_DAY;
    }

    @Override
    public boolean isExceedsToMaxRejectionPerDay(User seller) {


        return orderRepository.fetchSellerRejectedOrderCount(
                currentDateTimeMidnight,
                tomorrowMidnight,
                seller,
                OrderItem.OrderItemStatus.REJECTED
        ) >= MAX_ORDER_REJECTION_PER_DAY;
    }

    @Override
    public boolean isExceedsToMaxAcceptedOrder(User seller) {
        return seller.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .map(Product::getOrders)
                .flatMap(Collection::stream)
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED)
                .count() >= MAX_ACCEPTED_ORDER;
    }

    @Override
    public boolean isExceedsToMaxPendingOrder(User seller) {
        return seller.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .map(Product::getOrders)
                .flatMap(Collection::stream)
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING)
                .count() >= MAX_PENDING_ORDER;
    }

    @Override
    public Product saleProduct(User seller, Product product, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        // Add more validation for regular seller here for future
        return sellerService.saleProduct(seller, product, salePercentage);
    }

    @Override
    public Product saveProduct(User seller, ProductDTO productDTO, MultipartFile productPicture)
            throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {

        if (isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot save product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot save product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        if (isExceedsToMaxListingPerDay(seller))
            throw new SellerMaxListingException("Cannot save product! because already reached the limit of product listing per day which is " + MAX_LISTING_PER_DAY + ". Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        double totalPrice = productService.calculateTotalPrice(productDTO.getPricePerUnit(), productDTO.getQuantityPerUnit(), productDTO.getAvailableQuantity());
        double listingFee = getListingFee(totalPrice);
        if (isBalanceNotEnoughToPayListingFee(seller, listingFee))
            throw new InsufficientBalanceException("Cannot save product! because you doesn't have enough balance to pay for the listing fee of " + Formatter.formatDouble(listingFee) + " which is " + LISTING_FEE_PERCENTAGE + "%  of total price " + Formatter.formatDouble(totalPrice) + ". Consider buying premium account to remove listing fee.");

        feeService.deductListingFee(seller, listingFee);
        return sellerService.saveProduct(seller, productDTO, productPicture);
    }

    @Override
    public void updateProduct(User seller, Product product, ProductDTO productDTO, MultipartFile productPicture)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ResourceNotFoundException,
            ProductHasAcceptedOrderException,
            ProductHasPendingOrderException,
            SellerMaxAcceptedOrderException,
            SellerMaxPendingOrderException, IOException {

        if (isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot update product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot update product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        double totalPrice = productService.calculateTotalPrice(productDTO.getPricePerUnit(), productDTO.getQuantityPerUnit(), productDTO.getAvailableQuantity());
        double listingFee = getListingFee(totalPrice);
        if (isBalanceNotEnoughToPayListingFee(seller, listingFee))
            throw new InsufficientBalanceException("Cannot update product! because you doesn't have enough balance to pay for the listing fee of " + Formatter.formatDouble(listingFee) + " which is " + LISTING_FEE_PERCENTAGE + "%  of total price " + Formatter.formatDouble(totalPrice) + ". Consider buying premium account to remove listing fee.");

        sellerService.updateProduct(seller, product, productDTO, productPicture);
    }

    @Override
    public void deleteProduct(User seller, Product product)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            SellerMaxAcceptedOrderException,
            SellerMaxPendingOrderException {

        if (isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot delete product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot delete product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.deleteProduct(seller, product);
    }

    @Override
    public void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException,
            ProductRejectedException,
            SellerMaxAcceptedOrderException {

        if (isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot accept order! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.acceptOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException,
            MaxOrderRejectionException,
            SellerMaxPendingOrderException {

        if (isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot reject order! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        if (isExceedsToMaxRejectionPerDay(seller))
            throw new MaxOrderRejectionException("Cannot reject order! because you already reached the rejection limit per day which is " + MAX_ORDER_REJECTION_PER_DAY + " come back again tomorrow. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.rejectOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public void soldOrder(User seller, OrderItem orderItem)
            throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {

        double orderPrice = orderItem.getPrice();
        double successfulTransactionFee = getSuccessfulTransactionFee(orderPrice);
        if (isBalanceNotEnoughToPaySuccessfulTransactionFee(seller, successfulTransactionFee))
            throw new InsufficientBalanceException("Cannot sold order! because you doesn't have enough balance to pay for the successful transaction fee of " + successfulTransactionFee + " which is the " + SUCCESSFUL_TRANSACTION_FEE + "% of order total price of " + orderPrice + ". Consider buying premium account to lessen the successful transaction fee to" + PremiumSellerProxy.SUCCESSFUL_TRANSACTION_FEE);
        feeService.deductSuccessfulTransactionFee(seller, successfulTransactionFee);

        sellerService.soldOrder(seller, orderItem);
    }

    private double getListingFee(double productTotalPrice) {
        return (productTotalPrice * (LISTING_FEE_PERCENTAGE / 100f));
    }

    private double getSuccessfulTransactionFee(double orderPrice) {
        return (orderPrice * (SUCCESSFUL_TRANSACTION_FEE / 100f));
    }
}
