package model;

public class SavingsAccount extends Account {
    public SavingsAccount(int accNo, String name, String pass, String pin, double bal) {
        super(accNo, name, pass, pin, bal, "SAVINGS");
    }

    @Override
    public synchronized boolean withdraw(double amt) {
        if (amt > 0 && balance >= amt) { 
            balance -= amt; 
            return true; 
        }
        return false;
    }

    public synchronized double applyInterest() {
        double interest = balance * 0.04; // 4% interest
        deposit(interest);
        return interest;
    }
}
