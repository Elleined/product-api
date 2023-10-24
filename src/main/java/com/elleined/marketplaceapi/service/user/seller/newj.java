package com.elleined.marketplaceapi.service.user.seller;

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
import com.elleined.marketplaceapi.mapper.product.ProductMapper;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.atm.machine.ATMValidator;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.message.prv.PrivateChatRoomService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.unit.RetailUnitService;
import com.elleined.marketplaceapi.service.unit.WholeSaleUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("sellerServiceImpl")
public class newj implements SellerService {
    public static final int DAY_RANGE = 14;

    private final PrivateChatRoomService privateChatRoomService;

    private final RetailProductRepository retailProductRepository;
    private final WholeSaleProductRepository wholeSaleProductRepository;
    private final RetailProductService retailProductService;
    private final WholeSaleProductService wholeSaleProductService;
    private final ProductMapper productMapper;

    private final ImageUploader imageUploader;

    private final CropService cropService;
    private final RetailUnitService retailUnitService;
    private final WholeSaleUnitService wholeSaleUnitService;

    private final OrderRepository orderRepository;

    private final ATMValidator atmValidator;

    @Override
    public RetailProduct saleProduct(User seller, RetailProduct retailProduct, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        if (salePercentage <= 0) throw new FieldException("Cannot sale this product! Sale percentage must be a positive value. Please ensure that the sale percentage is greater than 0.");
        if (!seller.hasProduct(retailProduct)) throw new NotOwnedException("Cannot sale this product! because You do not have ownership rights to update this product. Only the owner of the product can make changes.");
        if (!retailProduct.isListed()) throw new ProductNotListedException("Cannot sale this product! because you are trying to perform an action on a product that has not been listed in our system. This action is not permitted for products that are not yet listed.");

        double totalPrice = retailProductService.calculateTotalPrice(retailProduct);
        double salePrice = (totalPrice * (salePercentage / 100f));
        if (salePrice >= totalPrice) throw new ProductSaleException("Cannot sale this product! the sale price " + salePrice + " you've entered does not result in a lower price than the previous price " + totalPrice + " after applying the specified sale percentage " + salePercentage + ". When setting a sale price, it should be lower than the original price to qualify as a discount.\nPlease enter a sale price that, after applying the sale percentage " + salePercentage + ", is lower than the previous price to apply a valid discount.");


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
    public void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {

    }

    @Override
    public void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer) throws NotOwnedException, NotValidBodyException {

    }

    @Override
    public void soldOrder(User seller, OrderItem orderItem) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {

    }

    @Override
    public boolean isBalanceNotEnoughToPaySuccessfulTransactionFee(User seller, double successfulTransactionFee) {
        return false;
    }

    @Override
    public double getSuccessfulTransactionFee(double orderItemPrice) {
        return 0;
    }
}
