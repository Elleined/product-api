package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void delete(Product product) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Product getById(int id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean isExists(int id) {
        return false;
    }

    @Override
    public boolean isExists(Product product) {
        return false;
    }

    @Override
    public Product saveByDTO(ProductDTO productDTO) {
        return null;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public void update(int id, ProductDTO productDTO) throws ResourceNotFoundException {

    }
}
