package DB;

import java.io.*;
import java.util.*;
import model.Account;

public class AccountDAO {
    private static final String FILE = "accounts.txt";

   
    public static void saveAccounts(List<Account> accounts) {
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Account a : accounts) {
               
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

    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        File file = new File(FILE);

    
        if (!file.exists()) {
            System.out.println("[Database] No existing data found. Starting fresh.");
            return accounts;
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue; 

               
                String[] parts = line.split(",");
                
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String password = parts[2];
                    double balance = Double.parseDouble(parts[3]);

                    
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