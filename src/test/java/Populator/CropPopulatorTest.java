package Populator;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(propagation = Propagation.SUPPORTS)
public class CropPopulatorTest {

//    @Test
//    @Sql(scripts = {"classpath:sql/"})
//    void populateCropTable() {
//
//    }

}
