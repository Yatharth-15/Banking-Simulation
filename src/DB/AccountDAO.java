package DB;

import java.sql.*;
import java.util.*;
import model.Account;

public class AccountDAO {
    // Database Credentials
    private static final String URL = "jdbc:mysql://localhost:3306/city_bank";
    private static final String USER = "root"; 
    private static final String PASS = "Root"; 

    
    public static void saveAccounts(List<Account> accounts) {
       
        String sql = "INSERT INTO accounts (account_no, name, password, balance, pin, is_blocked, lock_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE balance=?, is_blocked=?, lock_time=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps= conn.prepareStatement(sql)) {
            
            for (Account a : accounts) {
                ps.setInt(1, a.getAccountNo());
                ps.setString(2, a.getName());
                ps.setString(3, a.getPassword());
                ps.setDouble(4, a.getBalance());
                ps.setString(5, a.getPin());
                ps.setBoolean(6, a.isBlocked());
                ps.setLong(7, a.getLockTime());
                
                // For the "Update" part of the query
                ps.setDouble(8, a.getBalance());
                ps.setBoolean(9, a.isBlocked());
                ps.setLong(10, a.getLockTime());
                
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("[Database] MySQL Sync Complete.");
        } catch (SQLException e) {
            System.err.println("MySQL Save Error: " + e.getMessage());
        }
    }

    // Method to load all accounts from MySQL
    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Account acc = new Account(
                    rs.getInt("account_no"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getString("pin"),
                    rs.getDouble("balance")
                );
                acc.setBlocked(rs.getBoolean("is_blocked"));
                acc.setLockTime(rs.getLong("lock_time"));
                accounts.add(acc);
            }
        } catch (SQLException e) {
            System.err.println("MySQL Load Error: " + e.getMessage());
        }
        return accounts;
    }
}