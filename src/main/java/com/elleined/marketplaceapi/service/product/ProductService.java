package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.service.baseservices.DeleteService;
import com.elleined.marketplaceapi.service.baseservices.GetService;
import com.elleined.marketplaceapi.service.baseservices.PostService;
import com.elleined.marketplaceapi.service.baseservices.PutService;


public interface ProductService
        extends GetService<Product>,
        PostService<Product, ProductDTO>,
        PutService<ProductDTO>,
        DeleteService<Product> {

}
