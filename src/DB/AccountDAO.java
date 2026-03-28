package DB;

import model.Account;
import java.io.*;
import java.util.*;

public class AccountDAO {
    private static final String FILE = "accounts.txt";

    /**
     * SAVE: Converts the List of Account objects into strings and writes to file.
     * Format in file: accountNo,name,password,balance
     */
    public static void saveAccounts(List<Account> accounts) {
        // Using try-with-resources to ensure the file closes automatically
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Account a : accounts) {
                // Writing data separated by commas
                pw.println(a.getAccountNo() + "," + 
                           a.getName() + "," + 
                           a.getPassword() + "," + 
                           a.getBalance());
            }
            System.out.println("[Database] All accounts synced to " + FILE);
        } catch (IOException e) {
            System.err.println("[Database] Critical Save Error: " + e.getMessage());
        }
    }

    /**
     * LOAD: Reads the file, splits each line by commas, and recreates Account objects.
     */
    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        File file = new File(FILE);

        // Check if file exists to prevent errors on first run
        if (!file.exists()) {
            System.out.println("[Database] No existing data found. Starting fresh.");
            return accounts;
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue; // Skip empty lines

                // Split the line by the comma separator
                String[] parts = line.split(",");
                
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String password = parts[2];
                    double balance = Double.parseDouble(parts[3]);

                    // Reconstruct the Account object
                    accounts.add(new Account(id, name, password, balance));
                }
            }
            System.out.println("[Database] Successfully loaded " + accounts.size() + " accounts.");
        } catch (Exception e) {
            System.err.println("[Database] Load Error: " + e.getMessage());
        }
        return accounts;
    }
}