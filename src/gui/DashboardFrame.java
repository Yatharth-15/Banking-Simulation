package gui;

import DB.AccountDAO;
import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import model.Account;
import service.TransactionService;

public class DashboardFrame extends JFrame {
    private final Account currentUser;
    private final List<Account> allAccounts;
    private final TransactionService service = new TransactionService();
    
    private final JLabel balLabel;
    private final JTextField amtField = new JTextField(15);
    private final JTextField targetIdField = new JTextField(15);

    public DashboardFrame(Account user, List<Account> accounts) {
        this.currentUser = user;
        this.allAccounts = accounts;

        setTitle("Secure Dashboard - " + currentUser.getName());
        setSize(450, 650); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(236, 240, 241));

        // 1. Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(44, 62, 80));
        JLabel nameLabel = new JLabel("Welcome, " + currentUser.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        balLabel = new JLabel("Balance: ₹" + currentUser.getBalance(), SwingConstants.CENTER);
        balLabel.setFont(new Font("Arial", Font.BOLD, 22));
        balLabel.setForeground(new Color(46, 204, 113));
        header.add(nameLabel); header.add(balLabel);
        add(header, BorderLayout.NORTH);

        // 2. Center Panel
        JPanel center = new JPanel(new GridLayout(4, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        center.setOpaque(false);
        amtField.setBorder(BorderFactory.createTitledBorder("Enter Amount"));
        targetIdField.setBorder(BorderFactory.createTitledBorder("Recipient ID (for Transfer)"));
        center.add(new JLabel("Transaction Details:")); 
        center.add(amtField);
        center.add(new JLabel("Recipient Details:")); 
        center.add(targetIdField);
        add(center, BorderLayout.CENTER);

        // 3. Footer Panel
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        footer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JButton depBtn = new JButton("Deposit");
        JButton withBtn = new JButton("Withdraw");
        JButton transBtn = new JButton("Transfer");
        JButton histBtn = new JButton("View History");
        JButton logoutBtn = new JButton("Logout");

        styleBtn(depBtn, new Color(52, 152, 219));
        styleBtn(withBtn, new Color(230, 126, 34));
        styleBtn(transBtn, new Color(155, 89, 182));
        styleBtn(histBtn, new Color(52, 73, 94));
        styleBtn(logoutBtn, new Color(231, 76, 60));

        gbc.gridx = 0; gbc.gridy = 0; footer.add(depBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 0; footer.add(withBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1; footer.add(transBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 1; footer.add(histBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        gbc.insets = new Insets(15, 80, 5, 80); 
        footer.add(logoutBtn, gbc);
        add(footer, BorderLayout.SOUTH);

        // --- Action Listeners with Validation ---
        
        depBtn.addActionListener(e -> {
            double amount = getAmt();
            if (amount > 0 && verify()) {
                service.deposit(currentUser, amount);
                refresh("Deposit Done");
            } else if (amount <= 0) {
                err("Please enter a positive amount.");
            }
        });

        withBtn.addActionListener(e -> {
            double amount = getAmt();
            if (amount > 0 && verify()) {
                if (service.withdraw(currentUser, amount)) refresh("Withdraw Done");
                else err("Low Balance");
            } else if (amount <= 0) {
                err("Please enter a positive amount.");
            }
        });

        transBtn.addActionListener(e -> handleTransfer());
        
        histBtn.addActionListener(e -> showHistory());
        
        logoutBtn.addActionListener(e -> {
            AccountDAO.saveAccounts(allAccounts);
            new LoginFrame(allAccounts);
            this.dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleBtn(JButton b, Color bg) { b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false); }

    private boolean verify() {
        if (currentUser.isBlocked()) {
            long secondsPassed = (System.currentTimeMillis() - currentUser.getLockTime()) / 1000;
            if (secondsPassed >= 60) {
                currentUser.setBlocked(false); currentUser.resetFailedAttempts();
                AccountDAO.saveAccounts(allAccounts);
            } else {
                err("Blocked! Wait " + (60 - secondsPassed) + "s");
                return false;
            }
        }
        String pin = JOptionPane.showInputDialog(this, "Enter 4-Digit PIN:");
        if (pin == null) return false;
        if (currentUser.verifyPin(pin)) {
            currentUser.resetFailedAttempts(); return true;
        } else {
            currentUser.recordFailedAttempt();
            if (currentUser.isBlocked()) AccountDAO.saveAccounts(allAccounts);
            err(currentUser.isBlocked() ? "3 Failures! Blocked for 1 min." : "Wrong PIN!");
            return false;
        }
    }

    // Updated to handle empty or invalid text
    private double getAmt() {
        try {
            return Double.parseDouble(amtField.getText());
        } catch (NumberFormatException e) {
            return -1; // Returns -1 to trigger the "positive amount" error
        }
    }
    
    private void refresh(String m) {
        balLabel.setText("Balance: ₹" + currentUser.getBalance());
        amtField.setText(""); targetIdField.setText("");
        JOptionPane.showMessageDialog(this, m);
    }

    private void err(String m) { JOptionPane.showMessageDialog(this, m, "System Message", JOptionPane.ERROR_MESSAGE); }

    private void handleTransfer() {
        double amount = getAmt();
        if (amount <= 0) {
            err("Please enter a valid positive amount.");
            return;
        }
        
        if (amount > currentUser.getBalance()) {
            err("Insufficient Balance for this transfer.");
            return;
        }

        try {
            int tId = Integer.parseInt(targetIdField.getText());
            Account target = null;
            for (Account a : allAccounts) if (a.getAccountNo() == tId) target = a;

            if (target != null && target != currentUser) {
                if (verify()) {
                    service.transfer(currentUser, target, amount);
                    refresh("Transfer Successful");
                }
            } else {
                err("Recipient ID not found or invalid.");
            }
        } catch (NumberFormatException e) { 
            err("Invalid Recipient ID"); 
        }
    }

    private void showHistory() {
        StringBuilder sb = new StringBuilder("--- TRANSACTION HISTORY ---\n\n");
        try (BufferedReader br = new BufferedReader(new FileReader("transactions.log"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(String.valueOf(currentUser.getAccountNo()))) {
                    sb.append(line).append("\n");
                }
            }
        } catch (IOException e) { sb.append("No records found."); }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Mini-Statement", JOptionPane.INFORMATION_MESSAGE);
    }
}