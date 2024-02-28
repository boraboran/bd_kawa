package coffeshop;

import java.sql.*;

/**
 * Klasa odpowiedzialna za połączenie z bazą danych.
 * @author Anna Borowa
 */
public class Database {
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
     * Konstruktor klasy Database.
     * @throws SQLException wyjątek
     */
    public Database() throws SQLException {
    }

    /**
     * Metoda zamykająca połączenie z bazą danych.
     * @throws SQLException wyjątek
     */
    static void disconnect() throws SQLException {
        connection.close();
        System.out.println("Połączenie z bazą danych zostało zamknięte");
    }
    /**
     * Metoda zwracająca wyniki pasujące do zapytania.
     */
    static void displayMatchingData(String finalQuery) {
        int numberOfBags = 0;
        System.out.println(finalQuery);
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(finalQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt("id_coffee");
                String country = resultSet.getString("country");
                String region = resultSet.getString("region");
                String owner = resultSet.getString("owner");
                String type = resultSet.getString("variety");
                numberOfBags = resultSet.getInt("number_of_bags");
                int bagWeight = resultSet.getInt("bag_weight");
                int aroma = resultSet.getInt("aroma");
                int acidity = resultSet.getInt("acidity");
                int sweetness = resultSet.getInt("sweetness");
                int totalScore = resultSet.getInt("total_score");
                float price = resultSet.getFloat("price");
                System.out.println("ID: " + id + " Kraj: " + country + " Region: "
                        + region + " Producent: " + owner + " Typ: " + type
                        + " Ilość paczek: " + numberOfBags + " Waga paczki: " + bagWeight + "g Aromat: " + aroma
                        + " Kwasowość: " + acidity + " Słodycz: " + sweetness + " Ocena: " + totalScore
                        + " Cena za kilogram: " + price + " USD");
            }
        } catch (SQLException e) {
            System.out.println("Wystąpił błąd podczas połączenia z bazą danych: " + e.getMessage());
        }

        if (numberOfBags == 0) {
            System.out.println("Nie znaleziono kawy spełniającej podane kryteria.");
        }
    }
}
