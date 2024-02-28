package coffeshop;

/**
 * Klasa Menu zawiera metody wyświetlające menu.
 */
public class Menu {
    /**
     * Metoda wyświetlająca menu główne.
     */
    static void displayMain() {
        System.out.println("Wybierz opcję z menu.");
        System.out.println("0 - Wyświetl wszystkie dostępne kawy.");
        System.out.println("1 - Wyszukaj kawę według parametrów.");
        System.out.println("2 - Dodaj produkt do koszyka.");
        System.out.println("3 - Zobacz stan swojego koszyka.");
        System.out.println("4 - Złóż zamówienie.");
        System.out.println("5 - Zobacz swoje zamówienia.");
        System.out.println("6 - Wyloguj się.");
    }

    /**
     * Metoda wyświetlająca menu wyszukiwania kawy.
     */
    static void displayParameter() {
        System.out.println("Wybierz parametr, który cię interesuje.");
        System.out.println("1 - kraj pochodzenia");
        System.out.println("2 - rejon pochodzenia");
        System.out.println("3 - producent");
        System.out.println("4 - typ");
        System.out.println("5 - masa paczki [g]");
        System.out.println("6 - aromat (0 - 10)");
        System.out.println("7 - kwasowość (0 - 10)");
        System.out.println("8 - słodycz (0 - 10)");
        System.out.println("9 - ocena kawy (0 - 100)");
        System.out.println("10 - cena za kilogram kawy w USD");
    }

    /**
     * Metoda wyświetlająca menu wyboru metody płatności.
     */
    static void displayPayment() {
        System.out.println("Wpisz metodę płatności.");
        System.out.println("karta");
        System.out.println("przelew");
        System.out.println("BLIK");
    }
    /**
     * Metoda wyświetlająca menu wyboru metody dostawy.
     */
    static void displayDelivery() {
        System.out.println("Wpisz metodę dostawy.");
        System.out.println("poczta (koszt: 10 zł)");
        System.out.println("paczkomat (koszt: 12 zł)");
        System.out.println("kurier (koszt: 15 zł)");
    }
}
