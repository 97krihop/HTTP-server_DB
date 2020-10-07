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

    private DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    public void insert(String product) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("insert into products (name) values (?)")){
                statement.setString(1, product);
                statement.executeUpdate();
            }
        }
    }

    public List <String> list() throws SQLException {
        List<String> result = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("select * from products")){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        result.add(rs.getString("name"));
                    }
                }
            }
        }
        return result;
    }
}
