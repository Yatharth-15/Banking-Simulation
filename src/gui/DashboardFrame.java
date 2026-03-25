package gui;

import DB.AccountDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import logging.TransactionLogger;
import model.Account;

public class DashboardFrame extends JFrame {
    // Fields made 'final' to satisfy the hints
    private final Account acc;
    private final JLabel balLab;
    private final JTextField amtField = new JTextField(10);
    private final TransactionLogger logger = new TransactionLogger();
    private final List<Account> allAccounts; 

    public DashboardFrame(Account acc, List<Account> all) {
        this.acc = acc; 
        this.allAccounts = all; // 'allAccounts' is now used in the exit button
        
        setTitle("Bank - ID: " + acc.getId());
        setSize(300, 250);
        setLayout(new GridLayout(4, 1, 5, 5));

        balLab = new JLabel("Balance: ₹" + acc.getBalance(), 0);
        JButton depBtn = new JButton("Deposit");
        JButton withBtn = new JButton("Withdraw");
        JButton exitBtn = new JButton("Save & Exit");

        depBtn.addActionListener(e -> action(true));
        withBtn.addActionListener(e -> action(false));
        
        // Using allAccounts here removes the "unused field" warning
        exitBtn.addActionListener(e -> { 
            AccountDAO.saveAccounts(allAccounts); 
            System.exit(0); 
        });

        add(balLab);
        JPanel p = new JPanel(); p.add(new JLabel("₹")); p.add(amtField); add(p);
        JPanel b = new JPanel(); b.add(depBtn); b.add(withBtn); add(b);
        add(exitBtn);

        setLocationRelativeTo(null);
        setVisible(true);
    }

   private void action(boolean isDep) {
    try {
        double amt = Double.parseDouble(amtField.getText());
        
        if (isDep) {
            acc.deposit(amt);
            updateUI("DEP", amt);
        } else if (acc.withdraw(amt)) {
            updateUI("WITH", amt);
        } else {
            JOptionPane.showMessageDialog(this, "Low Balance!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (NumberFormatException e) {
     
        JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
    } catch (HeadlessException e) {
      
        JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
    }
     
    amtField.setText("");
}

    private void updateUI(String type, double amt) {
        balLab.setText("Balance: ₹" + acc.getBalance());
        logger.log(type + " for ID " + acc.getId() + ": ₹" + amt); // Fixed log call
        JOptionPane.showMessageDialog(this, "Success!");
    }
}