package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.exception.atm.InsufficientFundException;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.product.*;
import com.elleined.marketplaceapi.exception.resource.ResourceException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.NotVerifiedException;
import com.elleined.marketplaceapi.mapper.product.ProductMapper;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
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
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.unit.RetailUnitService;
import com.elleined.marketplaceapi.service.unit.WholeSaleUnitService;
import com.elleined.marketplaceapi.service.validator.Validator;
import com.elleined.marketplaceapi.utils.DirectoryFolders;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("sellerServiceImpl")
public class SellerServiceImpl implements SellerService {
    public static final int DAY_RANGE = 14;

    private final PrivateChatRoomService privateChatRoomService;

    private final RetailProductRepository retailProductRepository;
    private final WholeSaleProductRepository wholeSaleProductRepository;

    private final ProductService<RetailProduct> retailProductService;
    private final ProductService<WholeSaleProduct> wholeSaleProductService;

    private final ProductMapper<WholeSaleProductDTO, WholeSaleProduct> wholeSaleProductMapper;
    private final ProductMapper<RetailProductDTO, RetailProduct> retailProductMapper;

    private final ImageUploader imageUploader;

    private final CropService cropService;
    private final RetailUnitService retailUnitService;
    private final WholeSaleUnitService wholeSaleUnitService;

    private final OrderRepository orderRepository;

    private final ATMValidator atmValidator;

    @Value("${cropTrade.img.directory}")
    private String cropTradeImgDirectory;

    //    @Override
//    public Product saleProduct(User seller, Product product, int salePercentage)
//            throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
//
//        if (salePercentage <= 0) throw new FieldException("Cannot sale this product! Sale percentage must be a positive value. Please ensure that the sale percentage is greater than 0.");
//        if (!seller.hasProduct(product)) throw new NotOwnedException("Cannot sale this product! because You do not have ownership rights to update this product. Only the owner of the product can make changes.");
//        if (!product.isListed()) throw new ProductNotListedException("Cannot sale this product! because you are trying to perform an action on a product that has not been listed in our system. This action is not permitted for products that are not yet listed.");
//
//        double totalPrice = productService.calculateTotalPrice(product.getPricePerUnit(), product.getQuantityPerUnit(), product.getAvailableQuantity());
//        double salePrice = (totalPrice * (salePercentage / 100f));
//        if (salePrice >= totalPrice) throw new ProductSaleException("Cannot sale this product! the sale price " + salePrice + " you've entered does not result in a lower price than the previous price " + totalPrice + " after applying the specified sale percentage " + salePercentage + ". When setting a sale price, it should be lower than the original price to qualify as a discount.\nPlease enter a sale price that, after applying the sale percentage " + salePercentage + ", is lower than the previous price to apply a valid discount.");
//
//        return null;
//    }

    @Override
    public RetailProduct saleProduct(User seller, RetailProduct retailProduct, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return null;
    }

    @Override
    public WholeSaleProduct saleProduct(User seller, WholeSaleProduct wholeSaleProduct, int salePercentage) throws NotOwnedException, ProductSaleException, FieldException, ProductNotListedException {
        return null;
    }

