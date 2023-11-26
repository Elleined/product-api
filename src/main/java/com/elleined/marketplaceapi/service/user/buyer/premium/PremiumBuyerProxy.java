package com.elleined.marketplaceapi.service.user.buyer.premium;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.order.OrderReachedCancellingTimeLimitException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductExpiredException;
import com.elleined.marketplaceapi.exception.product.ProductNotListedException;
import com.elleined.marketplaceapi.exception.product.ProductRejectedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderAcceptedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderPendingException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@Qualifier("premiumBuyerProxy")
public class PremiumBuyerProxy implements BuyerService {
    private final BuyerService buyerService;

    public PremiumBuyerProxy(@Qualifier("buyerServiceImpl") BuyerService buyerService) {
        this.buyerService = buyerService;
    }
    @Override
    public RetailOrder order(User buyer, RetailOrderDTO retailOrderDTO) throws ResourceNotFoundException, ResourceOwnedException, ProductOrderAcceptedException, ProductOrderPendingException, ProductRejectedException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException, ProductExpiredException {
        return buyerService.order(buyer, retailOrderDTO);
    }

    @Override
    public WholeSaleOrder order(User buyer, WholeSaleOrderDTO wholeSaleOrderDTO) throws ResourceNotFoundException, ResourceOwnedException, ProductOrderAcceptedException, ProductOrderPendingException, ProductRejectedException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException, ProductExpiredException {
        return buyerService.order(buyer, wholeSaleOrderDTO);
    }

    @Override
    public void cancelOrder(User buyer, RetailOrder retailOrder) throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {
        buyerService.cancelOrder(buyer, retailOrder);
    }

    @Override
    public void cancelOrder(User buyer, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {
        buyerService.cancelOrder(buyer, wholeSaleOrder);
    }
}
