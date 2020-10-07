package database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ProductDaoTest {

    private JdbcDataSource getJdbcDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

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

    @Test
    void shouldListInsertedProducts() throws SQLException {
        ProductDao productDao = new ProductDao(getJdbcDataSource());
        Product product = exampleProduct();
        productDao.insert(product);
       assertThat(productDao.list())
                .extracting(Product::getName)
                .contains(product.getName());
    }
}