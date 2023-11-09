package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
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
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.order.OrderService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
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
import java.util.List;

@Service
@Slf4j
@Transactional
@Primary
public class RegularSellerProxy implements SellerService, RegularSellerRestriction {
    public final static float LISTING_FEE_PERCENTAGE = 2;
    public final static float SUCCESSFUL_TRANSACTION_FEE = 2;

    private final SellerService sellerService;

    private final OrderRepository orderRepository;

    private final RetailProductRepository retailProductRepository;
    private final WholeSaleProductRepository wholeSaleProductRepository;

    private final ProductService<WholeSaleProduct> wholeSaleProductService;
    private final ProductService<RetailProduct> retailProductService;

    private final FeeService feeService;


    public RegularSellerProxy(@Qualifier("sellerServiceImpl") SellerService sellerService,
                              OrderRepository orderRepository,
                              RetailProductRepository retailProductRepository,
                              WholeSaleProductRepository wholeSaleProductRepository,
                              ProductService<WholeSaleProduct> wholeSaleProductService,
                              ProductService<RetailProduct> retailProductService,
                              FeeService feeService) {

        this.sellerService = sellerService;
        this.orderRepository = orderRepository;
        this.retailProductRepository = retailProductRepository;
        this.wholeSaleProductRepository = wholeSaleProductRepository;
        this.wholeSaleProductService = wholeSaleProductService;
        this.retailProductService = retailProductService;
        this.feeService = feeService;
    }

    @Override
    public RetailProduct saleProduct(User seller, RetailProduct retailProduct, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return null;
    }

    @Override
    public WholeSaleProduct saleProduct(User seller, WholeSaleProduct wholeSaleProduct, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return null;
    }

    @Override
    public RetailProduct saveProduct(User seller, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {

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
    public WholeSaleProduct saveProduct(User seller, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        return null;
    }

    @Override
    public void updateProduct(User seller, RetailProduct retailProduct, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {

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
    public void updateProduct(User seller, WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {

    }

    @Override
    public void deleteProduct(User seller, RetailProduct retailProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {

        if (isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot delete product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot delete product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.deleteProduct(seller, product);
    }

    @Override
    public void deleteProduct(User seller, WholeSaleProduct wholeSaleProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {

    }

    @Override
    public void acceptOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {
        if (isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot accept order! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.acceptOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public void acceptOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {

    }

    @Override
    public void rejectOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        if (isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot reject order! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        if (isExceedsToMaxRejectionPerDay(seller))
            throw new MaxOrderRejectionException("Cannot reject order! because you already reached the rejection limit per day which is " + MAX_ORDER_REJECTION_PER_DAY + " come back again tomorrow. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.rejectOrder(seller, orderItem, messageToBuyer);
    }

    @Override
    public void rejectOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {

    }

    @Override
    public void soldOrder(User seller, RetailOrder retailOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        double orderPrice = orderItem.getPrice();
        double successfulTransactionFee = getSuccessfulTransactionFee(orderPrice);
        if (isBalanceNotEnoughToPaySuccessfulTransactionFee(seller, successfulTransactionFee))
            throw new InsufficientBalanceException("Cannot sold order! because you doesn't have enough balance to pay for the successful transaction fee of " + successfulTransactionFee + " which is the " + SUCCESSFUL_TRANSACTION_FEE + "% of order total price of " + orderPrice + ". Consider buying premium account to lessen the successful transaction fee to" + PremiumSellerProxy.SUCCESSFUL_TRANSACTION_FEE);
        feeService.deductSuccessfulTransactionFee(seller, successfulTransactionFee);

        sellerService.soldOrder(seller, orderItem);
    }

    @Override
    public void soldOrder(User seller, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {

    }

    @Override
    public boolean isExceedsToMaxListingPerDay(User seller) {
        LocalDateTime todayMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<RetailProduct> createdRetailProductToday = retailProductService.getByDateRange(seller, todayMidnight, tomorrowMidnight);
        List<WholeSaleProduct> createdWholeSaleProductToday = wholeSaleProductService.getByDateRange(seller, todayMidnight, tomorrowMidnight);

        int totalCreatedProductsToday = createdRetailProductToday.size() + createdWholeSaleProductToday.size();
        return totalCreatedProductsToday >= MAX_LISTING_PER_DAY;
    }

    @Override
    public boolean isExceedsToMaxRejectionPerDay(User seller) {
        LocalDateTime todayMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<RetailOrder> rejectedRetailOrderByDateRange = OrderService.getByDateRange(seller.getRetailOrders(), Order.Status.REJECTED, todayMidnight, tomorrowMidnight);
        List<WholeSaleOrder> rejectedWholeSaleOrderByDateRange = OrderService.getByDateRange(seller.getWholeSaleOrders(), Order.Status.REJECTED, todayMidnight, tomorrowMidnight);

        int totalRejectedOrdersToday = rejectedRetailOrderByDateRange.size() + rejectedWholeSaleOrderByDateRange.size();
        return totalRejectedOrdersToday >= MAX_ORDER_REJECTION_PER_DAY;
    }

    @Override
    public boolean isExceedsToMaxAcceptedOrder(User seller) {
        int retailAcceptedOrderCount = (int) seller.getRetailProducts().stream()
                .filter(Product::isNotDeleted)
                .map(RetailProduct::getRetailOrders)
                .flatMap(Collection::stream)
                .filter(RetailOrder::isAccepted)
                .count();

        int wholeSaleAcceptedOrderCount = (int) seller.getWholeSaleProducts().stream()
                .filter(Product::isNotDeleted)
                .map(WholeSaleProduct::getWholeSaleOrders)
                .flatMap(Collection::stream)
                .filter(WholeSaleOrder::isAccepted)
                .count();

        int totalAcceptedOrder = retailAcceptedOrderCount + wholeSaleAcceptedOrderCount;
        return totalAcceptedOrder >= MAX_ACCEPTED_ORDER;
    }

    @Override
    public boolean isExceedsToMaxPendingOrder(User seller) {
        int retailPendingOrderCount = (int) seller.getRetailProducts().stream()
                .filter(Product::isNotDeleted)
                .map(RetailProduct::getRetailOrders)
                .flatMap(Collection::stream)
                .filter(RetailOrder::isPending)
                .count();

        int wholeSalePendingOrderCount = (int) seller.getWholeSaleProducts().stream()
                .filter(Product::isNotDeleted)
                .map(WholeSaleProduct::getWholeSaleOrders)
                .flatMap(Collection::stream)
                .filter(WholeSaleOrder::isPending)
                .count();

        int totalPendingOrder = retailPendingOrderCount + wholeSalePendingOrderCount;
        return totalPendingOrder >= MAX_PENDING_ORDER;
    }

    private double getListingFee(double productTotalPrice) {
        return (productTotalPrice * (LISTING_FEE_PERCENTAGE / 100f));
    }

    private double getSuccessfulTransactionFee(double orderPrice) {
        return (orderPrice * (SUCCESSFUL_TRANSACTION_FEE / 100f));
    }
}
