package gui;

import DB.AccountDAO;
import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*; // Added for better boxes
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
        setSize(450, 600);
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

        // 2. Updated Boxes (Minimal UI Update)
        JPanel center = new JPanel(new GridLayout(4, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        center.setOpaque(false);
        
        // Setting Titled Borders for the boxes
        amtField.setBorder(BorderFactory.createTitledBorder("Enter Amount"));
        targetIdField.setBorder(BorderFactory.createTitledBorder("Recipient ID (for Transfer)"));
        
        center.add(new JLabel("Transaction Details:")); 
        center.add(amtField);
        center.add(new JLabel("Recipient Details:")); 
        center.add(targetIdField);
        add(center, BorderLayout.CENTER);

        // 3. Buttons
        JPanel footer = new JPanel(new GridLayout(3, 2, 10, 10));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

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

        footer.add(depBtn); footer.add(withBtn);
        footer.add(transBtn); footer.add(histBtn);
        footer.add(new JLabel("")); // Spacer
        footer.add(logoutBtn);
        add(footer, BorderLayout.SOUTH);

        // Listeners
        depBtn.addActionListener(e -> { if(verify()) { service.deposit(currentUser, getAmt()); refresh("Deposit Done"); }});
        withBtn.addActionListener(e -> { if(verify()) { if(service.withdraw(currentUser, getAmt())) refresh("Withdraw Done"); else err("Low Balance"); }});
        transBtn.addActionListener(e -> handleTransfer());
        histBtn.addActionListener(e -> showHistory());
        logoutBtn.addActionListener(e -> { AccountDAO.saveAccounts(allAccounts); new LoginFrame(allAccounts); this.dispose(); });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleBtn(JButton b, Color bg) { b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false); }

    // --- UPDATED VERIFY METHOD WITH WRONG PIN MESSAGE ---
    private boolean verify() {
        String pin = JOptionPane.showInputDialog(this, "Enter 4-Digit Security PIN:");
        
        if (pin == null) return false; // Cancelled

        if (currentUser.verifyPin(pin)) {
            return true;
        } else {
            // New error message for wrong PIN
            JOptionPane.showMessageDialog(this, "WRONG PIN! Access Denied.", "Security Alert", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private double getAmt() { return Double.parseDouble(amtField.getText()); }
    
    private void refresh(String m) {
        balLabel.setText("Balance: ₹" + currentUser.getBalance());
        amtField.setText(""); targetIdField.setText("");
        JOptionPane.showMessageDialog(this, m);
    }

    private void err(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    private void handleTransfer() {
        try {
            if(verify()) {
                int tId = Integer.parseInt(targetIdField.getText());
                Account target = null;
                for (Account a : allAccounts) if (a.getAccountNo() == tId) target = a;
                if (target != null && target != currentUser && service.transfer(currentUser, target, getAmt())) refresh("Transfer Successful");
                else err("Transfer Failed");
            }
        } catch (Exception e) { err("Invalid Input"); }
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