    @Override
    public RetailProduct saveProduct(User seller, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        if (Validator.notValidMultipartFile(productPicture))
            throw new ResourceException("Cannot save product! please provide product picture!");
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller))
            throw new InsufficientFundException("Cannot save product! because you're balance cannot be less than in you're total pending withdraw request which. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        if (isHarvestAndExpirationDateNotInRange(productDTO.getHarvestDate(), productDTO.getExpirationDate(), DAY_RANGE))
            throw new ProductExpirationLimitException("Cannot save product! because expiration date should be within " + DAY_RANGE + " after the harvest date");
        if (!seller.isVerified())
            throw new NotVerifiedException("Cannot save product! because you are not yet been verified! Consider sending verification form first then get verified afterwards to list a product!");

        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());

        Product product = productMapper.toEntity(productDTO, seller);
        product.setPicture(productPicture.getOriginalFilename());
        productRepository.save(product);

        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PRODUCT_PICTURES_FOLDER, productPicture);
        log.debug("Product saved successfully with id of {}", product.getId());
        return product;
    }

    @Override
    public WholeSaleProduct saveProduct(User seller, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        return null;
    }

    @Override
    public void updateProduct(User seller, RetailProduct retailProduct, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {
        if (Validator.notValidMultipartFile(productPicture)) throw new ResourceException("Cannot save product! please provide product picture!");
        if (product.hasAcceptedOrder())
            throw new ProductHasAcceptedOrderException("Cannot update product! because theres an accepted order for this product.");
        if (product.hasPendingOrder())
            throw new ProductHasPendingOrderException("Cannot update product! because theres an pending order for this product");
        if (product.hasSoldOrder())
            throw new ProductAlreadySoldException("Cannot update product! because this product is already been sold!");
        if (!seller.isVerified())
            throw new NotVerifiedException("Cannot update product! because you are not yet been verified! Consider register shop first then get verified afterwards to update this product!");
        if (!seller.hasProduct(product))
            throw new NotOwnedException("Cannot update product! because you don't owned this product!");
        if (product.isSold())
            throw new ProductAlreadySoldException("Cannot update product! because this product is already sold!");
        if (product.isDeleted())
            throw new ResourceNotFoundException("Cannot update product! because this product does not exists or might already been deleted!");

        product.setState(Product.State.PENDING);
        updatePendingAndAcceptedOrderStatus(product, OrderItem.OrderItemStatus.CANCELLED);
        if (!cropService.existsByName(productDTO.getCropName())) cropService.save(productDTO.getCropName());
        if (!unitService.existsByName(productDTO.getUnitName())) unitService.save(productDTO.getUnitName());
        Product updatedProduct = productMapper.toUpdate(product, productDTO);
        updatedProduct.setPicture(productPicture.getOriginalFilename());
        productRepository.save(updatedProduct);

        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PRODUCT_PICTURES_FOLDER, productPicture);
        log.debug("Product with id of {} updated successfully!", updatedProduct.getId());
    }

    @Override
    public void updateProduct(User seller, WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {

    }

    @Override
    public void deleteProduct(User seller, RetailProduct retailProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {

        if (!seller.isVerified())
            throw new NotVerifiedException("Cannot delete product! because you are not yet been verified! Consider register shop first then get verified afterwards to delete this product");
        if (!seller.hasProduct(product))
            throw new NotOwnedException("Cannot delete product! because you don't owned this product!");
        if (product.isSold())
            throw new ProductAlreadySoldException("Cannot delete product! because this product is already sold");
        if (product.hasPendingOrder())
            throw new ProductHasPendingOrderException("Cannot delete product! because this product has pending orders. Please settle first the pending order to delete this");
        if (product.hasAcceptedOrder())
            throw new ProductHasAcceptedOrderException("Cannot delete product! because this product has accepted orders. Please settle first the accepted order to delete this");

        product.setStatus(Product.Status.INACTIVE);
        updatePendingAndAcceptedOrderStatus(product, OrderItem.OrderItemStatus.CANCELLED);

        productRepository.save(product);
        orderRepository.saveAll(product.getOrders());
        log.debug("Product with id of {} are now inactive", product.getId());
    }

    @Override
    public void deleteProduct(User seller, WholeSaleProduct wholeSaleProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {

    }

    @Override
    public void acceptOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {

        Product product = orderItem.getProduct();
        int newAvailableQuantity = product.getAvailableQuantity() - orderItem.getOrderQuantity();
        if (newAvailableQuantity < 0) throw new ProductAlreadySoldException("Cannot accept order! because this product is already been sold and there are now available quantity!");

        if (orderItem.getProduct().isRejected())
            throw new ProductRejectedException("Cannot accept order! because with this product is rejected by the moderator!");
        if (isSellerOwnedOrder(seller, orderItem))
            throw new NotOwnedException("Cannot accept order! because you don't own this order!");
        if (StringUtil.isNotValid(messageToBuyer))
            throw new NotValidBodyException("Cannot accept order! please provide a message for buyer.");

        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.ACCEPTED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setSellerMessage(messageToBuyer);

        orderRepository.save(orderItem);
        log.debug("Seller successfully updated order item with id of {} status from {} to {}", orderItem.getId(), oldStatus, OrderItem.OrderItemStatus.ACCEPTED.name());
    }

    @Override
    public void acceptOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {

    }

    @Override
    public void rejectOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {

        if (isSellerOwnedOrder(seller, orderItem))
            throw new NotOwnedException("Cannot reject order! because you don't own this order!");
        if (StringUtil.isNotValid(messageToBuyer))
            throw new NotValidBodyException("Cannot reject order! please provide a message for the buyer");

        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.REJECTED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setSellerMessage(messageToBuyer);

        orderRepository.save(orderItem);
        log.debug("Seller successfully updated order item with id of {} status from {} to {}", orderItem.getId(), oldStatus, OrderItem.OrderItemStatus.REJECTED.name());
    }

    @Override
    public void rejectOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {

    }

    @Override
    public void soldOrder(User seller, RetailOrder retailOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller))
            throw new InsufficientFundException("Cannot order product! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        if (isSellerOwnedOrder(seller, orderItem))
            throw new NotOwnedException("Cannot sold order! because you don't owned this order!");

        Product product = orderItem.getProduct();
        int newAvailableQuantity = product.getAvailableQuantity() - orderItem.getOrderQuantity();
        if (newAvailableQuantity <= 0) {
            product.setState(Product.State.SOLD);
            orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.SOLD);
            updatePendingAndAcceptedOrderStatus(product, OrderItem.OrderItemStatus.SOLD);

            PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(seller, orderItem.getPurchaser(), product);
            privateChatRoomService.deleteAllMessages(privateChatRoom);

            productRepository.save(product);
            orderRepository.save(orderItem);
            orderRepository.saveAll(product.getOrders());
            return;
        }

        product.setAvailableQuantity(newAvailableQuantity);
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.SOLD);
        updatePendingAndAcceptedOrderStatus(product, OrderItem.OrderItemStatus.CANCELLED);

        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(seller, orderItem.getPurchaser(), product);
        privateChatRoomService.deleteAllMessages(privateChatRoom);

        productRepository.save(product);
        orderRepository.save(orderItem);
        orderRepository.saveAll(product.getOrders());
    }

    @Override
    public void soldOrder(User seller, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {

    }

    private boolean isHarvestAndExpirationDateNotInRange(LocalDate harvestDate, LocalDate expirationDate, int rangeInDays) {
        LocalDate dateRange = harvestDate.plusDays(rangeInDays);

        return expirationDate.isAfter(dateRange) || expirationDate.isBefore(harvestDate);
    }
}
