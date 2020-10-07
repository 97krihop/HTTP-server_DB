package database;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ProductDaoTest {

    @Test
    void shouldListInsertedProducts() {
        ProductDao productDao = new ProductDao();
        String product = exampleProduct();
        productDao.insert(product);
        assertThat(productDao.list()).contains(product);
    }

    private String exampleProduct() {
        String[] product = {"apples", "orange", "avocado", "bananas", "pineapple"};
        Random r = new Random();
        return product[r.nextInt(product.length)];
    }
}