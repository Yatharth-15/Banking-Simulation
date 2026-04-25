package gui;

import DB.AccountDAO;
import DB.LoanDAO;
import model.Account;
import model.Loan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminFrame extends JFrame {
    private final List<Account> allAccounts;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public AdminFrame(List<Account> accounts) {
        this.allAccounts = accounts;

        setTitle("City Bank - Admin Panel");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table Setup
        String[] columns = {"Account ID", "Name", "Account Type", "Balance", "Blocked Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        refreshTable();
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton unblockBtn = new JButton("Unblock Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton approveLoansBtn = new JButton("Pending Loans");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(unblockBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(approveLoansBtn);
        buttonPanel.add(logoutBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        unblockBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                for (Account a : allAccounts) {
                    if (a.getAccountNo() == id) {
                        a.setBlocked(false);
                        a.resetFailedAttempts();
                        AccountDAO.saveAccounts(allAccounts);
                        JOptionPane.showMessageDialog(this, "Account " + id + " unblocked.");
                        refreshTable();
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select an account to unblock.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                if (id == 101) {
                    JOptionPane.showMessageDialog(this, "Cannot delete Admin account!");
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Account " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    AccountDAO.deleteAccount(id);
                    allAccounts.removeIf(a -> a.getAccountNo() == id);
                    JOptionPane.showMessageDialog(this, "Account deleted.");
                    refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select an account to delete.");
            }
        });

        approveLoansBtn.addActionListener(e -> {
            List<Loan> pending = LoanDAO.getPendingLoans();
            if (pending.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No pending loan applications.");
                return;
            }
            
            String[] lCols = {"Loan ID", "Account ID", "Type", "Principal", "EMI"};
            DefaultTableModel lModel = new DefaultTableModel(lCols, 0);
            for (Loan l : pending) {
                lModel.addRow(new Object[]{l.getLoanId(), l.getAccountNo(), l.getLoanType(), l.getPrincipal(), String.format("%.2f", l.getEmi())});
            }
            JTable lTable = new JTable(lModel);
            
            JPanel lPanel = new JPanel(new BorderLayout());
            lPanel.add(new JScrollPane(lTable), BorderLayout.CENTER);
            
            int res = JOptionPane.showConfirmDialog(this, lPanel, "Pending Loans - Select and Approve", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                int row = lTable.getSelectedRow();
                if (row != -1) {
                    int loanId = (int) lModel.getValueAt(row, 0);
                    int accNo = (int) lModel.getValueAt(row, 1);
                    double principal = (double) lModel.getValueAt(row, 3);
                    
                    LoanDAO.approveLoan(loanId);
                    
                    for (Account a : allAccounts) {
                        if (a.getAccountNo() == accNo) {
                            a.deposit(principal);
                            AccountDAO.saveAccounts(allAccounts);
                            JOptionPane.showMessageDialog(this, "Loan Approved! ₹" + principal + " deposited into Account " + accNo);
                            refreshTable();
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No loan selected.");
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            AccountDAO.saveAccounts(allAccounts);
            new LoginFrame(allAccounts);
            this.dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Account a : allAccounts) {
            tableModel.addRow(new Object[]{
                a.getAccountNo(),
                a.getName(),
                a.getAccountType(),
                a.getBalance(),
                a.isBlocked() ? "Yes" : "No"
            });
        }
    }
}
