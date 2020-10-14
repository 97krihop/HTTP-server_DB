package database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ProductDaoTest {

    private ProductDao productDao;

    private String exampleProductName() {
        String[] product = {"apples", "orange", "avocado", "bananas", "pineapple"};
        Random r = new Random();
        return product[r.nextInt(product.length)];
    }

    private Product exampleProduct() {
        Product product = new Product();
        product.setName(exampleProductName());
        return product;
    }

    @BeforeEach
    void init() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        productDao = new ProductDao(dataSource);
    }

    @Test
    void shouldListInsertedProducts() throws SQLException {
        Product product = exampleProduct();
        productDao.insert(product);
        assertThat(productDao.list())
                .extracting(Product::getName)
                .contains(product.getName());
    }
}