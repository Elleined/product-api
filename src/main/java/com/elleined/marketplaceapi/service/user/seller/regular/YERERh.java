package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
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
public class YERERh implements SellerService, RegularSellerRestriction {
    public final static float LISTING_FEE_PERCENTAGE = 2;
    public final static float SUCCESSFUL_TRANSACTION_FEE = 2;

    private final SellerService sellerService;

    private final OrderRepository orderRepository;

    private final RetailProductRepository retailProductRepository;
    private final WholeSaleProductRepository wholeSaleProductRepository;

    private final ProductService<WholeSaleProduct> wholeSaleProductService;
    private final ProductService<RetailProduct> retailProductService;

    private final FeeService feeService;


    public YERERh(@Qualifier("sellerServiceImpl") SellerService sellerService,
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
        return null;
    }

    @Override
    public WholeSaleProduct saveProduct(User seller, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        return null;
    }

    @Override
    public void updateProduct(User seller, RetailProduct retailProduct, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {

    }

    @Override
    public void updateProduct(User seller, WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {

    }

    @Override
    public void deleteProduct(User seller, RetailProduct retailProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {

    }

    @Override
    public void deleteProduct(User seller, WholeSaleProduct wholeSaleProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {

    }

    @Override
    public void acceptOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {

    }

    @Override
    public void acceptOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {

    }

    @Override
    public void rejectOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {

    }

    @Override
    public void rejectOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {

    }

    @Override
    public void soldOrder(User seller, RetailOrder retailOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {

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


        return false;
    }

    @Override
    public boolean isExceedsToMaxRejectionPerDay(User seller) {
        LocalDateTime todayMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<RetailOrder> rejectedRetailOrderByDateRange = OrderService.getOrdersByDateRange(seller.getRetailOrders(), todayMidnight, tomorrowMidnight).stream()
                .filter(Order::isRejected)
                .toList();

        List<WholeSaleOrder> rejectedWholeSaleOrderByDateRange = OrderService.getOrdersByDateRange(seller.getWholeSaleOrders(), todayMidnight, tomorrowMidnight).stream()
                .filter(Order::isRejected)
                .toList();

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
}
