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
        productRepository.delete(product);
    }

    @Override
    public void delete(int id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getById(int id) throws ResourceNotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id of " + id + " does not exists!"));
    }

    @Override
    public boolean isExists(int id) {
        return productRepository.existsById(id);
    }


    @Override
    public Product saveByDTO(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        productRepository.save(product);
        log.debug("Product saved successfully with id of {}", product.getId());
        return product;
    }

    @Override
    public Product save(Product product) {
        productRepository.save(product);
        log.debug("Product saved successfully with id of {}", product.getId());
        return product;
    }

    @Override
    public void update(Product product, ProductDTO productDTO) throws ResourceNotFoundException {
        Product updatedProduct = productMapper.toUpdate(product, productDTO);
        productRepository.save(updatedProduct);
        log.debug("Product with id of {} updated successfully!", updatedProduct.getId());
    }
}
