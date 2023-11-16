package com.elleined.marketplaceapi.controller.address;

import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.mapper.address.DeliveryAddressMapper;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/delivery-addresses")
public class DeliveryAddressController {

    private final UserService userService;

    private final AddressService addressService;
    private final DeliveryAddressMapper deliveryAddressMapper;

    @PostMapping
    public DeliveryAddressDTO saveDeliveryAddress(@PathVariable("currentUserId") int currentUserId,
                                                  @Valid @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        User currentUser = userService.getById(currentUserId);
        DeliveryAddress deliveryAddress = addressService.saveDeliveryAddress(currentUser, deliveryAddressDTO);
        return deliveryAddressMapper.toDTO(deliveryAddress);
    }

    @GetMapping
    public List<DeliveryAddressDTO> getAllDeliveryAddress(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        List<DeliveryAddress> deliveryAddresses = addressService.getAllDeliveryAddress(currentUser);
        return deliveryAddresses.stream()
                .map(deliveryAddressMapper::toDTO)
                .toList();
    }

    @GetMapping("/{deliveryAddressId}")
    public DeliveryAddressDTO getDeliveryAddressById(@PathVariable("currentUserId") int currentUserId,
                                                     @PathVariable("deliveryAddressId") int deliveryAddressId) {

        User currentUser = userService.getById(currentUserId);
        DeliveryAddress deliveryAddress = addressService.getDeliveryAddressById(currentUser, deliveryAddressId);
        return deliveryAddressMapper.toDTO(deliveryAddress);
    }
}
