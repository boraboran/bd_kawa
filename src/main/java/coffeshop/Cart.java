package coffeshop;

import java.sql.*;
import java.util.Scanner;

/**
 * Klasa reprezentująca koszyk.
 */
public class Cart {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffeeshop";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "...";

    static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metoda dodająca produkty do koszyka.
     * @throws SQLException
     */
    public static void addProductToCart() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Podaj id kawy, którą chcesz dodać do koszyka.");
        int coffeeId = scan.nextInt();
        System.out.println("Podaj liczbę paczek kawy, które chcesz zamówić.");
        int bags = scan.nextInt();

        String selectSql = "SELECT number_of_bags FROM coffee_data WHERE id_coffee = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setInt(1, coffeeId);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    int availableBags = resultSet.getInt("number_of_bags");
                    if (bags <= availableBags) {
                        String sql = "INSERT INTO cart (client_id, coffee_id, number_of_bags) VALUES (?, ?, ?)";
                        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                            statement.setInt(1, Login.getClientId());
                            statement.setInt(2, coffeeId);
                            statement.setInt(3, bags);

                            int rowsInserted = statement.executeUpdate();
                            if (rowsInserted > 0) {
                                System.out.println("Dodano produkt do koszyka.");
                                updateCoffeeData(coffeeId, availableBags - bags);
                            } else {
                                System.out.println("Wystąpił błąd podczas dodawania produktu do koszyka.");
                            }
                        }
                    } else {
                        System.out.println("Nie ma wystarczającej liczby paczek kawy w magazynie.");
                    }
                } else {
                    System.out.println("Nie znaleziono kawy o podanym ID.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Metoda aktualizująca bazę danych i usuwająca produkty z koszyka.
     * @param coffeeId ID kawy
     * @param newBags liczba paczek kawy
     * @throws SQLException
     */
    private static void updateCoffeeData(int coffeeId, int newBags) throws SQLException {
        String sql = "UPDATE coffee_data SET number_of_bags = ? WHERE id_coffee = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newBags);
            statement.setInt(2, coffeeId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Zaktualizowano liczbę paczek kawy w bazie danych.");
            } else {
                System.out.println("Nie ma wystarczającej liczby paczek kawy dostępnej w bazie danych.");
            }
        }
    }

    /**
     * Metoda wyświetlająca zawartość koszyka.
     */
    public static void viewCart() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT cart_id, client_id, coffee_id, number_of_bags FROM cart";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                System.out.println("Zawartość koszyka:");
                while (resultSet.next()) {
                    int cartId = resultSet.getInt("cart_id");
                    int clientId = resultSet.getInt("client_id");
                    int coffeeId = resultSet.getInt("coffee_id");
                    int numberOfBags = resultSet.getInt("number_of_bags");
                    System.out.println("Id koszyka: " + cartId + ", Id klienta: " + clientId +
                            ", Id kawy: " + coffeeId + ", Liczba paczek: " + numberOfBags);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Metoda usuwająca zawartość koszyka.
     */
    public static void emptyCart() {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            callableStatement = connection.prepareCall("{CALL delete_cart_contents()}");
            callableStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (callableStatement != null) {
                    callableStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
