package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.Address;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.baseservices.DeleteService;
import com.elleined.marketplaceapi.service.baseservices.GetService;
import com.elleined.marketplaceapi.service.baseservices.PostService;
import com.elleined.marketplaceapi.service.baseservices.PutService;

import java.util.List;

// other related user marketplace here
public interface UserService
        extends GetService<User>,
        PostService<User, UserDTO>,
        PutService<UserDTO>,
        DeleteService<User> {

    Address getAddress(User currentUser);
    List<Address> getAllDeliveryAddress(User currentUser);
    Product getAllOrder(User currentUser);

}
