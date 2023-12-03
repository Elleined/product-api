package com.elleined.marketplaceapi.service.cart.retail;


import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductExpiredException;
import com.elleined.marketplaceapi.exception.product.ProductNotListedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderAcceptedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderPendingException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.resource.exists.AlreadyExistException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.cart.RetailCartItemMapper;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.RetailCartItemRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
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
@Primary
public class RetailCartItemServiceImpl implements RetailCartItemService {
    private final RetailProductService retailProductService;

    private final RetailCartItemMapper retailCartItemMapper;
    private final RetailCartItemRepository retailCartItemRepository;

    private final RetailOrderRepository retailOrderRepository;

    private final AddressService addressService;

    @Override
    public List<RetailCartItem> getAll(User currentUser) {
        return currentUser.getRetailCartItems().stream()
                .filter(retailCartItem -> retailCartItem.getRetailProduct().getStatus() == Product.Status.ACTIVE)
                .filter(retailCartItem -> retailCartItem.getRetailProduct().getState() == Product.State.LISTING)
                .sorted(Comparator.comparing(CartItem::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public void delete(RetailCartItem retailCartItem) {
        retailCartItemRepository.delete(retailCartItem);
        log.debug("Cart item with id of {} are deleted because user ordered this product with id of {}", retailCartItem.getId(), retailCartItem.getRetailProduct().getId());
    }

    @Override
    public RetailCartItem save(User currentUser, RetailCartItemDTO dto) throws AlreadyExistException, ProductOrderAcceptedException, ProductOrderPendingException, ResourceOwnedException, ResourceNotFoundException, ProductAlreadySoldException, ProductNotListedException, OrderQuantiantyExceedsException, ProductExpiredException, BuyerAlreadyRejectedException {
        RetailProduct retailProduct = retailProductService.getById(dto.getProductId());

        if (currentUser.isProductAlreadyInCart(retailProduct))
            throw new AlreadyExistException("Cannot add to cart! because you already have this product in your cart.");
        if (retailProduct.isExpired())
            throw new ProductExpiredException("Cannot add to cart! because this product is already expired!");
        if (currentUser.hasOrder(retailProduct, PENDING))
            throw new ProductOrderAcceptedException("Cannot add to cart! because you already has pending order for this product. Please wait until seller take action in you order request!");
        if (currentUser.hasOrder(retailProduct, ACCEPTED))
            throw new ProductOrderPendingException("Cannot add to cart! because you already have accepted order for this product. Please contact the seller to settle your order");
        if (currentUser.hasProduct(retailProduct))
            throw new ResourceOwnedException("Cannot add to cart! you cannot add to your cart your own product!");
        if (retailProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot add to cart! because this product might already been deleted or does not exists!");
        if (retailProduct.isSold())
            throw new ProductAlreadySoldException("Cannot add to cart! because this product already been sold");
        if (!retailProduct.isListed())
            throw new ProductNotListedException("Cannot add to cart! because this product are not yet listed");
        if (retailProduct.isExceedingToAvailableQuantity(dto.getOrderQuantity()))
            throw new OrderQuantiantyExceedsException("Cannot add to cart! because trying to order that exceeds to available amount!");
        if (retailProductService.isRejectedBySeller(currentUser, retailProduct))
            throw new BuyerAlreadyRejectedException("Cannot add to cart! because seller of this product already rejected your order request before. Please wait after 1 day to re-oder this product!");

        double price = retailProduct.isSale()
                ? retailProductService.calculateOrderPrice(retailProduct.getSaleRetailProduct(), dto.getOrderQuantity())
                : retailProductService.calculateOrderPrice(retailProduct, dto.getOrderQuantity());

        DeliveryAddress deliveryAddress = addressService.getDeliveryAddressById(currentUser, dto.getDeliveryAddressId());
        RetailCartItem retailCartItem = retailCartItemMapper.toEntity(dto, currentUser, deliveryAddress, price, retailProduct);

        retailCartItemRepository.save(retailCartItem);
        log.debug("User with id of {} added to successfully added to cart product with id of {} and now has cart item id of {}", currentUser.getId(), dto.getProductId(), retailCartItem.getId());
        return retailCartItem;
    }

    @Override
    public RetailOrder orderCartItem(User currentUser, RetailCartItem retailCartItem) throws ResourceNotFoundException, ResourceOwnedException, ProductOrderAcceptedException, ProductOrderPendingException, ProductAlreadySoldException, ProductNotListedException, ProductExpiredException, OrderQuantiantyExceedsException, BuyerAlreadyRejectedException {
        RetailProduct retailProduct = retailCartItem.getRetailProduct();

        if (retailProduct.isExpired())
            throw new ProductExpiredException("Cannot order! because this product is already expired!");
        if (currentUser.hasOrder(retailProduct, PENDING))
            throw new ProductOrderAcceptedException("Cannot order! because you already pending order for this product. Please wait until seller take action in you order request!");
        if (currentUser.hasOrder(retailProduct, ACCEPTED))
            throw new ProductOrderPendingException("Cannot order! because you already have a accepted order for this product. Please contact the seller to settle your order!");
        if (currentUser.hasProduct(retailCartItem.getRetailProduct()))
            throw new ResourceOwnedException("You cannot order your own product listing!");
        if (retailProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot order! because this product does not exist or might already been deleted");
        if (retailProduct.isSold())
            throw new ProductAlreadySoldException("Cannot order! because this product has already been sold");
        if (!retailProduct.isListed())
            throw new ProductNotListedException("Cannot order! because this product is not yet been listed");
        if (retailProduct.isExceedingToAvailableQuantity(retailCartItem.getOrderQuantity()))
            throw new OrderQuantiantyExceedsException("Cannot order! because you are trying to order that exceeds to available amount!");
        if (retailProductService.isRejectedBySeller(currentUser, retailProduct))
            throw new BuyerAlreadyRejectedException("Cannot order! because seller of this product already rejected your order request before. Please wait after 1 day to re-oder this product!");

        RetailOrder retailOrder = retailCartItemMapper.cartItemToOrder(retailCartItem);
        if (retailProduct.isSale()) {
            double salePrice = retailProductService.calculateOrderPrice(retailProduct.getSaleRetailProduct(), retailCartItem.getOrderQuantity());
            retailOrder.setPrice(salePrice);
        }

        retailCartItemRepository.delete(retailCartItem);
        retailOrderRepository.save(retailOrder);
        log.debug("Cart item with id of {} are now moved to order item with id of {}", retailCartItem.getId(), retailOrder.getId());
        return retailOrder;
    }

    @Override
    public List<RetailOrder> orderAllCartItems(User currentUser, List<RetailCartItem> retailCartItems) {
        return retailCartItems.stream()
                .map(retailCartItem -> this.orderCartItem(currentUser, retailCartItem))
                .toList();
    }

    @Override
    public RetailCartItem getById(int cartItemId) throws ResourceNotFoundException {
        return retailCartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("You are trying to access cart item that does not exists or might already been deleted!"));
    }

    @Override
    public RetailCartItem getByProduct(User currentUser, Product product) throws ResourceNotFoundException {
        return currentUser.getRetailCartItems().stream()
                .filter(cartItem -> cartItem.getRetailProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Your cart does not have this product!"));
    }

    @Override
    public List<RetailCartItem> getAllById(List<Integer> cartItemIds) {
        return retailCartItemRepository.findAllById(cartItemIds).stream()
                .sorted(Comparator.comparing(RetailCartItem::getCreatedAt).reversed())
                .toList();
    }
}
