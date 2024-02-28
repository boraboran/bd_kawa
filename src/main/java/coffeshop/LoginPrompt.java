package coffeshop;

import java.sql.*;

/**
 * Klasa reprezentująca logowanie.
 */
public class LoginPrompt {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffeeshop";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "...";

    /**
     * Metoda sprawdzająca poprawność logowania.
     *
     * @param statement statement
     * @param username login
     * @param password hasło
     */
    public static void loginPrompt(Statement statement, String username, String password) {
        Login login = new Login();
        String selectSql = "SELECT idclient FROM client WHERE login = ?";
        
        if (login.login(username, password, statement)) {
            System.out.println("Logowanie powiodło się!");

        } else {
            System.out.println("Logowanie nie powiodło się. Sprawdź swój login i hasło.");
            System.exit(0);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, username);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    resultSet.getInt("idclient");
                } else {
                    System.out.println("Nie znaleziono klienta o podanej nazwie użytkownika.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}