package DB;

import java.sql.*;
import java.util.*;
import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;

public class AccountDAO {
    // db details
    private static final String URL = "jdbc:mysql://localhost:3306/city_bank";
    private static final String USER = "root"; 
    private static final String PASS = "Root"; 

    public static void ensureSchema() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE accounts ADD COLUMN daily_withdrawn DOUBLE DEFAULT 0.0");
            stmt.executeUpdate("ALTER TABLE accounts ADD COLUMN last_transaction_date VARCHAR(255)");
        } catch (SQLException e) {
            // Ignore if columns already exist
        }
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE accounts ADD COLUMN acc_type VARCHAR(20) DEFAULT 'SAVINGS'");
        } catch (SQLException e) {
            // Ignore
        }
    }

    public static void saveAccounts(List<Account> accounts) {
        ensureSchema();
       
        String sql = "INSERT INTO accounts (account_no, name, password, balance, pin, is_blocked, lock_time, daily_withdrawn, last_transaction_date, acc_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE balance=?, is_blocked=?, lock_time=?, daily_withdrawn=?, last_transaction_date=?, acc_type=?";

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
                ps.setDouble(8, a.getDailyWithdrawn());
                ps.setString(9, a.getLastTransactionDate());
                ps.setString(10, a.getAccountType());
                

                ps.setDouble(11, a.getBalance());
                ps.setBoolean(12, a.isBlocked());
                ps.setLong(13, a.getLockTime());
                ps.setDouble(14, a.getDailyWithdrawn());
                ps.setString(15, a.getLastTransactionDate());
                ps.setString(16, a.getAccountType());
                
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("db updated.");
        } catch (SQLException e) {
            System.err.println("error saving: " + e.getMessage());
        }
    }

    // gets all accounts
    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String accType = "SAVINGS";
                try {
                    accType = rs.getString("acc_type");
                } catch (SQLException ignore) {}

                Account acc;
                if ("CURRENT".equals(accType)) {
                    acc = new CurrentAccount(
                        rs.getInt("account_no"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("pin"),
                        rs.getDouble("balance")
                    );
                } else {
                    acc = new SavingsAccount(
                        rs.getInt("account_no"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("pin"),
                        rs.getDouble("balance")
                    );
                }
                acc.setBlocked(rs.getBoolean("is_blocked"));
                acc.setLockTime(rs.getLong("lock_time"));
                
                try {
                    acc.setDailyWithdrawn(rs.getDouble("daily_withdrawn"));
                    acc.setLastTransactionDate(rs.getString("last_transaction_date"));
                } catch (SQLException ignore) { } 
                
                accounts.add(acc);
            }
        } catch (SQLException e) {
            System.err.println("load error: " + e.getMessage());
        }
        return accounts;
    } 

    public static void deleteAccount(int accountNo) {
        String sql = "DELETE FROM accounts WHERE account_no = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNo);
            ps.executeUpdate();
            System.out.println("account " + accountNo + " deleted");
        } catch (SQLException e) {
            System.err.println("MySQL Delete Error: " + e.getMessage());
        }
    }
}