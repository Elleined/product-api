package com.elleined.marketplaceapi.service.cart.wholesale;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.cart.WholeSaleCartItemMapper;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.WholeSaleCartItemRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.elleined.marketplaceapi.model.order.Order.Status.ACCEPTED;
import static com.elleined.marketplaceapi.model.order.Order.Status.PENDING;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("wholeSaleCartItemService")
public class WholeSaleCartItemServiceImpl implements WholeSaleCartItemService {
    private final WholeSaleProductService wholeSaleProductService;

    private final WholeSaleCartItemMapper wholeSaleCartItemMapper;
    private final WholeSaleCartItemRepository wholeSaleCartItemRepository;

    private final WholeSaleOrderRepository wholeSaleOrderRepository;

    private final AddressService addressService;

    @Override
    public List<WholeSaleCartItem> getAll(User currentUser) {
        return currentUser.getWholeSaleCartItems().stream()
                .filter(wholeSaleCartItem -> wholeSaleCartItem.getWholeSaleProduct().getStatus() == Product.Status.ACTIVE)
                .filter(wholeSaleCartItem -> wholeSaleCartItem.getWholeSaleProduct().getState() == Product.State.LISTING)
                .sorted(Comparator.comparing(CartItem::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public void delete(WholeSaleCartItem wholeSaleCartItem) {
        wholeSaleCartItemRepository.delete(wholeSaleCartItem);
        log.debug("Cart item with id of {} are deleted because user ordered this product with id of {}", wholeSaleCartItem.getId(), wholeSaleCartItem.getWholeSaleProduct().getId());
    }

    @Override
    public WholeSaleCartItem save(User currentUser, WholeSaleCartItemDTO dto) throws AlreadyExistException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ResourceOwnedException, ResourceNotFoundException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, ProductExpiredException, BuyerAlreadyRejectedException {
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(dto.getProductId());
        if (currentUser.isProductAlreadyInCart(wholeSaleProduct))
            throw new AlreadyExistException("Cannot add to cart! because you already have this product in your cart.");
        if (currentUser.hasOrder(wholeSaleProduct, PENDING))
            throw new ProductHasPendingOrderException("Cannot add to cart! because you already has pending order for this product. Please wait until seller take action in you order request!");
        if (currentUser.hasOrder(wholeSaleProduct, ACCEPTED))
            throw new ProductHasAcceptedOrderException("Cannot add to cart! because you already have accepted order for this product. Please contact the seller to settle your order");
        if (currentUser.hasProduct(wholeSaleProduct))
            throw new ResourceOwnedException("Cannot add to cart! you cannot add to your cart your own product!");
        if (wholeSaleProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot add to cart! because this product might already been deleted or does not exists!");
        if (wholeSaleProduct.isSold())
            throw new ProductAlreadySoldException("Cannot add to cart! because this product already been sold");
        if (!wholeSaleProduct.isListed())
            throw new ProductNotListedException("Cannot add to cart! because this product are not yet listed");
        if (wholeSaleProductService.isRejectedBySeller(currentUser, wholeSaleProduct))
            throw new BuyerAlreadyRejectedException("Cannot add to cart! because seller of this product already rejected your order request before. Please wait after 1 day to re-oder this product!");

        DeliveryAddress deliveryAddress = addressService.getDeliveryAddressById(currentUser, dto.getDeliveryAddressId());
        WholeSaleCartItem wholeSaleCartItem = wholeSaleCartItemMapper.toEntity(dto, currentUser, deliveryAddress, wholeSaleProduct);
        currentUser.getWholeSaleCartItems().add(wholeSaleCartItem);

        wholeSaleCartItemRepository.save(wholeSaleCartItem);
        log.debug("User with id of {} added to successfully added to cart product with id of {} and now has cart item id of {}", currentUser.getId(), dto.getProductId(), wholeSaleCartItem.getId());
        return wholeSaleCartItem;
    }

    @Override
    public WholeSaleOrder orderCartItem(User currentUser, WholeSaleCartItem wholeSaleCartItem) throws ResourceNotFoundException, ResourceOwnedException, ProductHasPendingOrderException, ProductHasAcceptedOrderException, ProductAlreadySoldException, ProductNotListedException, ProductExpiredException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException {
        WholeSaleProduct wholeSaleProduct = wholeSaleCartItem.getWholeSaleProduct();
        if (currentUser.hasOrder(wholeSaleProduct, PENDING))
            throw new ProductHasPendingOrderException("Cannot order! because you already pending order for this product. Please wait until seller take action in you order request!");
        if (currentUser.hasOrder(wholeSaleProduct, ACCEPTED))
            throw new ProductHasAcceptedOrderException("Cannot order! because you already have a accepted order for this product. Please contact the seller to settle your order!");
        if (currentUser.hasProduct(wholeSaleProduct))
            throw new ResourceOwnedException("You cannot order your own product listing!");
        if (wholeSaleProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot order! because this product does not exist or might already been deleted");
        if (wholeSaleProduct.isSold())
            throw new ProductAlreadySoldException("Cannot order! because this product has already been sold");
        if (!wholeSaleProduct.isListed())
            throw new ProductNotListedException("Cannot order! because this product is not yet been listed");
        if (wholeSaleProductService.isRejectedBySeller(currentUser, wholeSaleProduct))
            throw new BuyerAlreadyRejectedException("Cannot order! because seller of this product already rejected your order request before. Please wait after 1 day to re-oder this product!");

        WholeSaleOrder wholeSaleOrder = wholeSaleCartItemMapper.cartItemToOrder(wholeSaleCartItem);

        wholeSaleCartItemRepository.delete(wholeSaleCartItem);
        wholeSaleOrderRepository.save(wholeSaleOrder);
        log.debug("Cart item with id of {} are now moved to order item with id of {}", wholeSaleCartItem.getId(), wholeSaleOrder.getId());
        return wholeSaleOrder;
    }

    @Override
    public List<WholeSaleOrder> orderAllCartItems(User currentUser, List<WholeSaleCartItem> cartItems) {
        return cartItems.stream()
                .map(wholeSaleCartItem -> this.orderCartItem(currentUser, wholeSaleCartItem))
                .toList();
    }

    @Override
    public WholeSaleCartItem getById(int cartItemId) throws ResourceNotFoundException {
        return wholeSaleCartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("You are trying to access cart item that does not exists or might already been deleted!"));
    }

    @Override
    public WholeSaleCartItem getByProduct(User currentUser, Product product) throws ResourceNotFoundException {
        return currentUser.getWholeSaleCartItems().stream()
                .filter(cartItem -> cartItem.getWholeSaleProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Your cart does not have this product!"));
    }

    @Override
    public List<WholeSaleCartItem> getAllById(List<Integer> cartItemIds) {
        return wholeSaleCartItemRepository.findAllById(cartItemIds).stream()
                .sorted(Comparator.comparing(WholeSaleCartItem::getCreatedAt).reversed())
                .toList();
    }
}
