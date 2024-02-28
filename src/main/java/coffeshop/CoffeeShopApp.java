package coffeshop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

import static coffeshop.Cart.*;
import static coffeshop.Order.viewOrders;

/**
 * Klasa reprezentująca aplikację.
 */
public class CoffeeShopApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffeeshop";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "...";

    public CoffeeShopApp() {
    }

    /**
     * Metoda uruchamiająca aplikację.
     */
    public static void start() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);

        System.out.println("===== LOGOWANIE =====");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Podaj login: ");
        String username = reader.readLine();
        System.out.print("Podaj hasło: ");
        String password = reader.readLine();
        LoginPrompt.loginPrompt(statement, username, password);

        System.out.println("====== COFFEE SHOP ======");
        handleOption();
    }

    /**
     * Metoda obsługująca wybór opcji z menu.
     * @throws SQLException wyjątek
     */
    public static void handleOption() throws SQLException {
        int choice = 1;
        while (choice != 6) {
            Menu.displayMain();
            Scanner scan = new Scanner(System.in);
            choice = scan.nextInt();
            switch (choice) {
                case 0 -> displayAvailableCoffees();
                case 1 -> searchCoffeeByParameters();
                case 2 -> addProductToCart();
                case 3 -> viewCart();
                case 4 -> Order.placeOrder();
                case 5 -> viewOrders();
                case 6 -> {
                    emptyCart();
                    logout();
                }
                default -> System.out.println("Nieprawidłowa opcja. Proszę wybrać ponownie.");
            }
        }
    }

    /**
     * Metoda wylogowująca użytkownika.
     * @throws SQLException wyjątek
     */
    public static void logout() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{CALL delete_cart_contents()}");
        callableStatement.execute();
        System.out.println("Usunięto zawartość koszyka.");
        Database.disconnect();
    }

    /**
     * Metoda wyświetlająca dostępne kawy.
     */
    private static void displayAvailableCoffees() {
        String availableCoffeesQuery = "SELECT * FROM available_coffees;";
        Database.displayMatchingData(availableCoffeesQuery);
    }
    /**
     * Metoda wyszukująca kawę po zadanych parametrach.
     */
    public static void searchCoffeeByParameters() {
        Menu.displayParameter();
        String parameterQuery = "SELECT id_coffee, country, region, owner, variety, number_of_bags, " +
                "bag_weight, aroma, acidity, sweetness, total_score, price FROM coffee_data WHERE ";

        Scanner scan = new Scanner(System.in);
        int choice = scan.nextInt();
        scan.nextLine();

        switch (choice) {
            case 1:
                System.out.println("Podaj kraj, z którego chcesz wyszukać kawy:");
                String countryName = scan.nextLine();
                String countryQuery = "country LIKE '" + countryName + "'";
                String finalQuery = parameterQuery + countryQuery;
                Database.displayMatchingData(finalQuery);
                break;
            case 2:
                System.out.println("Podaj region, z którego chcesz wyszukać kawy:");
                String regionName = scan.nextLine();
                String regionQuery = "region LIKE '" + regionName + "'";
                finalQuery = parameterQuery + regionQuery;
                Database.displayMatchingData(finalQuery);
                break;
            case 3:
                System.out.println("Podaj producenta, którego kawy chcesz wyszukać:");
                String ownerName = scan.nextLine();
                String ownerQuery = "owner LIKE '" + ownerName + "'";
                finalQuery = parameterQuery + ownerQuery;
                Database.displayMatchingData(finalQuery);
            case 4:
                System.out.println("Podaj typ kawy, jaki chcesz wyszukać:");
                String typeName = scan.nextLine();
                String typeQuery = "variety LIKE '" + typeName + "';";
                finalQuery = parameterQuery + typeQuery;
                Database.displayMatchingData(finalQuery);
                break;
            case 5:
                String rangedParameter = "bag_weight ";
                String range = chooseParameterRange();
                String sortOrder = chooseSortOrder();
                finalQuery = parameterQuery + rangedParameter + range + " ORDER BY bag_weight " + sortOrder;
                Database.displayMatchingData(finalQuery);
                break;
            case 6:
                rangedParameter = "aroma ";
                range = chooseParameterRange();
                sortOrder = chooseSortOrder();
                finalQuery = parameterQuery + rangedParameter + range + " ORDER BY aroma " + sortOrder;
                Database.displayMatchingData(finalQuery);
                break;
            case 7:
                rangedParameter = "acidity ";
                range = chooseParameterRange();
                sortOrder = chooseSortOrder();
                finalQuery = parameterQuery + rangedParameter + range + " ORDER BY acidity " + sortOrder;
                Database.displayMatchingData(finalQuery);
                break;
            case 8:
                rangedParameter = "sweetness ";
                range = chooseParameterRange();
                sortOrder = chooseSortOrder();
                finalQuery = parameterQuery + rangedParameter + range + " ORDER BY sweetness " + sortOrder;
                Database.displayMatchingData(finalQuery);
                break;
            case 9:
                rangedParameter = "total_score ";
                range = chooseParameterRange();
                sortOrder = chooseSortOrder();
                finalQuery = parameterQuery + rangedParameter + range + " ORDER BY total_score " + sortOrder;
                Database.displayMatchingData(finalQuery);
                break;
            case 10:
                rangedParameter = "price ";
                range = chooseParameterRange();
                sortOrder = chooseSortOrder();
                finalQuery = parameterQuery + rangedParameter + range + " ORDER BY price " + sortOrder;
                Database.displayMatchingData(finalQuery);
                break;
        }
    }

    /**
     * Metoda wybierająca zakres parametru.
     * @return fragment zapytania SQL
     */
    public static String chooseParameterRange() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Wyszukaj w zakresie od... (podaj liczbę)");
        String parameterRangeMin = scan.next();
        System.out.println("do... (podaj liczbę)");
        String parameterRangeMax = scan.next();
        System.out.println("Podany zakres: od " + parameterRangeMin + " do " + parameterRangeMax);
        return "BETWEEN " + parameterRangeMin + " AND " + parameterRangeMax;
    }

    /**
     * Metoda wybierająca kierunek sortowania.
     * @return fragment zapytania SQL
     */
    public static String chooseSortOrder() {
        System.out.println("Wybierz sortowanie według wartości parametru");
        System.out.println("1 - rosnące");
        System.out.println("2 - malejące");
        Scanner scan = new Scanner(System.in);
        int sortChoice = scan.nextInt();
        if (sortChoice == 1) {
            return "ASC;";
        } else if (sortChoice == 2) {
            return "DESC;";
        } else {
            System.out.println("Niepoprawna opcja! Spróbuj ponownie.");
            return null;
        }
    }

}
