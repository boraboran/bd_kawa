package coffeshop;

import java.sql.*;
import java.util.Scanner;

import static coffeshop.Login.getClientId;
import static coffeshop.Menu.displayDelivery;
import static coffeshop.Menu.displayPayment;

/**
 * Klasa zawierająca metody do realizacji zamówienia.
 */
public class Order {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffeeshop";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "...";

    /**
     * Metoda służąca do złożenia zamówienia.
     */
    public static void placeOrder() {
        Connection connect;
        Statement statement;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            Scanner scan = new Scanner(System.in);
            int clientId = getClientId();
            displayDelivery();
            String deliveryMethod = scan.next();
            displayPayment();
            String paymentMethod = scan.next();
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = connect.createStatement();

            resultSet = statement.executeQuery("SELECT is_premium FROM client WHERE idclient = " + clientId);
            boolean isPremium = false;
            while (resultSet.next()) {
                isPremium = resultSet.getBoolean("is_premium");
            }

            resultSet = statement.executeQuery("SELECT SUM(c.price * cart.number_of_bags) AS total_amount " +
                    "FROM coffee_data c " +
                    "INNER JOIN cart ON cart.coffee_id = c.id_coffee " +
                    "WHERE cart.client_id = " + clientId);
            float totalAmount = 0;
            while (resultSet.next()) {
                totalAmount = resultSet.getFloat("total_amount");
            }

            if (isPremium) {
                totalAmount = totalAmount * 0.9f;
            }

            float deliveryCost = switch (deliveryMethod) {
                case "poczta" -> 10;
                case "paczkomat" -> 12;
                case "kurier" -> 15;
                default -> 0;
            };

            totalAmount = totalAmount + deliveryCost;

            preparedStatement = connect.prepareStatement("INSERT INTO order_summary " +
                    "(client_id, total_price, payment_method, delivery_method) " +
                    "VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, clientId);
            preparedStatement.setFloat(2, totalAmount);
            preparedStatement.setString(3, paymentMethod);
            preparedStatement.setString(4, deliveryMethod);
            preparedStatement.executeUpdate();

            int orderId = 0;
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
            while (resultSet.next()) {
                orderId = resultSet.getInt(1);
            }

            preparedStatement = connect.prepareStatement("INSERT INTO order_coffee " +
                    "(order_coffee_id, order_id, coffee_id, number_of_bags) " +
                    "SELECT cart_id, ?, coffee_id, number_of_bags " +
                    "FROM cart " +
                    "WHERE client_id = ?");
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, clientId);
            preparedStatement.executeUpdate();
            preparedStatement = connect.prepareStatement("DELETE FROM cart WHERE client_id = ?");
            preparedStatement.setInt(1, clientId);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda służąca do wyświetlenia zamówień klienta.
     */
    public static void viewOrders() {
        try {
            Connection connection;
            Statement statement;

            int clientId = getClientId();

            String sql = "SELECT * FROM order_summary WHERE client_id=" + clientId;
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                int client_id = resultSet.getInt("client_id");
                double totalPrice = resultSet.getDouble("total_price");
                String paymentMethod = resultSet.getString("payment_method");
                String deliveryMethod = resultSet.getString("delivery_method");

                System.out.println("ID zamówienia: " + orderId);
                System.out.println("ID klienta: " + client_id);
                System.out.println("Całkowity koszt: " + totalPrice);
                System.out.println("Metoda płatności: " + paymentMethod);
                System.out.println("Metoda dostawy: " + deliveryMethod);
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
}
