package gui;

import DB.AccountDAO;
import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import model.Account;
import model.Loan;
import service.TransactionService;

public class DashboardFrame extends JFrame {
    private static final int LOCKOUT_TIME_SECONDS = 60;
    private final Account currentUser;
    private final List<Account> allAccounts;
    private final TransactionService service = new TransactionService();
    
    private final JLabel balLabel;
    private final JTextField amtField = new JTextField(15);
    private final JTextField targetIdField = new JTextField(15);

    public DashboardFrame(Account user, List<Account> accounts) {
        this.currentUser = user;
        this.allAccounts = accounts;

        setTitle("Secure Dashboard - " + currentUser.getName() + " (" + currentUser.getAccountType() + ")");
        setSize(450, 650); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(236, 240, 241));

        // 1. Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(44, 62, 80));
        JLabel nameLabel = new JLabel("Welcome, " + currentUser.getName() + " [" + currentUser.getAccountType() + "]", SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        balLabel = new JLabel(String.format("Balance: ₹%.2f", currentUser.getBalance()), SwingConstants.CENTER);
        balLabel.setFont(new Font("Arial", Font.BOLD, 22));
        balLabel.setForeground(new Color(46, 204, 113));
        
        JPanel headerInfo = new JPanel(new GridLayout(2, 1));
        headerInfo.setOpaque(false);
        headerInfo.add(balLabel);
        if ("CURRENT".equals(currentUser.getAccountType())) {
            JLabel overdraftLabel = new JLabel("(Overdraft Limit: ₹10000)", SwingConstants.CENTER);
            overdraftLabel.setForeground(new Color(231, 76, 60));
            headerInfo.add(overdraftLabel);
        }
        
        header.add(nameLabel); header.add(headerInfo);
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
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(center, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

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
        JButton interestBtn = new JButton("Apply Interest");
        JButton loanBtn = new JButton("Apply Loan");
        JButton myLoansBtn = new JButton("My Loans");

        styleBtn(depBtn, new Color(52, 152, 219));
        styleBtn(withBtn, new Color(230, 126, 34));
        styleBtn(transBtn, new Color(155, 89, 182));
        styleBtn(histBtn, new Color(52, 73, 94));
        styleBtn(logoutBtn, new Color(231, 76, 60));
        styleBtn(interestBtn, new Color(39, 174, 96));
        styleBtn(loanBtn, new Color(241, 196, 15));
        styleBtn(myLoansBtn, new Color(142, 68, 173));

        gbc.gridx = 0; gbc.gridy = 0; footer.add(depBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 0; footer.add(withBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1; footer.add(transBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 1; footer.add(histBtn, gbc);
        
        int nextY = 2;
        
        gbc.gridx = 0; gbc.gridy = nextY; gbc.gridwidth = 1;
        footer.add(loanBtn, gbc);
        if ("SAVINGS".equals(currentUser.getAccountType())) {
            gbc.gridx = 1;
            footer.add(interestBtn, gbc);
            nextY++;
            gbc.gridx = 0; gbc.gridy = nextY; gbc.gridwidth = 2;
            footer.add(myLoansBtn, gbc);
        } else {
            gbc.gridx = 1;
            footer.add(myLoansBtn, gbc);
        }
        nextY++;

        gbc.gridx = 0; gbc.gridy = nextY; gbc.gridwidth = 2; 
        gbc.insets = new Insets(15, 80, 5, 80); 
        footer.add(logoutBtn, gbc);
        add(footer, BorderLayout.SOUTH);

        // --- Action Listeners with Validation ---
        
        depBtn.addActionListener(e -> {
            double amount = getAmt();
            if (amount > 0 && verify()) {
                if (service.deposit(currentUser, amount)) {
                    refresh("Deposit Done");
                } else {
                    err("Transaction Failed: Daily limit (₹20,000) exceeded.");
                }
            } else if (amount <= 0) {
                err("Please enter a positive amount.");
            }
        });

        withBtn.addActionListener(e -> {
            double amount = getAmt();
            if (amount > 0 && verify()) {
                if (service.withdraw(currentUser, amount)) refresh("Withdraw Done");
                else err("Transaction Failed: Low balance or daily limit (₹20,000) exceeded.");
            } else if (amount <= 0) {
                err("Please enter a positive amount.");
            }
        });

        transBtn.addActionListener(e -> handleTransfer());
        
        histBtn.addActionListener(e -> showHistory());
        
        interestBtn.addActionListener(e -> {
            if (currentUser instanceof model.SavingsAccount) {
                double earned = ((model.SavingsAccount) currentUser).applyInterest();
                refresh(String.format("Interest Applied Successfully! You earned ₹%.2f (+4%%)", earned));
            }
        });
        
        loanBtn.addActionListener(e -> handleLoanApplication());
        myLoansBtn.addActionListener(e -> showMyLoans());
        
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
            if (secondsPassed >= LOCKOUT_TIME_SECONDS) {
                currentUser.setBlocked(false); currentUser.resetFailedAttempts();
                AccountDAO.saveAccounts(allAccounts);
            } else {
                err("Blocked! Wait " + (LOCKOUT_TIME_SECONDS - secondsPassed) + "s");
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
        balLabel.setText(String.format("Balance: ₹%.2f", currentUser.getBalance()));
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
                if (target.getAccountNo() == 101) {
                    err("Cannot transfer funds to the Admin account.");
                    return;
                }
                if (verify()) {
                    if (amount >= 10000) {
                        int otp = 1000 + new java.util.Random().nextInt(9000);
                        System.out.println("\n========================================");
                        System.out.println("[SECURITY] 2FA OTP for Transfer of ₹" + amount);
                        System.out.println("[SECURITY] Your OTP is: " + otp);
                        System.out.println("========================================\n");
                        
                        String inputOtp = JOptionPane.showInputDialog(this, "A security OTP has been sent to your phone.\n(Please check the terminal console)\n\nEnter 4-digit OTP:");
                        if (inputOtp == null || !inputOtp.trim().equals(String.valueOf(otp))) {
                            err("Transfer Failed: Incorrect OTP.");
                            return;
                        }
                    }

                    if (service.transfer(currentUser, target, amount)) {
                        refresh("Transfer Successful");
                    } else {
                        err("Transaction Failed: Daily limit (₹20,000) exceeded.");
                    }
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
                if (line.startsWith(currentUser.getAccountNo() + " |")) {
                    sb.append(line).append("\n");
                }
            }
        } catch (IOException e) { sb.append("No records found."); }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Mini-Statement", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMyLoans() {
        List<Loan> myLoans = DB.LoanDAO.getLoansForUser(currentUser.getAccountNo());
        if (myLoans.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no loans on record.");
            return;
        }
        
        String[] lCols = {"Loan ID", "Type", "Principal", "Months", "EMI", "Status"};
        javax.swing.table.DefaultTableModel lModel = new javax.swing.table.DefaultTableModel(lCols, 0);
        for (Loan l : myLoans) {
            lModel.addRow(new Object[]{l.getLoanId(), l.getLoanType(), l.getPrincipal(), l.getMonths(), String.format("₹%.2f", l.getEmi()), l.getStatus()});
        }
        JTable lTable = new JTable(lModel);
        
        JPanel lPanel = new JPanel(new BorderLayout());
        lPanel.add(new JScrollPane(lTable), BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, lPanel, "My Loans", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleLoanApplication() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Home Loan (8%)", "Car Loan (10%)", "Education Loan (6%)"});
        JTextField principalField = new JTextField(10);
        JTextField monthsField = new JTextField("60");
        
        panel.add(new JLabel("Loan Type:")); panel.add(typeBox);
        panel.add(new JLabel("Principal (₹):")); panel.add(principalField);
        panel.add(new JLabel("Duration (Months):")); panel.add(monthsField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Apply for a Loan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double principal = Double.parseDouble(principalField.getText());
                int months = Integer.parseInt(monthsField.getText());
                if (principal <= 0 || months <= 0) {
                    err("Invalid amount or duration."); return;
                }
                
                String selectedType = (String) typeBox.getSelectedItem();
                double rate = 0;
                String typeName = "";
                if (selectedType.contains("Home")) { rate = 0.08; typeName = "Home"; }
                else if (selectedType.contains("Car")) { rate = 0.10; typeName = "Car"; }
                else { rate = 0.06; typeName = "Education"; }
                
                double r = rate / 12;
                double emi = (principal * r * Math.pow(1 + r, months)) / (Math.pow(1 + r, months) - 1);
                
                String msg = String.format("Calculated EMI: ₹%.2f per month for %d months.\n\nDo you want to submit this application?", emi, months);
                int confirm = JOptionPane.showConfirmDialog(this, msg, "Confirm Application", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Loan newLoan = new Loan(0, currentUser.getAccountNo(), typeName, principal, rate, months, emi, "PENDING");
                    DB.LoanDAO.applyLoan(newLoan);
                    refresh("Loan Application Submitted! Waiting for Admin Approval.");
                }
            } catch (NumberFormatException ex) {
                err("Please enter valid numbers.");
            }
        }
    }
}