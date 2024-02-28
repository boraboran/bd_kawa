package coffeshop;
import java.sql.*;

public class Login {
    private static int clientId;

    /**
     * Metoda sprawdzająca poprawność logowania.
     * @param login login
     * @param password hasło
     * @param statement statement
     * @return
     */
    public boolean login(String login, String password, Statement statement) {
        try {
            String sql = "SELECT * FROM client WHERE login='" + login + "' AND password='" + password + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                clientId = resultSet.getInt("idclient");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }

    /**
     * Metoda zwracająca id klienta.
     * @return id klienta
     */
    public static int getClientId() {
        return clientId;
    }
}