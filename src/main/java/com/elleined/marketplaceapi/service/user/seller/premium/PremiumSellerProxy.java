package com.elleined.marketplaceapi.service.user.seller.premium;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderAcceptedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderPendingException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.atm.machine.validator.ATMValidator;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import com.elleined.marketplaceapi.service.user.seller.fee.PremiumSellerFeeService;
import com.elleined.marketplaceapi.service.user.seller.fee.SellerFeeService;
import com.elleined.marketplaceapi.utils.Formatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@Slf4j
@Transactional
@Qualifier("premiumSellerProxy")
public class PremiumSellerProxy implements SellerService {

    private final SellerFeeService sellerFeeService;
    private final SellerService sellerService;
    private final FeeService feeService;
    private final ATMValidator atmValidator;

    public PremiumSellerProxy(@Qualifier("premiumSellerFeeService") SellerFeeService sellerFeeService,
                              @Qualifier("sellerServiceImpl") SellerService sellerService,
                              FeeService feeService, ATMValidator atmValidator) {
        this.sellerFeeService = sellerFeeService;
        this.sellerService = sellerService;
        this.feeService = feeService;
        this.atmValidator = atmValidator;
    }

    @Override
    public RetailProduct saleProduct(User seller, RetailProduct retailProduct, int quantityPerUnit, int pricePerUnit) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return sellerService.saleProduct(seller, retailProduct, quantityPerUnit, pricePerUnit);
    }

    @Override
    public WholeSaleProduct saleProduct(User seller, WholeSaleProduct wholeSaleProduct, BigDecimal salePrice) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return sellerService.saleProduct(seller, wholeSaleProduct, salePrice);
    }

    @Override
    public RetailProduct saveProduct(User seller, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        return sellerService.saveProduct(seller, retailProductDTO, productPicture);
    }

    @Override
    public WholeSaleProduct saveProduct(User seller, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        return sellerService.saveProduct(seller, wholeSaleProductDTO, productPicture);
    }

    @Override
    public RetailProduct updateProduct(User seller, RetailProduct retailProduct, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductOrderPendingException, ProductOrderAcceptedException, IOException {
        return sellerService.updateProduct(seller, retailProduct, retailProductDTO, productPicture);
    }

    @Override
    public WholeSaleProduct updateProduct(User seller, WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductOrderPendingException, ProductOrderAcceptedException, IOException {
        return sellerService.updateProduct(seller, wholeSaleProduct, wholeSaleProductDTO, productPicture);
    }

    @Override
    public void deleteProduct(User seller, RetailProduct retailProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductOrderAcceptedException, ProductOrderPendingException {
        sellerService.deleteProduct(seller, retailProduct);
    }

    @Override
    public void deleteProduct(User seller, WholeSaleProduct wholeSaleProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductOrderAcceptedException, ProductOrderPendingException {
        sellerService.deleteProduct(seller, wholeSaleProduct);
    }

    @Override
    public void acceptOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {
        sellerService.acceptOrder(seller, retailOrder, messageToBuyer);
    }

    @Override
    public void acceptOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {
        sellerService.acceptOrder(seller, wholeSaleOrder, messageToBuyer);
    }

    @Override
    public void rejectOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        sellerService.rejectOrder(seller, retailOrder, messageToBuyer);
    }

    @Override
    public void rejectOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        sellerService.rejectOrder(seller, wholeSaleOrder, messageToBuyer);
    }

    @Override
    public void soldOrder(User seller, RetailOrder retailOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        double orderPrice = retailOrder.getPrice();
        double successfulTransactionFee = sellerFeeService.getSuccessfulTransactionFee(orderPrice);
        if (seller.isBalanceNotEnough(successfulTransactionFee))
            throw new InsufficientBalanceException("You cannot complete the sale of this order because you do not have a sufficient balance to cover the successful transaction fee. The fee amounts to " + Formatter.formatDouble(successfulTransactionFee) + ", which is " + PremiumSellerFeeService.SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE + "% of the order's total price of " + Formatter.formatDouble(orderPrice));
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller, new BigDecimal(successfulTransactionFee)))
            throw new InsufficientFundException("Cannot order product! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        feeService.deductSuccessfulTransactionFee(seller, successfulTransactionFee);

        sellerService.soldOrder(seller, retailOrder);
    }

    @Override
    public void soldOrder(User seller, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        double orderPrice = wholeSaleOrder.getPrice();
        double successfulTransactionFee = sellerFeeService.getSuccessfulTransactionFee(orderPrice);
        if (seller.isBalanceNotEnough(successfulTransactionFee))
            throw new InsufficientBalanceException("You cannot complete the sale of this order because you do not have a sufficient balance to cover the successful transaction fee. The fee amounts to " + Formatter.formatDouble(successfulTransactionFee) + ", which is " + PremiumSellerFeeService.SUCCESSFUL_TRANSACTION_FEE_PERCENTAGE + "% of the order's total price of " + Formatter.formatDouble(orderPrice));
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller, new BigDecimal(successfulTransactionFee)))
            throw new InsufficientFundException("Cannot order product! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        feeService.deductSuccessfulTransactionFee(seller, successfulTransactionFee);

        sellerService.soldOrder(seller, wholeSaleOrder);
    }

}
