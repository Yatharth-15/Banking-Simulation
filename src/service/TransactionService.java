package service;
import logging.TransactionLogger;
import model.Account;

public class TransactionService {
    private final TransactionLogger logger = new TransactionLogger();

    public void deposit(Account acc, double amt) {
        acc.deposit(amt);
        logger.log("DEP: ID " + acc.getId() + " Amt: " + amt);
    }

    public boolean withdraw(Account acc, double amt) {
        if (acc.withdraw(amt)) {
            logger.log("WITH: ID " + acc.getId() + " Amt: " + amt);
            return true;
        }
        logger.log("FAIL: ID " + acc.getId() + " Insufficient funds");
        return false;
    }

    public void transfer(Account from, Account to, double amt) {
        if (from.withdraw(amt)) {
            to.deposit(amt);
            logger.log("XFER: From " + from.getId() + " To " + to.getId() + " Amt: " + amt);
        }
    }
}