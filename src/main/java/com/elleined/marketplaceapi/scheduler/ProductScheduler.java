package com.elleined.marketplaceapi.scheduler;


import com.elleined.marketplaceapi.service.product.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductService productService;

    @Scheduled(fixedRate = 2000L)
    public void run() {
        productService.deleteExpiredProducts();
    }
}
