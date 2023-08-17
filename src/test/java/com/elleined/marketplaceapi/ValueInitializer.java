package com.elleined.marketplaceapi;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(propagation = Propagation.SUPPORTS)
public class ValueInitializer {

    @Test
    @SqlGroup({
            @Sql(scripts = {
                    "classpath:sql/dml/cropData.sql",
                    "classpath:sql/dml/unitData.sql"
            }, config = @SqlConfig(transactionMode = TransactionMode.DEFAULT))
    })
    void initializeValues() {

    }
}
