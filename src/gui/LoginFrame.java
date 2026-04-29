package gui;

import DB.AccountDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;

public class LoginFrame extends JFrame {
    private static final int LOCKOUT_TIME_SECONDS = 60;
    private final List<Account> allAccounts;
    
    // Input Fields
    private final JTextField accField = new JTextField(15);
    private final JPasswordField passField = new JPasswordField(15);
    private final JTextField nameField = new JTextField(15);
    private final JTextField pinField = new JTextField(15);
    private final JComboBox<String> accTypeBox = new JComboBox<>(new String[]{"Savings", "Current"});

    private final JLabel nameLabel = new JLabel("Full Name:");
    private final JLabel pinLabel = new JLabel("Set 4-Digit PIN:");
    private final JLabel typeLabel = new JLabel("Account Type:");
    private boolean isRegisterMode = false;

    public LoginFrame(List<Account> accounts) {
        this.allAccounts = accounts;
        
        setTitle("City Bank Login");
        setSize(400, 600); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); 
        getContentPane().setBackground(new Color(245, 245, 250));

        // --- 1. Top banner ---
        JPanel brandPanel = new JPanel(new GridLayout(2, 1));
        brandPanel.setBackground(new Color(44, 62, 80)); 
        brandPanel.setPreferredSize(new Dimension(400, 120));

        JLabel bankName = new JLabel("CITY BANK", SwingConstants.CENTER);
        bankName.setFont(new Font("Serif", Font.BOLD, 38));
        bankName.setForeground(new Color(236, 240, 241));
        
        JLabel tagline = new JLabel("Digital Banking Simplified", SwingConstants.CENTER);
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 14));
        tagline.setForeground(new Color(46, 204, 113)); 

        brandPanel.add(bankName);
        brandPanel.add(tagline);
        add(brandPanel, BorderLayout.NORTH);

        // --- 2. Form for details ---
        JPanel formPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setOpaque(false);

        addLabel(formPanel, "Account Number:");
        formPanel.add(accField);
        addLabel(formPanel, "Password:");
        formPanel.add(passField);
        
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(pinLabel); formPanel.add(pinField);
        formPanel.add(typeLabel); formPanel.add(accTypeBox);
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(formPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

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
        typeLabel.setVisible(v); accTypeBox.setVisible(v);
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
                        if (secondsPassed >= LOCKOUT_TIME_SECONDS) {
                            a.setBlocked(false); a.resetFailedAttempts();
                            AccountDAO.saveAccounts(allAccounts);
                        } else {
                            JOptionPane.showMessageDialog(this, "Wait " + (LOCKOUT_TIME_SECONDS - secondsPassed) + "s", "Locked", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    if (a.getAccountNo() == 101) {
                        new AdminFrame(allAccounts);
                    } else {
                        new DashboardFrame(a, allAccounts);
                    }
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

            for (Account a : allAccounts) {
                if (a.getAccountNo() == id) {
                    JOptionPane.showMessageDialog(this, "ID is taken, try another.");
                    return;
                }
            }

            if (name.isEmpty() || pass.isEmpty() || pin.length() != 4) {
                JOptionPane.showMessageDialog(this, "Fill everything properly (PIN 4 digits)"); return;
            }
            
            String type = (String) accTypeBox.getSelectedItem();
            Account newAcc;
            if ("Current".equals(type)) {
                newAcc = new CurrentAccount(id, name, pass, pin, 1000.0);
            } else {
                newAcc = new SavingsAccount(id, name, pass, pin, 1000.0);
            }
            
            allAccounts.add(newAcc);
            AccountDAO.saveAccounts(allAccounts);
            JOptionPane.showMessageDialog(this, "Done! Go to login now.");
            isRegisterMode = false; setRegisterFieldsVisible(false);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid Data!"); }
    }
}