package main;
import gui.LoginFrame;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import model.Account;

public class BankingApp {
    public static void main(String[] args) {
        List<Account> accounts = Arrays.asList(
            new Account(1, 5000.0),
            new Account(2, 3000.0),
            new Account(3, 8500.0)
        );

        SwingUtilities.invokeLater(() -> {
            @SuppressWarnings("unused")
            LoginFrame app = new LoginFrame(accounts);
        });
    }
}