package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Product.State;
import com.elleined.marketplaceapi.model.user.VerifiedUser;
import com.elleined.marketplaceapi.service.baseservices.DeleteService;
import com.elleined.marketplaceapi.service.baseservices.GetService;
import com.elleined.marketplaceapi.service.baseservices.PostService;
import com.elleined.marketplaceapi.service.baseservices.PutService;

import java.util.List;


public interface ProductService
        extends GetService<Product>,
        PostService<Product, ProductDTO>,
        PutService<Product, ProductDTO>,
        DeleteService {

    // Use this to get all the product listing available
    List<Product> getAllExcept(VerifiedUser verifiedUser);

    // You can use this method to see you product listing state if PENDING, LISTING, and SOLD
    List<Product> getAllProductByState(VerifiedUser verifiedUser, State state);
}
