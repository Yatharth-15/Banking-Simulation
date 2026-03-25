package gui;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Account;

public class LoginFrame extends JFrame {
    private List<Account> accounts;

    public LoginFrame(List<Account> accounts) {
        this.accounts = accounts;

        setTitle("Bank Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10)); // Simple vertical stack

    
        JLabel titleLabel = new JLabel("Banking System Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel promptLabel = new JLabel("Enter Account ID:", SwingConstants.CENTER);
        
        JPasswordField idField = new JPasswordField();
        idField.setHorizontalAlignment(JTextField.CENTER);

        JButton loginBtn = new JButton("Login");


        loginBtn.addActionListener(e -> {
            try {
            
                String input = new String(idField.getPassword());
                int id = Integer.parseInt(input);

            
                Account user = this.accounts.stream()
                    .filter(a -> a.getId() == id)
                    .findFirst()
                    .orElse(null);

                if (user != null) {
                
                    new DashboardFrame(user, accounts);
                    this.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "ID Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a numeric ID.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add components to the window
        add(titleLabel);
        add(promptLabel);
        add(idField);
        add(loginBtn);

        // Center on screen
        setLocationRelativeTo(null);
        setVisible(true);
    }
}