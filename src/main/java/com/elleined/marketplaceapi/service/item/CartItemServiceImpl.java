package com.elleined.marketplaceapi.service.item;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartItemServiceImpl implements CartItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<CartItem> getAll() {
        return null;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void save(CartItem cartItem) {

    }

    @Override
    public void moveToOrderItem(CartItem cartItem) {

    }

    @Override
    public void moveAllToOrderItem(List<CartItem> cartItems) {

    }

    @Override
    public boolean isProductAlreadyInCart(User buyer, Product product) {
        return false;
    }

    @Override
    public void updateOrderQuantity(CartItem cartItem) {

    }
}
