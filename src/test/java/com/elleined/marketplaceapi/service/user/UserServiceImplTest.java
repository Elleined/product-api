package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @Transactional(propagation = Propagation.NOT_SUPPORTED) // uncomment this to actually hit the database
class UserServiceImplTest {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplTest(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Test
    void isSellerExceedsToMaxListingPerDay() {
        LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        User user = userRepository.findById(1).orElseThrow();
        int listedProductsNgayon = productRepository.fetchSellerProductListingCount(currentDateTimeMidnight, tomorrowMidnight, user);
        System.out.println(listedProductsNgayon);
    }
}