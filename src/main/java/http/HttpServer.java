package http;

import database.Product;
import database.ProductDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private static ProductDao productDao;
    private File contentRoot;

    public HttpServer(int port) throws IOException {
        // Opens a entry point to our program for network clients
        ServerSocket serverSocket = new ServerSocket(port);
        //connect database
        if(productDao == null){
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
            Flyway.configure().dataSource(dataSource).load().migrate();
            productDao = new ProductDao(dataSource);
        }


        // new Threads executes the code in a separate "thread", that is: In parallel
        new Thread(() -> { // anonymous function with code that will be executed in parallel
            while(true){
                try{
                    // accept waits for a client to try to connect - blocks
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                }catch(IOException | SQLException e){
                    // If something went wrong - print out exception and try again
                    e.printStackTrace();
                }
            }
        }).start(); // Start the threads, so the code inside executes without block the current thread
    }

    // This code will be executed for each client
    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);
        // Example "GET /echo?body=hello HTTP/1.1"

        // Example GET, POST, PUT, DELETE etc
        String requestMethod = requestLine.split(" ")[0];

        String requestTarget = requestLine.split(" ")[1];
        // Example "/echo?body=hello"

        int questionPos = requestTarget.indexOf('?');

        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if(requestMethod.equals("POST")){
            QueryString requestParameter = new QueryString(request.getBody());
            Product product = new Product(requestParameter.getParameter("productName"));
            productDao.insert(product);
            String body = "Okay";
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "\r\n" +
                    body;
            // Write the response back to the client
            clientSocket.getOutputStream().write(response.getBytes());
        }else{
            if(requestPath.equals("/echo")){
                handleEchoRequest(clientSocket, requestTarget, questionPos);
            }else if(requestPath.equals("/api/products")){
                handleGetProducts(clientSocket);
            }else{
                if(requestPath.equals("/")) requestPath = "/index.html";
                File file = new File(contentRoot, requestPath);
                if(!file.exists()){
                    String body = file + " does not exist";
                    String response = "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: " + body.length() + "\r\n" +
                            "\r\n" +
                            body;
                    // Write the response back to the client
                    clientSocket.getOutputStream().write(response.getBytes());
                    return;
                }
                String statusCode = "200";
                String contentType = "text/plain";
                if(file.getName().endsWith(".html")){
                    contentType = "text/html";
                }
                String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "\r\n";
                // Write the response back to the client
                clientSocket.getOutputStream().write(response.getBytes());

                new FileInputStream(file).transferTo(clientSocket.getOutputStream());
            }
        }
    }

    private void handleGetProducts(Socket clientSocket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder("<ul>");
        for(Product productName : productDao.list()){
            body.append("<li>").append(productName.getName()).append("</li>");
        }
        body.append("</ul>");
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    private void handleEchoRequest(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if(questionPos != -1){
            // body=hello
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if(queryString.getParameter("status") != null){
                statusCode = queryString.getParameter("status");
            }
            if(queryString.getParameter("body") != null){
                body = queryString.getParameter("body");
            }
        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/shop_app");
        dataSource.setUser("kristianiashop");
        dataSource.setPassword("PzBhrjmtoAaC");
        productDao = new ProductDao(dataSource);

        HttpServer server = new HttpServer(8080);
        server.setContentRoot(new File("src/main/resources"));
    }

    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }

    public List <String> getProductNames() throws SQLException {
        List<String> res = new ArrayList<>();
        for(Product product:productDao.list()){
            res.add(product.getName());
        }
        return res;
    }
}
