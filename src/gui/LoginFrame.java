package gui;

import DB.AccountDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Account;

public class LoginFrame extends JFrame {
    private final List<Account> allAccounts;
    private final JTextField accField = new JTextField(15);
    private final JTextField nameField = new JTextField(15);
    private final JPasswordField passField = new JPasswordField(15);

    public LoginFrame(List<Account> accounts) {
        this.allAccounts = accounts;
        
        setTitle("Secure Bank Login");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 1, 10, 10));
        getContentPane().setBackground(new Color(245, 245, 250));

        JLabel header = new JLabel("CITY BANK DEHRADUN", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 22));
        header.setForeground(new Color(44, 62, 80));

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(46, 204, 113)); // Green

        JButton regBtn = new JButton("Register New User");
        styleButton(regBtn, new Color(52, 152, 219)); // Blue

        add(header);
        add(new JLabel("  Account Number:", SwingConstants.LEFT));
        add(accField);
        add(new JLabel("  Password:", SwingConstants.LEFT));
        add(passField);
        add(new JLabel("  Full Name (For Registration Only):", SwingConstants.LEFT));
        add(nameField);
        add(new JLabel("")); // Spacer
        add(loginBtn);
        add(regBtn);

        loginBtn.addActionListener(e -> handleLogin());
        regBtn.addActionListener(e -> handleRegister());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
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
            JOptionPane.showMessageDialog(this, "Invalid ID or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Check ID Format!"); }
    }

    private void handleRegister() {
        try {
            int id = Integer.parseInt(accField.getText());
            String name = nameField.getText();
            String pass = new String(passField.getPassword());
            if (name.isEmpty() || pass.isEmpty()) throw new Exception();

            Account newAcc = new Account(id, name, pass, 1000.0);
            allAccounts.add(newAcc);
            AccountDAO.saveAccounts(allAccounts);
            JOptionPane.showMessageDialog(this, "Registration Successful! Log in now.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Fill all fields correctly!"); }
    }
}