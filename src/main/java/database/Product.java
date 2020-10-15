package database;

public class Product {
    private String name;
    private int id;

    public Product(String name) {
        this.name = name;
    }

    public Product(){

    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
