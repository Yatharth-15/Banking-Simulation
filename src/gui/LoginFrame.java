package gui;

import DB.AccountDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Account;

public class LoginFrame extends JFrame {
    private final List<Account> allAccounts;
    
    // Input Fields
    private final JTextField accField = new JTextField(15);
    private final JPasswordField passField = new JPasswordField(15);
    private final JTextField nameField = new JTextField(15);
    private final JTextField pinField = new JTextField(15);

    // Labels
    private final JLabel nameLabel = new JLabel("  Full Name:");
    private final JLabel pinLabel = new JLabel("  Set 4-Digit PIN:");
    private boolean isRegisterMode = false;

    public LoginFrame(List<Account> accounts) {
        this.allAccounts = accounts;
        
        setTitle("City Bank - Secure Access");
        setSize(400, 600); // Taller to accommodate the header
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Using BorderLayout for the Header/Form/Footer structure
        getContentPane().setBackground(new Color(245, 245, 250));

        // --- 1. BRANDING HEADER ---
        JPanel brandPanel = new JPanel(new GridLayout(2, 1));
        brandPanel.setBackground(new Color(44, 62, 80)); // Professional Dark Navy
        brandPanel.setPreferredSize(new Dimension(400, 120));

        JLabel bankName = new JLabel("CITY BANK", SwingConstants.CENTER);
        bankName.setFont(new Font("Serif", Font.BOLD, 38));
        bankName.setForeground(new Color(236, 240, 241));
        
        JLabel tagline = new JLabel("Digital Banking Simplified", SwingConstants.CENTER);
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 14));
        tagline.setForeground(new Color(46, 204, 113)); // Success Green

        brandPanel.add(bankName);
        brandPanel.add(tagline);
        add(brandPanel, BorderLayout.NORTH);

        // --- 2. INPUT FORM ---
        JPanel formPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setOpaque(false);

        addLabel(formPanel, "Account Number:");
        formPanel.add(accField);
        addLabel(formPanel, "Password:");
        formPanel.add(passField);
        
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(pinLabel); formPanel.add(pinField);
        
        add(formPanel, BorderLayout.CENTER);

        // --- 3. BUTTONS ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));
        buttonPanel.setOpaque(false);

        JButton actionBtn = new JButton("Login");
        JButton toggleBtn = new JButton("New User? Click to Register");

        styleBtn(actionBtn, new Color(46, 204, 113));
        styleBtn(toggleBtn, new Color(52, 152, 219));

        buttonPanel.add(actionBtn);
        buttonPanel.add(toggleBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setRegisterFieldsVisible(false);

        // Listeners
        toggleBtn.addActionListener(e -> {
            isRegisterMode = !isRegisterMode;
            actionBtn.setText(isRegisterMode ? "Register Account" : "Login");
            toggleBtn.setText(isRegisterMode ? "Already have an account? Login" : "New User? Register");
            setRegisterFieldsVisible(isRegisterMode);
        });

        actionBtn.addActionListener(e -> {
            if (isRegisterMode) handleRegister();
            else handleLogin();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(l);
    }

    private void setRegisterFieldsVisible(boolean v) {
        nameLabel.setVisible(v); nameField.setVisible(v);
        pinLabel.setVisible(v); pinField.setVisible(v);
        revalidate(); repaint();
    }

    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void handleLogin() {
        try {
            int id = Integer.parseInt(accField.getText());
            String pass = new String(passField.getPassword());
            for (Account a : allAccounts) {
                if (a.getAccountNo() == id && a.getPassword().equals(pass)) {
                    if (a.isBlocked()) {
                        long secondsPassed = (System.currentTimeMillis() - a.getLockTime()) / 1000;
                        if (secondsPassed >= 60) {
                            a.setBlocked(false); a.resetFailedAttempts();
                            AccountDAO.saveAccounts(allAccounts);
                        } else {
                            JOptionPane.showMessageDialog(this, "Locked! Try again in " + (60 - secondsPassed) + "s", "Security", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    new DashboardFrame(a, allAccounts);
                    this.dispose(); return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid Credentials!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Enter ID and Password!"); }
    }

    private void handleRegister() {
        try {
            int id = Integer.parseInt(accField.getText());
            String pin = pinField.getText();
            String name = nameField.getText();
            String pass = new String(passField.getPassword());

            if (name.isEmpty() || pass.isEmpty() || pin.length() != 4) {
                JOptionPane.showMessageDialog(this, "Fill all fields (PIN: 4 digits)"); return;
            }
            allAccounts.add(new Account(id, name, pass, pin, 1000.0));
            AccountDAO.saveAccounts(allAccounts);
            JOptionPane.showMessageDialog(this, "Success! Switch to Login.");
            isRegisterMode = false; setRegisterFieldsVisible(false);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid Data!"); }
    }
}