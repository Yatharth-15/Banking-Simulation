package model;

public abstract class Account {
    private final int accountNo;
    private final String name;
    private final String password;
    private final String pin; 
    protected double balance;
    protected String accountType;

    // Security State
    private boolean isBlocked = false; 
    private int failedAttempts = 0;    
    private long lockTime = 0; 

    // Daily Limits
    private double dailyWithdrawn = 0.0;
    private String lastTransactionDate = "";

    public Account(int accNo, String name, String pass, String pin, double bal, String type) {
        this.accountNo = accNo;
        this.name = name;
        this.password = pass;
        this.pin = pin;
        this.balance = bal;
        this.accountType = type;
    }

    public int getAccountNo() { return accountNo; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getPin() { return pin; }
    public synchronized double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    
    public boolean isBlocked() { return isBlocked; }
    public long getLockTime() { return lockTime; }

    public synchronized double getDailyWithdrawn() { return dailyWithdrawn; }
    public synchronized void setDailyWithdrawn(double dailyWithdrawn) { this.dailyWithdrawn = dailyWithdrawn; }
    public synchronized String getLastTransactionDate() { return lastTransactionDate; }
    public synchronized void setLastTransactionDate(String lastTransactionDate) { this.lastTransactionDate = lastTransactionDate; }

    public synchronized boolean canTransact(double amount) {
        String today = java.time.LocalDate.now().toString();
        if (!today.equals(lastTransactionDate)) {
            dailyWithdrawn = 0.0;
            lastTransactionDate = today;
        }
        return (dailyWithdrawn + amount) <= 20000.0;
    }

    public synchronized void recordTransaction(double amount) {
        String today = java.time.LocalDate.now().toString();
        if (!today.equals(lastTransactionDate)) {
            dailyWithdrawn = 0.0;
            lastTransactionDate = today;
        }
        dailyWithdrawn += amount;
    }

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
        if (!b) {
            this.lockTime = 0;
        }
    }
    public void setLockTime(long time) { 
        this.lockTime = time; 
    }
    public void resetFailedAttempts() {
         this.failedAttempts = 0; 
    }

    // Transactions
    public synchronized void deposit(double amt) { 
        if (amt > 0) balance += amt;
    }
    
    // Abstract method to be overridden by child classes
    public abstract boolean withdraw(double amt);
}