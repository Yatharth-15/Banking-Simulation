package model;

public class Account {
    private final int accountNo;
    private final String name;
    private final String password;
    private final String pin; 
    private double balance;

    // Security State
    private boolean isBlocked = false; 
    private int failedAttempts = 0;    
    private long lockTime = 0; 

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
    public String getPin() { return pin; }
    public synchronized double getBalance() { return balance; }
    public boolean isBlocked() { return isBlocked; }
    public long getLockTime() { return lockTime; }

    // Security Logic
    public boolean verifyPin(String inputPin) {
        if (this.isBlocked) return false;
        return this.pin.equals(inputPin);
    }

    public void recordFailedAttempt() {
        failedAttempts++;
        if (failedAttempts >= 3) {
            this.isBlocked = true;
            this.lockTime = System.currentTimeMillis(); 
        }
    }

    public void setBlocked(boolean b) { 
        this.isBlocked = b;
     }
    public void setLockTime(long time) { 
        this.lockTime = time; 
    }
    public void resetFailedAttempts() {
         this.failedAttempts = 0; 
        }

    // Transactions
    public synchronized void deposit(double amt)
     { 
        if (amt > 0) balance += amt;
        
     }
    public synchronized boolean withdraw(double amt) 
    {
        if (amt > 0 && balance >= amt) { balance -= amt; return true; }
        return false;
    }
}