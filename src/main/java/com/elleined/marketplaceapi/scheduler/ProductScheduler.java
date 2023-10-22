package com.elleined.marketplaceapi.scheduler;


import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class ProductScheduler {

    private final RetailProductService retailProductService;

    @Scheduled(fixedRate = 2000L)
    public void run() {
        retailProductService.deleteExpiredProducts();
    }
}
