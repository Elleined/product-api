package com.elleined.marketplaceapi.service.cart.wholesale;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.cart.CartItemService;

import java.util.List;

public interface WholeSaleCartItemService extends CartItemService<WholeSaleCartItem, WholeSaleCartItemDTO> {
    List<WholeSaleOrder> orderAllCartItems(User currentUser, List<WholeSaleCartItem> wholeSaleCartItems);
}
