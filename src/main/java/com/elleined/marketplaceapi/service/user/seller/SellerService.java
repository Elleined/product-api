package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.dto.product.ProductDTO;
import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SellerService extends SellerTransactionFeeService {

    Product saleProduct(User seller, Product product, int salePercentage)
            throws NotOwnedException,
            ProductSaleException,
            FieldException,
            ProductNotListedException;

    Product saveProduct(User seller, ProductDTO productDTO, MultipartFile productPicture)
            throws NotVerifiedException,
            InsufficientFundException,
            ProductExpirationLimitException,
            IOException;

    void updateProduct(User seller, Product product, ProductDTO productDTO, MultipartFile productPicture)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ResourceNotFoundException,
            ProductHasAcceptedOrderException,
            ProductHasPendingOrderException, IOException;

    void deleteProduct(User seller, Product product)
            throws NotOwnedException,
            NotVerifiedException,
            ProductAlreadySoldException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException;

    void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException,
            ProductRejectedException;

    void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer)
            throws NotOwnedException,
            NotValidBodyException;

    void soldOrder(User seller, OrderItem orderItem)
            throws NotOwnedException,
            InsufficientFundException,
            InsufficientBalanceException;
}
