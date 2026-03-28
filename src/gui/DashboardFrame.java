package gui;

import DB.AccountDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Account;
import service.TransactionService;

public class DashboardFrame extends JFrame {
    private final Account currentUser;
    private final List<Account> allAccounts;
    private final TransactionService service = new TransactionService();
    
    private final JLabel balLabel;
    private final JTextField amtField = new JTextField(10);
    private final JTextField targetIdField = new JTextField(10);

    public DashboardFrame(Account user, List<Account> accounts) {
        this.currentUser = user;
        this.allAccounts = accounts;

        setTitle("Bank Dashboard - " + currentUser.getName());
        setSize(450, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(236, 240, 241));

        // 1. Balance Header
        balLabel = new JLabel("Current Balance: ₹" + currentUser.getBalance(), SwingConstants.CENTER);
        balLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balLabel.setForeground(new Color(39, 174, 96));
        balLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(balLabel, BorderLayout.NORTH);

        // 2. Main Input Panel
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        centerPanel.setOpaque(false);

        JPanel p1 = new JPanel(); p1.add(new JLabel("Amount: ₹")); p1.add(amtField);
        JPanel p2 = new JPanel(); p2.add(new JLabel("Transfer To (ID): ")); p2.add(targetIdField);
        
        centerPanel.add(new JLabel("Welcome, " + currentUser.getName(), SwingConstants.CENTER));
        centerPanel.add(p1);
        centerPanel.add(p2);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Action Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton depBtn = new JButton("Deposit");
        JButton withBtn = new JButton("Withdraw");
        JButton transBtn = new JButton("Transfer Money");
        JButton logoutBtn = new JButton("Logout & Save");

        styleBtn(depBtn, new Color(46, 204, 113));
        styleBtn(withBtn, new Color(230, 126, 34));
        styleBtn(transBtn, new Color(155, 89, 182));
        styleBtn(logoutBtn, new Color(231, 76, 60));

        btnPanel.add(depBtn); btnPanel.add(withBtn);
        btnPanel.add(transBtn); btnPanel.add(logoutBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Logic
        depBtn.addActionListener(e -> handleAction("DEP"));
        withBtn.addActionListener(e -> handleAction("WITH"));
        transBtn.addActionListener(e -> handleTransfer());
        logoutBtn.addActionListener(e -> {
            AccountDAO.saveAccounts(allAccounts);
            new LoginFrame(allAccounts);
            this.dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false);
    }

    private void handleAction(String type) {
        try {
            double amt = Double.parseDouble(amtField.getText());
            if (type.equals("DEP")) service.deposit(currentUser, amt);
            else if (!service.withdraw(currentUser, amt)) throw new Exception("Low Balance");
            refreshUI("Success!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void handleTransfer() {
        try {
            double amt = Double.parseDouble(amtField.getText());
            int tId = Integer.parseInt(targetIdField.getText());
            Account target = null;
            for (Account a : allAccounts) if (a.getAccountNo() == tId) target = a;

            if (target != null && service.transfer(currentUser, target, amt)) refreshUI("Transfer Sent!");
            else JOptionPane.showMessageDialog(this, "Transfer Failed!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Check Inputs!"); }
    }

    private void refreshUI(String msg) {
        balLabel.setText("Current Balance: ₹" + currentUser.getBalance());
        amtField.setText(""); targetIdField.setText("");
        JOptionPane.showMessageDialog(this, msg);
    }
}