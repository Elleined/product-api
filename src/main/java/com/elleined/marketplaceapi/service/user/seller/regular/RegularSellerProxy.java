package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.order.MaxOrderRejectionException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderPendingException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderAcceptedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.exception.user.seller.SellerMaxAcceptedOrderException;
import com.elleined.marketplaceapi.exception.user.seller.SellerMaxListingException;
import com.elleined.marketplaceapi.exception.user.seller.SellerMaxPendingOrderException;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import com.elleined.marketplaceapi.service.user.seller.fee.RegularSellerFeeService;
import com.elleined.marketplaceapi.service.user.seller.fee.SellerFeeService;
import com.elleined.marketplaceapi.service.user.seller.premium.PremiumSellerProxy;
import com.elleined.marketplaceapi.utils.Formatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import static com.elleined.marketplaceapi.service.user.seller.regular.RegularSellerRestriction.*;

@Service
@Slf4j
@Transactional
@Primary
public class RegularSellerProxy implements SellerService {
    private final SellerService sellerService;

    private final SellerFeeService sellerFeeService;

    private final ATMValidator atmValidator;

    private final RegularSellerRestriction regularSellerRestriction;

    private final RetailProductService retailProductService;

    private final FeeService feeService;


    public RegularSellerProxy(@Qualifier("sellerServiceImpl") SellerService sellerService,
                              SellerFeeService sellerFeeService, ATMValidator atmValidator, RegularSellerRestriction regularSellerRestriction,
                              RetailProductService retailProductService,
                              FeeService feeService) {
        this.sellerService = sellerService;
        this.sellerFeeService = sellerFeeService;
        this.atmValidator = atmValidator;
        this.regularSellerRestriction = regularSellerRestriction;
        this.retailProductService = retailProductService;
        this.feeService = feeService;
    }

    @Override
    public RetailProduct saleProduct(User seller, RetailProduct retailProduct, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return sellerService.saleProduct(seller, retailProduct, salePercentage);
    }

    @Override
    public WholeSaleProduct saleProduct(User seller, WholeSaleProduct wholeSaleProduct, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return sellerService.saleProduct(seller, wholeSaleProduct, salePercentage);
    }

    @Override
    public RetailProduct saveProduct(User seller, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        if (regularSellerRestriction.isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot save product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot save product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxListingPerDay(seller))
            throw new SellerMaxListingException("Cannot save product! because already reached the limit of product listing per day which is " + MAX_LISTING_PER_DAY + ". Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        double totalPrice = retailProductService.calculateTotalPrice(retailProductDTO.getPricePerUnit(), retailProductDTO.getQuantityPerUnit(), retailProductDTO.getAvailableQuantity());
        double listingFee = getListingFee(totalPrice);
        if (seller.isBalanceNotEnough(listingFee))
            throw new InsufficientBalanceException("Cannot save product! because you doesn't have enough balance to pay for the listing fee of " + Formatter.formatDouble(listingFee) + " which is " + RegularSellerFeeService.LISTING_FEE_PERCENTAGE + "%  of total price " + Formatter.formatDouble(totalPrice) + ". Consider buying premium account to remove listing fee.");
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller, new BigDecimal(listingFee)))
            throw new InsufficientFundException("Cannot save product! because you're balance cannot be less than in you're total pending withdraw request which. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        feeService.deductListingFee(seller, listingFee);

        return sellerService.saveProduct(seller, retailProductDTO, productPicture);
    }

