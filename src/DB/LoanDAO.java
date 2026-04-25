package DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Loan;

public class LoanDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/city_bank";
    private static final String USER = "root"; 
    private static final String PASS = "Root"; 

    public static void ensureLoanSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS loans (" +
                     "loan_id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "account_no INT, " +
                     "loan_type VARCHAR(50), " +
                     "principal DOUBLE, " +
                     "interest_rate DOUBLE, " +
                     "months INT, " +
                     "emi DOUBLE, " +
                     "status VARCHAR(20), " +
                     "FOREIGN KEY (account_no) REFERENCES accounts(account_no) ON DELETE CASCADE" +
                     ")";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Loan Schema Error: " + e.getMessage());
        }
    }

    public static void applyLoan(Loan loan) {
        ensureLoanSchema();
        String sql = "INSERT INTO loans (account_no, loan_type, principal, interest_rate, months, emi, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loan.getAccountNo());
            ps.setString(2, loan.getLoanType());
            ps.setDouble(3, loan.getPrincipal());
            ps.setDouble(4, loan.getInterestRate());
            ps.setInt(5, loan.getMonths());
            ps.setDouble(6, loan.getEmi());
            ps.setString(7, loan.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Apply Loan Error: " + e.getMessage());
        }
    }

    public static List<Loan> getPendingLoans() {
        ensureLoanSchema();
        List<Loan> pendingLoans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE status = 'PENDING'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pendingLoans.add(new Loan(
                    rs.getInt("loan_id"),
                    rs.getInt("account_no"),
                    rs.getString("loan_type"),
                    rs.getDouble("principal"),
                    rs.getDouble("interest_rate"),
                    rs.getInt("months"),
                    rs.getDouble("emi"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Get Pending Loans Error: " + e.getMessage());
        }
        return pendingLoans;
    }

    public static void approveLoan(int loanId) {
        String sql = "UPDATE loans SET status = 'APPROVED' WHERE loan_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Approve Loan Error: " + e.getMessage());
        }
    }

    public static List<Loan> getLoansForUser(int accountNo) {
        ensureLoanSchema();
        List<Loan> userLoans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE account_no = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userLoans.add(new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("account_no"),
                        rs.getString("loan_type"),
                        rs.getDouble("principal"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("months"),
                        rs.getDouble("emi"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get User Loans Error: " + e.getMessage());
        }
        return userLoans;
    }
}
