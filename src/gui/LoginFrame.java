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

    // Labels to hide/show
    private final JLabel nameLabel = new JLabel("  Full Name:");
    private final JLabel pinLabel = new JLabel("  Set 4-Digit PIN:");
    
    private boolean isRegisterMode = false;

    public LoginFrame(List<Account> accounts) {
        this.allAccounts = accounts;
        
        setTitle("City Bank - Secure Access");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(11, 1, 5, 5));
        getContentPane().setBackground(new Color(245, 245, 250));

        // UI Components
        add(new JLabel("  Account Number:"));
        add(accField);
        add(new JLabel("  Password:"));
        add(passField);
        
        // These will be hidden by default
        add(nameLabel); add(nameField);
        add(pinLabel); add(pinField);
        
        JButton actionBtn = new JButton("Login"); // Main Action Button
        JButton toggleBtn = new JButton("New User? Click to Register"); // Switch Button

        styleBtn(actionBtn, new Color(46, 204, 113));
        styleBtn(toggleBtn, new Color(52, 152, 219));

        add(new JLabel("")); 
        add(actionBtn);
        add(toggleBtn);

        // DEFAULT STATE: Hide Register Fields
        setRegisterFieldsVisible(false);

        // TOGGLE LOGIC
        toggleBtn.addActionListener(e -> {
            isRegisterMode = !isRegisterMode;
            if (isRegisterMode) {
                actionBtn.setText("Register Account");
                toggleBtn.setText("Already have an account? Login");
                setRegisterFieldsVisible(true);
            } else {
                actionBtn.setText("Login");
                toggleBtn.setText("New User? Click to Register");
                setRegisterFieldsVisible(false);
            }
        });

        // ACTION LOGIC
        actionBtn.addActionListener(e -> {
            if (isRegisterMode) handleRegister();
            else handleLogin();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setRegisterFieldsVisible(boolean visible) {
        nameLabel.setVisible(visible);
        nameField.setVisible(visible);
        pinLabel.setVisible(visible);
        pinField.setVisible(visible);
        revalidate(); // Refresh the layout
        repaint();
    }

    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false);
    }

    private void handleLogin() {
        try {
            int id = Integer.parseInt(accField.getText());
            String pass = new String(passField.getPassword());
            
            for (Account a : allAccounts) {
                if (a.getAccountNo() == id && a.getPassword().equals(pass)) {
                    new DashboardFrame(a, allAccounts);
                    this.dispose();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid Credentials!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Enter ID and Password!"); }
    }

    private void handleRegister() {
        try {
            int id = Integer.parseInt(accField.getText());
            String name = nameField.getText();
            String pass = new String(passField.getPassword());
            String pin = pinField.getText();

            if (name.isEmpty() || pass.isEmpty() || pin.length() != 4) {
                JOptionPane.showMessageDialog(this, "Please fill Name, Password, and 4-digit PIN!");
                return;
            }

            Account newAcc = new Account(id, name, pass, pin, 1000.0);
            allAccounts.add(newAcc);
            AccountDAO.saveAccounts(allAccounts);
            JOptionPane.showMessageDialog(this, "Registered Successfully! Switching to Login.");
            
            // Auto-switch back to login mode
            isRegisterMode = false;
            setRegisterFieldsVisible(false);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid Data!"); }
    }
}