    @Override
    public WholeSaleProduct saveProduct(User seller, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        double totalPrice = wholeSaleProductDTO.getTotalPrice();
        double listingFee = getListingFee(totalPrice);
        if (seller.isBalanceNotEnough(listingFee))
            throw new InsufficientBalanceException("Cannot save product! because you doesn't have enough balance to pay for the listing fee of " + Formatter.formatDouble(listingFee) + " which is " + LISTING_FEE_PERCENTAGE + "%  of total price " + Formatter.formatDouble(totalPrice) + ". Consider buying premium account to remove listing fee.");
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller, new BigDecimal(listingFee)))
            throw new InsufficientFundException("Cannot save product! because you're balance cannot be less than in you're total pending withdraw request which. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        feeService.deductListingFee(seller, listingFee);

        return sellerService.saveProduct(seller, wholeSaleProductDTO, productPicture);
    }

    @Override
    public RetailProduct updateProduct(User seller, RetailProduct retailProduct, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductOrderPendingException, ProductOrderAcceptedException, IOException {
        if (regularSellerRestriction.isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot update product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot update product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        double totalPrice = retailProductService.calculateTotalPrice(retailProductDTO.getPricePerUnit(), retailProductDTO.getQuantityPerUnit(), retailProductDTO.getAvailableQuantity());
        double listingFee = getListingFee(totalPrice);
        if (seller.isBalanceNotEnough(listingFee))
            throw new InsufficientBalanceException("Cannot update product! because you doesn't have enough balance to pay for the listing fee of " + Formatter.formatDouble(listingFee) + " which is " + LISTING_FEE_PERCENTAGE + "%  of total price " + Formatter.formatDouble(totalPrice) + ". Consider buying premium account to remove listing fee.");
        feeService.deductListingFee(seller, listingFee);

        return sellerService.updateProduct(seller, retailProduct, retailProductDTO, productPicture);
    }

    @Override
    public WholeSaleProduct updateProduct(User seller, WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductOrderPendingException, ProductOrderAcceptedException, IOException {
        if (regularSellerRestriction.isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot update product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot update product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        double totalPrice = wholeSaleProductDTO.getTotalPrice();
        double listingFee = getListingFee(totalPrice);
        if (seller.isBalanceNotEnough(listingFee))
            throw new InsufficientBalanceException("Cannot update product! because you doesn't have enough balance to pay for the listing fee of " + Formatter.formatDouble(listingFee) + " which is " + LISTING_FEE_PERCENTAGE + "%  of total price " + Formatter.formatDouble(totalPrice) + ". Consider buying premium account to remove listing fee.");
        feeService.deductListingFee(seller, listingFee);

        return sellerService.updateProduct(seller, wholeSaleProduct, wholeSaleProductDTO, productPicture);
    }

    @Override
    public void deleteProduct(User seller, RetailProduct retailProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductOrderAcceptedException, ProductOrderPendingException {
        if (regularSellerRestriction.isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot delete product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot delete product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.deleteProduct(seller, retailProduct);
    }

    @Override
    public void deleteProduct(User seller, WholeSaleProduct wholeSaleProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductOrderAcceptedException, ProductOrderPendingException {
        if (regularSellerRestriction.isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot delete product! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot delete product! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.deleteProduct(seller, wholeSaleProduct);
    }

    @Override
    public void acceptOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {
        if (regularSellerRestriction.isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot accept order! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.acceptOrder(seller, retailOrder, messageToBuyer);
    }

    @Override
    public void acceptOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {
        if (regularSellerRestriction.isExceedsToMaxAcceptedOrder(seller))
            throw new SellerMaxAcceptedOrderException("Cannot accept order! because you already exceeds to max accepted order which is " + MAX_ACCEPTED_ORDER + " please either reject the accepted order or set the accepted orders to sold to proceed. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.acceptOrder(seller, wholeSaleOrder, messageToBuyer);
    }

    @Override
    public void rejectOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        if (regularSellerRestriction.isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot reject order! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxRejectionPerDay(seller))
            throw new MaxOrderRejectionException("Cannot reject order! because you already reached the rejection limit per day which is " + MAX_ORDER_REJECTION_PER_DAY + " come back again tomorrow. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.rejectOrder(seller, retailOrder, messageToBuyer);
    }

    @Override
    public void rejectOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        if (regularSellerRestriction.isExceedsToMaxPendingOrder(seller))
            throw new SellerMaxPendingOrderException("Cannot reject order! because you already exceeds to max pending which is " + MAX_PENDING_ORDER + " please accept first some orders to proceed. Consider buying premium account to remove this restriction.");
        if (regularSellerRestriction.isExceedsToMaxRejectionPerDay(seller))
            throw new MaxOrderRejectionException("Cannot reject order! because you already reached the rejection limit per day which is " + MAX_ORDER_REJECTION_PER_DAY + " come back again tomorrow. Consider buying premium account to remove this restriction.");
        // Add more validation for regular seller here for future

        sellerService.rejectOrder(seller, wholeSaleOrder, messageToBuyer);
    }

    @Override
    public void soldOrder(User seller, RetailOrder retailOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        double orderPrice = retailOrder.getPrice();
        double successfulTransactionFee = getSuccessfulTransactionFee(orderPrice);
        if (seller.isBalanceNotEnough(successfulTransactionFee))
            throw new InsufficientBalanceException("Cannot sold order! because you doesn't have enough balance to pay for the successful transaction fee of " + successfulTransactionFee + " which is the " + SUCCESSFUL_TRANSACTION_FEE + "% of order total price of " + orderPrice + ". Consider buying premium account to lessen the successful transaction fee to" + PremiumSellerProxy.SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE);
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller, new BigDecimal(successfulTransactionFee)))
            throw new InsufficientFundException("Cannot order product! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        feeService.deductSuccessfulTransactionFee(seller, successfulTransactionFee);

        sellerService.soldOrder(seller, retailOrder);
    }

    @Override
    public void soldOrder(User seller, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        double orderPrice = wholeSaleOrder.getPrice();
        double successfulTransactionFee = getSuccessfulTransactionFee(orderPrice);
        if (seller.isBalanceNotEnough(successfulTransactionFee))
            throw new InsufficientBalanceException("Cannot sold order! because you doesn't have enough balance to pay for the successful transaction fee of " + successfulTransactionFee + " which is the " + SUCCESSFUL_TRANSACTION_FEE + "% of order total price of " + orderPrice + ". Consider buying premium account to lessen the successful transaction fee to" + PremiumSellerProxy.SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE);
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller, new BigDecimal(successfulTransactionFee)))
            throw new InsufficientFundException("Cannot order product! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        feeService.deductSuccessfulTransactionFee(seller, successfulTransactionFee);

        sellerService.soldOrder(seller, wholeSaleOrder);
    }

}
