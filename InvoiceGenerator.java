package Assignment1;

import java.sql.*;
import java.util.Scanner;

public class InvoiceGenerator {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/wipro";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Manish@#123";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            Scanner sc = new Scanner(System.in);

            System.out.println("Enter Customer Name:");
            String customerName = sc.next();

            System.out.println("Select Product:");
            System.out.println("1. Laptop");
            System.out.println("2. Smartphone");
            System.out.println("3. Tablet");
            System.out.println("4. Headphones");
            System.out.print("Enter your choice (1-4): ");
            int productChoice = sc.nextInt();
            
            String productName;
            switch (productChoice) {
                case 1:
                    productName = "Laptop";
                    break;
                case 2:
                    productName = "Smartphone";
                    break;
                case 3:
                    productName = "Tablet";
                    break;
                case 4:
                    productName = "Headphones";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid product choice.");
            }

            System.out.println("Enter Quantity:");
            int quantity = sc.nextInt();

            int customerId = createCustomer(connection, customerName);
            int productId = getProductId(connection, productName);
            double productPrice = getProductPrice(connection, productId);
            double totalCost = productPrice * quantity;

            createInvoice(connection, customerId, customerName, productId, productName, quantity, totalCost);

            System.out.println("Invoice created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int createCustomer(Connection connection, String name) throws SQLException {
        String insertCustomerSQL = "INSERT INTO customers (name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    private static int getProductId(Connection connection, String name) throws SQLException {
        String selectProductSQL = "SELECT id FROM products WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectProductSQL)) {
            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    throw new SQLException("Product not found.");
                }
            }
        }
    }

    private static double getProductPrice(Connection connection, int productId) throws SQLException {
        String selectPriceSQL = "SELECT price FROM products WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectPriceSQL)) {
            preparedStatement.setInt(1, productId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("price");
                } else {
                    throw new SQLException("Product price not found.");
                }
            }
        }
    }

    private static void createInvoice(Connection connection, int customerId, String customerName, int productId, String productName, int quantity, double totalCost) throws SQLException {
        String insertInvoiceSQL = "INSERT INTO invoices (customer_id, customer_name, product_id, product_name, quantity, total_cost) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertInvoiceSQL)) {
            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, customerName);
            preparedStatement.setInt(3, productId);
            preparedStatement.setString(4, productName);
            preparedStatement.setInt(5, quantity);
            preparedStatement.setDouble(6, totalCost);
            preparedStatement.executeUpdate();
        }
    }
}
