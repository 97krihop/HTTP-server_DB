package database;

import org.postgresql.ds.PGSimpleDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ProductDao {

    private ArrayList <String> products = new ArrayList <>();

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/shop_app");
        dataSource.setUser("kristianiashop");
        dataSource.setPassword("PzBhrjmtoAaC");

        System.out.println("Please enter product name:");
        Scanner scanner = new Scanner(System.in);
        String product = scanner.nextLine();

        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("insert into products (name) values (?)")){
                statement.setString(1, product);
                statement.executeUpdate();
            }
        }

        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("select * from products")){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        System.out.println(rs.getString("name"));
                    }
                }
            }
        }
    }

    public void insert(String product) {
        products.add(product);
    }

    public List <String> list() {
        return products;
    }
}
