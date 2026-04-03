package model;

public class Account {
    private final int accountNo;
    private final String name;
    private final String password;
    private final String pin; // 4-Digit PIN
    private double balance;

    public Account(int accNo, String name, String pass, String pin, double bal) {
        this.accountNo = accNo;
        this.name = name;
        this.password = pass;
        this.pin = pin;
        this.balance = bal;
    }

    // Getters
    public int getAccountNo() { return accountNo; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public synchronized double getBalance() { return balance; }

    // Security Check
    public boolean verifyPin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    // Transactions
    public synchronized void deposit(double amt)
     { 
        if (amt > 0) balance += amt;
     }
    public synchronized boolean withdraw(double amt) {
        if (amt > 0 && balance >= amt) { balance -= amt; return true; }
        return false;
    }
    public String getPin() {
       return this.pin;
    }
}
