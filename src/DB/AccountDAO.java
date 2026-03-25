package DB;
import java.io.*;
import java.util.List;
import model.Account;

public class AccountDAO {
    private static final String FILE = "accounts.txt";

    public static void saveAccounts(List<Account> accounts) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Account a : accounts) {
                pw.println(a.getId() + " " + a.getBalance());
            }
            System.out.println("Data saved to " + FILE);
        } catch (IOException e) {
            System.err.println("Save Error: " + e.getMessage());
        }
    }
}