package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShopMapperTest {

    @InjectMocks
    private ShopMapper shopMapper = Mappers.getMapper(ShopMapper.class);

    @Test
    void toEntity() {
        User owner = User.builder()
                .id(1)
                .build();

        Shop shop = shopMapper.toEntity(owner, "Shopname", "shopdesc", MultiPartFileDataFactory.notEmpty());

        assertEquals(0, shop.getId());

        assertNotNull(shop.getOwner());
        assertEquals(owner, shop.getOwner());

        assertNotNull(shop.getName());
        assertNotNull(shop.getDescription());
        assertNotNull(shop.getPicture());
    }

    @Test
    void toDTO() {
        User owner = User.builder()
                .id(1)
                .build();

        Shop expected = Shop.builder()
                .id(1)
                .name("Name")
                .description("Description")
                .picture("PIcture")
                .owner(owner)
                .build();

        ShopDTO actual = shopMapper.toDTO(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getShopName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPicture(), actual.getPicture());

        assertNull(actual.getValidId());
    }
}