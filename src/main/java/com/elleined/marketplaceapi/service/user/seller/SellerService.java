package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderPendingException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderAcceptedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SellerService {

    // not implemented
    RetailProduct saleProduct(User seller, RetailProduct retailProduct, int salePercentage)
            throws NotOwnedException,
            ProductSaleException,
            FieldException,
            ProductNotListedException;

    // not implemented
    WholeSaleProduct saleProduct(User seller, WholeSaleProduct wholeSaleProduct, int salePercentage)
            throws NotOwnedException,
            ProductSaleException,
            FieldException,
            ProductNotListedException;

    RetailProduct saveProduct(User seller, RetailProductDTO retailProductDTO, MultipartFile productPicture)
            throws NotVerifiedException,
            InsufficientFundException,
            ProductExpirationLimitException,
            IOException;

    WholeSaleProduct saveProduct(User seller, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture)
            throws NotVerifiedException,
            InsufficientFundException,
            ProductExpirationLimitException,
            IOException;

    RetailProduct updateProduct(User seller, RetailProduct retailProduct, RetailProductDTO retailProductDTO, MultipartFile productPicture)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ResourceNotFoundException,
            ProductOrderPendingException,
            ProductOrderAcceptedException, IOException;

    WholeSaleProduct updateProduct(User seller, WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ResourceNotFoundException,
            ProductOrderPendingException,
            ProductOrderAcceptedException, IOException;

    void deleteProduct(User seller, RetailProduct retailProduct)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ProductOrderAcceptedException,
            ProductOrderPendingException;

    void deleteProduct(User seller, WholeSaleProduct wholeSaleProduct)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ProductOrderAcceptedException,
            ProductOrderPendingException;

    void acceptOrder(User seller, RetailOrder retailOrder, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException,
            ProductRejectedException;

    void acceptOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException,
            ProductRejectedException;

    void rejectOrder(User seller, RetailOrder retailOrder, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException;

    void rejectOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException;

    void soldOrder(User seller, RetailOrder retailOrder)
            throws NotOwnedException,
            InsufficientFundException,
            InsufficientBalanceException;

    void soldOrder(User seller, WholeSaleOrder wholeSaleOrder)
            throws NotOwnedException,
            InsufficientFundException,
            InsufficientBalanceException;
}
