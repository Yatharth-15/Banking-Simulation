package DB;

import java.io.*;
import java.util.*;
import model.Account;

public class AccountDAO {
    private static final String FILE = "accounts.txt";

    public static void saveAccounts(List<Account> accounts) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Account a : accounts) {
                pw.println(a.getAccountNo() + "," 
                + a.getName() + ","
                + a.getPassword() + ","
                + a.getBalance() + ","
                + a.getPin() + "," 
                + a.isBlocked() + ","
                + a.getLockTime());
            }
        } catch (IOException e) {
            System.err.println("Database Save Error: " + e.getMessage());
        }
    }

    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        File file = new File(FILE);
        if (!file.exists()) return accounts;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length == 7) {
                    Account acc = new Account(Integer.parseInt(p[0]), p[1], p[2], p[4], Double.parseDouble(p[3]));
                    acc.setBlocked(Boolean.parseBoolean(p[5]));
                    acc.setLockTime(Long.parseLong(p[6]));
                    accounts.add(acc);
                }
            }
        } catch (Exception e) {
            System.err.println("Database Load Error: " + e.getMessage());
        }
        return accounts;
    }
}