package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.order.OrderReachedCancellingTimeLimitException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;

public interface BuyerService {

    RetailOrder order(User buyer, RetailOrderDTO retailOrderDTO)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductRejectedException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            BuyerAlreadyRejectedException,
            ProductExpiredException;

    WholeSaleOrder order(User buyer, WholeSaleOrderDTO wholeSaleOrderDTO)
            throws ResourceNotFoundException,
            ResourceOwnedException,
            ProductHasPendingOrderException,
            ProductHasAcceptedOrderException,
            ProductRejectedException,
            ProductAlreadySoldException,
            ProductNotListedException,
            OrderQuantiantyExceedsException,
            BuyerAlreadyRejectedException,
            ProductExpiredException;

    void cancelOrder(User buyer, RetailOrder retailOrder)
            throws NotOwnedException,
            OrderAlreadyAcceptedException,
            OrderReachedCancellingTimeLimitException,
            OrderAlreadyRejectedException;

    void cancelOrder(User buyer, WholeSaleOrder wholeSaleOrder)
            throws NotOwnedException,
            OrderAlreadyAcceptedException,
            OrderReachedCancellingTimeLimitException,
            OrderAlreadyRejectedException;
}
