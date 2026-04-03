package DB;

import model.Account;
import java.io.*;
import java.util.*;

public class AccountDAO {
    private static final String FILE = "accounts.txt";

    
    public static void saveAccounts(List<Account> accounts) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Account a : accounts) {
             
                pw.println(a.getAccountNo() + "," + 
                           a.getName() + "," + 
                           a.getPassword() + "," + 
                           a.getBalance() + "," + 
                           a.getPin());
            }
            System.out.println("[Database] Sync Complete: All data secured in " + FILE);
        } catch (IOException e) {
            System.err.println("[Database] Save Error: " + e.getMessage());
        }
    }

    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        File file = new File(FILE);

        if (!file.exists()) {
            System.out.println("[Database] No file found. Creating fresh system.");
            return accounts;
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                
                // We now check for 5 parts instead of 4
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String pass = parts[2];
                    double bal = Double.parseDouble(parts[3]);
                    String pin = parts[4]; // The 4-digit PIN

                    accounts.add(new Account(id, name, pass, pin, bal));
                }
            }
            System.out.println("[Database] Loaded " + accounts.size() + " secure accounts.");
        } catch (Exception e) {
            System.err.println("[Database] Load Error: " + e.getMessage());
        }
        return accounts;
    }
}