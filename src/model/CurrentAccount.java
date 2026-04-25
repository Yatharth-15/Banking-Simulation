package model;

public class CurrentAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 10000.0;

    public CurrentAccount(int accNo, String name, String pass, String pin, double bal) {
        super(accNo, name, pass, pin, bal, "CURRENT");
    }

    @Override
    public synchronized boolean withdraw(double amt) {
        if (amt > 0 && (balance + OVERDRAFT_LIMIT) >= amt) { 
            balance -= amt; 
            return true; 
        }
        return false;
    }
}
