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
            System.out.println("[System] No data found. Initializing Admin Account...");
           
            accounts.add(new Account(101, "Admin User", "admin123", "0000", 5000.0));
        
            AccountDAO.saveAccounts(accounts);
        }
        SwingUtilities.invokeLater(() -> {
            new LoginFrame(accounts);
            System.out.println("[System] GUI Launched Successfully.");
        });
    }
}