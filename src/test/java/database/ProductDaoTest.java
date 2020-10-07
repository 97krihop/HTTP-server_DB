package database;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ProductDaoTest {

    @Test
    void shouldListInsertedProducts() throws SQLException {
        ProductDao productDao = new ProductDao(getJdbcDataSource());
        String product = exampleProduct();
        productDao.insert(product);
        assertThat(productDao.list()).contains(product);
    }

    private JdbcDataSource getJdbcDataSource() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        try(Connection connection = dataSource.getConnection()){
            connection.prepareStatement("create table products (name varchar)").executeUpdate();
        }
        return dataSource;
    }

    private String exampleProduct() {
        String[] product = {"apples", "orange", "avocado", "bananas", "pineapple"};
        Random r = new Random();
        return product[r.nextInt(product.length)];
    }
}