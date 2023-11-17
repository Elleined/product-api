package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyRejectedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.order.OrderReachedCancellingTimeLimitException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderPendingException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderAcceptedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.order.RetailOrderMapper;
import com.elleined.marketplaceapi.mapper.order.WholeSaleOrderMapper;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.elleined.marketplaceapi.model.order.Order.Status.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("buyerServiceImpl")
public class BuyerServiceImpl implements BuyerService {
    private final RetailProductService retailProductService;
    private final ProductService<WholeSaleProduct> wholeSaleProductService;

    private final WholeSaleOrderRepository wholeSaleOrderRepository;
    private final RetailOrderRepository retailOrderRepository;

    private final RetailOrderMapper retailOrderMapper;
    private final WholeSaleOrderMapper wholeSaleOrderMapper;

    private final AddressService addressService;

    @Override
    public RetailOrder order(User buyer, RetailOrderDTO dto) throws ResourceNotFoundException, ResourceOwnedException, ProductOrderAcceptedException, ProductOrderPendingException, ProductRejectedException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException, ProductExpiredException {
        RetailProduct retailProduct = retailProductService.getById(dto.getProductId());

        if (retailProduct.isExpired())
            throw new ProductExpiredException("Cannot order this retail product! because this retail product is already expired!");
        if (retailProduct.isRejected())
            throw new ProductRejectedException("Cannot order this retail product! because this retail product is rejected by moderator!");
        if (buyer.hasOrder(retailProduct, PENDING))
            throw new ProductOrderAcceptedException("Cannot order this retail product! because you already have pending order. Please wait until seller take action in you order request!");
        if (buyer.hasOrder(retailProduct, ACCEPTED))
            throw new ProductOrderPendingException("Cannot order this retail product! because you already have accepted order. Please contact the seller to settle your order");
        if (retailProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot order this retail product! because this retail product does not exists or might already been deleted!");
        if (retailProduct.isSold())
            throw new ProductAlreadySoldException("Cannot order this retail product! because this retail product are already been sold!");
        if (!retailProduct.isListed())
            throw new ProductNotListedException("Cannot order this retail product! because this retail product are not yet listed!");
        if (buyer.hasProduct(retailProduct))
            throw new ResourceOwnedException("You cannot order your own retail product listing!");
        if (retailProduct.isExceedingToAvailableQuantity(dto.getOrderQuantity()))
            throw new OrderQuantiantyExceedsException("Cannot order this retail product! because you are trying to order that exceeds to available amount!");
        if (retailProductService.isRejectedBySeller(buyer, retailProduct))
            throw new BuyerAlreadyRejectedException("Cannot order this retail product! because seller of this retail product is rejected you order request before!. Please wait after 1 day to re-oder this product");

        double price = retailProductService.calculateOrderPrice(retailProduct, dto.getOrderQuantity());
        DeliveryAddress deliveryAddress = addressService.getDeliveryAddressById(buyer, dto.getDeliveryAddressId());
        RetailOrder retailOrder = retailOrderMapper.toEntity(dto, buyer, deliveryAddress, price, retailProduct);

        buyer.getRetailOrders().add(retailOrder);
        retailOrderRepository.save(retailOrder);
        log.debug("User with id of {} successfully ordered product with id of {}", buyer.getId(), retailProduct.getId());
        return retailOrder;
    }

    @Override
    public WholeSaleOrder order(User buyer, WholeSaleOrderDTO dto) throws ResourceNotFoundException, ResourceOwnedException, ProductOrderAcceptedException, ProductOrderPendingException, ProductRejectedException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException, ProductExpiredException{
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(dto.getProductId());

        if (wholeSaleProduct.isRejected())
            throw new ProductRejectedException("Cannot order this whole sale product! because this whole sale product is rejected by moderator!");
        if (buyer.hasOrder(wholeSaleProduct, PENDING))
            throw new ProductOrderAcceptedException("Cannot order this whole sale product! because you already have pending order. Please wait until seller take action in you order request!");
        if (buyer.hasOrder(wholeSaleProduct, ACCEPTED))
            throw new ProductOrderPendingException("Cannot order this whole sale product! because you already have accepted order. Please contact the seller to settle your order");
        if (wholeSaleProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot order this whole sale product! because this whole sale product does not exists or might already been deleted!");
        if (wholeSaleProduct.isSold())
            throw new ProductAlreadySoldException("Cannot order this whole sale product! because this whole sale product are already been sold!");
        if (!wholeSaleProduct.isListed())
            throw new ProductNotListedException("Cannot order this whole sale product! because this whole sale product are not yet listed!");
        if (buyer.hasProduct(wholeSaleProduct))
            throw new ResourceOwnedException("You cannot order your own whole sale product listing!");
        if (wholeSaleProductService.isRejectedBySeller(buyer, wholeSaleProduct))
            throw new BuyerAlreadyRejectedException("Cannot order this whole sale product! because seller of this whole sale product is rejected you order request before!. Please wait after 1 day to re-oder this product");

        DeliveryAddress deliveryAddress = addressService.getDeliveryAddressById(buyer, dto.getDeliveryAddressId());
        WholeSaleOrder wholeSaleOrder = wholeSaleOrderMapper.toEntity(dto, buyer, deliveryAddress, wholeSaleProduct);

        buyer.getWholeSaleOrders().add(wholeSaleOrder);
        wholeSaleOrderRepository.save(wholeSaleOrder);
        log.debug("User with id of {} successfully ordered product with id of {}", buyer.getId(), wholeSaleProduct.getId());
        return wholeSaleOrder;
    }

    @Override
    public void cancelOrder(User buyer, RetailOrder retailOrder) throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {
        if (!buyer.hasOrder(retailOrder))
            throw new NotOwnedException("Cannot cancel order! because you don't owned this order");
        if (retailOrder.isAccepted())
            throw new OrderAlreadyAcceptedException("Cannot cancel order! because seller already accepted your order request for this product!");
        if (retailOrder.reachedCancellingTimeLimit())
            throw new OrderReachedCancellingTimeLimitException("Cannot cancel order! because orders can only be canceled within the first 24 hours from the time of purchase. This order has exceeded the cancellation window.");

        retailOrder.setStatus(CANCELLED);
        retailOrder.setUpdatedAt(LocalDateTime.now());
        retailOrderRepository.save(retailOrder);
        log.debug("Buyer with id of {} cancel his order in product with id of {}", buyer.getId(), retailOrder.getRetailProduct().getId());
    }

    @Override
    public void cancelOrder(User buyer, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, OrderAlreadyAcceptedException, OrderReachedCancellingTimeLimitException, OrderAlreadyRejectedException {
        if (!buyer.hasOrder(wholeSaleOrder))
            throw new NotOwnedException("Cannot cancel order! because you don't owned this order");
        if (wholeSaleOrder.isAccepted())
            throw new OrderAlreadyAcceptedException("Cannot cancel order! because seller already accepted your order request for this product!");
        if (wholeSaleOrder.reachedCancellingTimeLimit())
            throw new OrderReachedCancellingTimeLimitException("Cannot cancel order! because orders can only be canceled within the first 24 hours from the time of purchase. This order has exceeded the cancellation window.");

        wholeSaleOrder.setStatus(CANCELLED);
        wholeSaleOrder.setUpdatedAt(LocalDateTime.now());
        wholeSaleOrderRepository.save(wholeSaleOrder);
        log.debug("Buyer with id of {} cancel his order in product with id of {}", buyer.getId(), wholeSaleOrder.getWholeSaleProduct().getId());
    }
}
