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
import com.elleined.marketplaceapi.model.order.Order.Status;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
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
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("sellerServiceImpl")
public class SellerServiceImpl implements SellerService {
    private final WholeSaleOrderRepository wholeSaleOrderRepository;
    private final RetailOrderRepository retailOrderRepository;
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
        if (seller.isNotVerified())
            throw new NotVerifiedException("Cannot save product! because you are not yet been verified! Consider sending verification form first then get verified afterwards to list a product!");

        if (cropService.notExist(retailProductDTO.getCropName())) cropService.save(retailProductDTO.getCropName());

        RetailProduct retailProduct = retailProductMapper.toEntity(retailProductDTO, seller);
        retailProduct.setPicture(productPicture.getOriginalFilename());
        retailProductRepository.save(retailProduct);

        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PRODUCT_PICTURES_FOLDER, productPicture);
        log.debug("Retail product saved successfully with id of {}", retailProduct.getId());
        return retailProduct;
    }

    @Override
    public WholeSaleProduct saveProduct(User seller, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotVerifiedException, InsufficientFundException, ProductExpirationLimitException, IOException {
        if (Validator.notValidMultipartFile(productPicture))
            throw new ResourceException("Cannot save product! please provide product picture!");
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller))
            throw new InsufficientFundException("Cannot save product! because you're balance cannot be less than in you're total pending withdraw request which. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        if (seller.isNotVerified())
            throw new NotVerifiedException("Cannot save product! because you are not yet been verified! Consider sending verification form first then get verified afterwards to list a product!");

        if (cropService.notExist(wholeSaleProductDTO.getCropName())) cropService.save(wholeSaleProductDTO.getCropName());

        WholeSaleProduct wholeSaleProduct = wholeSaleProductMapper.toEntity(wholeSaleProductDTO, seller);
        wholeSaleProduct.setPicture(productPicture.getOriginalFilename());
        wholeSaleProductRepository.save(wholeSaleProduct);

        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PRODUCT_PICTURES_FOLDER, productPicture);
        log.debug("Whole sale product saved successfully with id of {}", wholeSaleProduct.getId());
        return wholeSaleProduct;
    }

    @Override
    public void updateProduct(User seller, RetailProduct retailProduct, RetailProductDTO retailProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {
        if (Validator.notValidMultipartFile(productPicture))
            throw new ResourceException("Cannot save product! please provide product picture!");
        if (retailProduct.hasAcceptedOrder())
            throw new ProductHasAcceptedOrderException("Cannot update retail product! because theres an accepted order for this retail product.");
        if (retailProduct.hasPendingOrder())
            throw new ProductHasPendingOrderException("Cannot update retail product! because theres an pending order for this retail product");
        if (retailProduct.hasSoldOrder())
            throw new ProductAlreadySoldException("Cannot update retail product! because this retail product is already been sold!");
        if (!seller.isVerified())
            throw new NotVerifiedException("Cannot update retail product! because you are not yet been verified! Consider register shop first then get verified afterwards to update this retail product!");
        if (!seller.hasProduct(retailProduct))
            throw new NotOwnedException("Cannot update retail product! because you don't owned this retail product!");
        if (retailProduct.isSold())
            throw new ProductAlreadySoldException("Cannot update retail product! because this retail product is already sold!");
        if (retailProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot update retail product! because this retail product does not exists or might already been deleted!");

        retailProduct.setState(Product.State.PENDING);
        retailProductService.updateAllPendingAndAcceptedOrders(retailProduct, Status.CANCELLED);

        if (cropService.notExist(retailProductDTO.getCropName())) cropService.save(retailProductDTO.getCropName());
        RetailProduct updatedRetailProduct = retailProductMapper.toUpdate(retailProduct, retailProductDTO);
        updatedRetailProduct.setPicture(productPicture.getOriginalFilename());
        retailProductRepository.save(updatedRetailProduct);

        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PRODUCT_PICTURES_FOLDER, productPicture);
        log.debug("Retail product with id of {} updated successfully!", updatedRetailProduct.getId());
    }

    @Override
    public void updateProduct(User seller, WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO wholeSaleProductDTO, MultipartFile productPicture) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ResourceNotFoundException, ProductHasAcceptedOrderException, ProductHasPendingOrderException, IOException {
        if (Validator.notValidMultipartFile(productPicture))
            throw new ResourceException("Cannot save product! please provide product picture!");
        if (wholeSaleProduct.hasAcceptedOrder())
            throw new ProductHasAcceptedOrderException("Cannot update whole sale product! because theres an accepted order for this whole sale product.");
        if (wholeSaleProduct.hasPendingOrder())
            throw new ProductHasPendingOrderException("Cannot update whole sale product! because theres an pending order for this whole sale product");
        if (wholeSaleProduct.hasSoldOrder())
            throw new ProductAlreadySoldException("Cannot update whole sale product! because this whole sale product is already been sold!");
        if (!seller.isVerified())
            throw new NotVerifiedException("Cannot update whole sale product! because you are not yet been verified! Consider register shop first then get verified afterwards to update this whole sale product!");
        if (!seller.hasProduct(wholeSaleProduct))
            throw new NotOwnedException("Cannot update whole sale product! because you don't owned this whole sale product!");
        if (wholeSaleProduct.isSold())
            throw new ProductAlreadySoldException("Cannot update whole sale product! because this whole sale product is already sold!");
        if (wholeSaleProduct.isDeleted())
            throw new ResourceNotFoundException("Cannot update whole sale product! because this whole sale product does not exists or might already been deleted!");

        wholeSaleProduct.setState(Product.State.PENDING);
        wholeSaleProductService.updateAllPendingAndAcceptedOrders(wholeSaleProduct, Status.CANCELLED);

        if (cropService.notExist(wholeSaleProductDTO.getCropName())) cropService.save(wholeSaleProductDTO.getCropName());
        WholeSaleProduct updatedWholeSaleProduct = wholeSaleProductMapper.toUpdate(wholeSaleProduct, wholeSaleProductDTO);
        updatedWholeSaleProduct.setPicture(productPicture.getOriginalFilename());
        wholeSaleProductRepository.save(wholeSaleProduct);

        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PRODUCT_PICTURES_FOLDER, productPicture);
        log.debug("Whole sale product with id of {} updated successfully!", wholeSaleProduct.getId());
    }

    @Override
    public void deleteProduct(User seller, RetailProduct retailProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {
        if (!seller.isVerified())
            throw new NotVerifiedException("Cannot delete product! because you are not yet been verified! Consider register shop first then get verified afterwards to delete this product");
        if (!seller.hasProduct(retailProduct))
            throw new NotOwnedException("Cannot delete retail product! because you don't owned this retail product!");
        if (retailProduct.isSold())
            throw new ProductAlreadySoldException("Cannot delete retail product! because this retail product is already sold");
        if (retailProduct.hasPendingOrder())
            throw new ProductHasPendingOrderException("Cannot delete retail product! because this retail product has pending orders. Please settle first the pending order to delete this");
        if (retailProduct.hasAcceptedOrder())
            throw new ProductHasAcceptedOrderException("Cannot delete retail product! because this retail product has accepted orders. Please settle first the accepted order to delete this");

        retailProduct.setStatus(Product.Status.INACTIVE);
        retailProductService.updateAllPendingAndAcceptedOrders(retailProduct, Status.CANCELLED);
        retailProductRepository.save(retailProduct);

        log.debug("Retail product with id of {} are now inactive", retailProduct.getId());
    }

    @Override
    public void deleteProduct(User seller, WholeSaleProduct wholeSaleProduct) throws NotOwnedException, NotVerifiedException, ProductAlreadySoldException, ProductHasPendingOrderException, ProductHasAcceptedOrderException {
        if (!seller.isVerified())
            throw new NotVerifiedException("Cannot delete product! because you are not yet been verified! Consider register shop first then get verified afterwards to delete this product");
        if (!seller.hasProduct(wholeSaleProduct))
            throw new NotOwnedException("Cannot delete whole sale product! because you don't owned this whole sale product!");
        if (wholeSaleProduct.isSold())
            throw new ProductAlreadySoldException("Cannot delete whole sale product! because this whole sale product is already sold");
        if (wholeSaleProduct.hasPendingOrder())
            throw new ProductHasPendingOrderException("Cannot delete whole sale product! because this whole sale product has pending orders. Please settle first the pending order to delete this");
        if (wholeSaleProduct.hasAcceptedOrder())
            throw new ProductHasAcceptedOrderException("Cannot delete whole sale product! because this whole sale product has accepted orders. Please settle first the accepted order to delete this");

        wholeSaleProduct.setStatus(Product.Status.INACTIVE);
        wholeSaleProductService.updateAllPendingAndAcceptedOrders(wholeSaleProduct, Status.CANCELLED);
        wholeSaleProductRepository.save(wholeSaleProduct);

        log.debug("Retail product with id of {} are now inactive", wholeSaleProduct.getId());
    }

    @Override
    public void acceptOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {
        RetailProduct retailProduct = retailOrder.getRetailProduct();
        int newAvailableQuantity = retailProduct.getAvailableQuantity() - retailOrder.getOrderQuantity();
        if (newAvailableQuantity < 0) throw new ProductAlreadySoldException("Cannot accept order! because this product is already been sold and there are now available quantity!");

        if (retailProduct.isRejected())
            throw new ProductRejectedException("Cannot accept order! because with this product is rejected by the moderator!");
        if (!seller.hasSellableProductOrder(retailOrder))
            throw new NotOwnedException("Cannot accept order! because you don't own this order!");
        if (StringUtil.isNotValid(messageToBuyer))
            throw new NotValidBodyException("Cannot accept order! please provide a message for buyer.");

        Status oldStatus = retailOrder.getStatus();

        retailOrder.setStatus(Status.ACCEPTED);
        retailOrder.setUpdatedAt(LocalDateTime.now());
        retailOrder.setSellerMessage(messageToBuyer);

        retailOrderRepository.save(retailOrder);
        log.debug("Seller successfully updated retail order with id of {} status from {} to {}", retailOrder.getId(), oldStatus, retailOrder.getStatus());
    }

    @Override
    public void acceptOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException, ProductRejectedException {
        WholeSaleProduct wholeSaleProduct = wholeSaleOrder.getWholeSaleProduct();

        if (wholeSaleProduct.isRejected())
            throw new ProductRejectedException("Cannot accept order! because with this product is rejected by the moderator!");
        if (!seller.hasSellableProductOrder(wholeSaleOrder))
            throw new NotOwnedException("Cannot accept order! because you don't own this order!");
        if (StringUtil.isNotValid(messageToBuyer))
            throw new NotValidBodyException("Cannot accept order! please provide a message for buyer.");

        Status oldStatus = wholeSaleOrder.getStatus();

        wholeSaleOrder.setStatus(Status.ACCEPTED);
        wholeSaleOrder.setUpdatedAt(LocalDateTime.now());
        wholeSaleOrder.setSellerMessage(messageToBuyer);

        wholeSaleOrderRepository.save(wholeSaleOrder);
        log.debug("Seller successfully updated whole sale order with id of {} status from {} to {}", wholeSaleOrder.getId(), oldStatus, wholeSaleOrder.getStatus());
    }

    @Override
    public void rejectOrder(User seller, RetailOrder retailOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        if (!seller.hasSellableProductOrder(retailOrder))
            throw new NotOwnedException("Cannot reject order! because you don't own this order!");
        if (StringUtil.isNotValid(messageToBuyer))
            throw new NotValidBodyException("Cannot reject order! please provide a message for the buyer");

        Status oldStatus = retailOrder.getStatus();

        retailOrder.setStatus(Status.REJECTED);
        retailOrder.setUpdatedAt(LocalDateTime.now());
        retailOrder.setSellerMessage(messageToBuyer);

        orderRepository.save(retailOrder);
        log.debug("Seller successfully updated retail order with id of {} status from {} to {}", retailOrder.getId(), oldStatus, retailOrder.getStatus());
    }

    @Override
    public void rejectOrder(User seller, WholeSaleOrder wholeSaleOrder, String messageToBuyer) throws NotOwnedException, NotValidBodyException {
        if (!seller.hasSellableProductOrder(wholeSaleOrder))
            throw new NotOwnedException("Cannot reject order! because you don't own this order!");
        if (StringUtil.isNotValid(messageToBuyer))
            throw new NotValidBodyException("Cannot reject order! please provide a message for the buyer");

        Status oldStatus = wholeSaleOrder.getStatus();

        wholeSaleOrder.setStatus(Status.REJECTED);
        wholeSaleOrder.setUpdatedAt(LocalDateTime.now());
        wholeSaleOrder.setSellerMessage(messageToBuyer);

        orderRepository.save(wholeSaleOrder);
        log.debug("Seller successfully updated whole sale order with id of {} status from {} to {}", wholeSaleOrder.getId(), oldStatus, wholeSaleOrder.getStatus());
    }

    @Override
    public void soldOrder(User seller, RetailOrder retailOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller))
            throw new InsufficientFundException("Cannot order product! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        if (!seller.hasSellableProductOrder(retailOrder))
            throw new NotOwnedException("Cannot sold order! because you don't owned this order!");

        RetailProduct retailProduct = retailOrder.getRetailProduct();
        int newAvailableQuantity = retailProduct.getAvailableQuantity() - retailOrder.getOrderQuantity();
        if (newAvailableQuantity <= 0) {
            retailProduct.setState(Product.State.SOLD);
            retailOrder.setStatus(Status.SOLD);

            PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(seller, retailOrder.getPurchaser(), retailProduct);
            privateChatRoomService.deleteAllMessages(privateChatRoom);

            retailProductRepository.save(retailProduct);
            retailOrderRepository.save(retailOrder);
            retailProductService.updateAllPendingAndAcceptedOrders(retailProduct, Status.SOLD);
            return;
        }

        retailProduct.setAvailableQuantity(newAvailableQuantity);
        retailOrder.setStatus(Status.SOLD);

        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(seller, retailOrder.getPurchaser(), retailProduct);
        privateChatRoomService.deleteAllMessages(privateChatRoom);

        retailProductRepository.save(retailProduct);
        retailOrderRepository.save(retailOrder);
        retailProductService.updateAllPendingAndAcceptedOrders(retailProduct, Status.CANCELLED);
    }

    @Override
    public void soldOrder(User seller, WholeSaleOrder wholeSaleOrder) throws NotOwnedException, InsufficientFundException, InsufficientBalanceException {
        if (atmValidator.isUserTotalPendingRequestAmountAboveBalance(seller))
            throw new InsufficientFundException("Cannot order product! because you're balance cannot be less than in you're total pending withdraw request. Cancel some of your withdraw request or wait for our team to settle you withdraw request.");
        if (!seller.hasSellableProductOrder(wholeSaleOrder))
            throw new NotOwnedException("Cannot sold order! because you don't owned this order!");

        WholeSaleProduct wholeSaleProduct = wholeSaleOrder.getWholeSaleProduct();
        wholeSaleOrder.setStatus(Status.SOLD);

        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(seller, wholeSaleOrder.getPurchaser(), wholeSaleProduct);
        privateChatRoomService.deleteAllMessages(privateChatRoom);

        wholeSaleOrderRepository.save(wholeSaleOrder);
        wholeSaleProductService.updateAllPendingAndAcceptedOrders(wholeSaleProduct, Status.CANCELLED);
    }
}
