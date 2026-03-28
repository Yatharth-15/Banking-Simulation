package main;

import DB.AccountDAO;
import gui.LoginFrame;
import java.util.List;
import javax.swing.SwingUtilities;
import model.Account;

public class BankingApp {
    public static void main(String[] args) {
        List<Account> accounts = AccountDAO.loadAccounts();

      
        if (accounts.isEmpty()) {
            System.out.println("[System] Initializing database with default account...");
          
            accounts.add(new Account(101, "Admin User", "pass123", 5000.0));
            AccountDAO.saveAccounts(accounts);
        }

       
        SwingUtilities.invokeLater(() -> {
            @SuppressWarnings("unused")
            LoginFrame login = new LoginFrame(accounts);
        });
    }
}