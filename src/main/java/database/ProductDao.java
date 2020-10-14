package database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ProductDao {

    private final DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/shop_app");
        dataSource.setUser("kristianiashop");
        dataSource.setPassword("PzBhrjmtoAaC");

        ProductDao productDao = new ProductDao(dataSource);

        System.out.println("Please enter product name:");
        Scanner scanner = new Scanner(System.in);
        String productName = scanner.nextLine();
        Product prod = new Product();
        prod.setName(productName);
        productDao.insert(prod);
        for(Product product : productDao.list()){
            System.out.println(product.getName());
        }
    }

    public void insert(Product product) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("insert into products (name) values (?)")){
                statement.setString(1, product.getName());
                statement.executeUpdate();
            }
        }
    }

    public List <Product> list() throws SQLException {
        List <Product> products = new ArrayList <>();
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("select * from products")){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        Product product = new Product();
                        product.setName(rs.getString("name"));
                        products.add(product);
                    }
                }
            }
        }
        return products;
    }

    public Product retrieve(int id) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("select * from products")){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        if(Integer.parseInt(rs.getString("ID")) == id){
                            Product product = new Product();
                            product.setName(rs.getString("name"));
                            return product;
                        }
                    }
                }
            }
        }


        return null;
    }
}
