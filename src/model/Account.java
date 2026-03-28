package model;

public class Account {
    private final int accountNo;
    private String name;
    private String password;
    private double balance;

    public Account(int accountNo, String name, String password, double balance) {
        this.accountNo = accountNo;
        this.name = name;
        this.password = password;
        this.balance = balance;
    }

    // Getters
    public int getAccountNo() { return accountNo; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public synchronized double getBalance() { return balance; }

    // Logic
    public synchronized void deposit(double amt) { if (amt > 0) balance += amt; }
    public synchronized boolean withdraw(double amt) {
        if (amt > 0 && balance >= amt) { balance -= amt; return true; }
        return false;
    }
}