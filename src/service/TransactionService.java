package service;

import logging.TransactionLogger;
import model.Account;

public class TransactionService {
    private final TransactionLogger logger = new TransactionLogger();

    // 1. DEPOSIT
    public void deposit(Account acc, double amt) {
        if (amt > 0) {
            acc.deposit(amt);
            logger.log("DEPOSIT: " + acc.getName() + " (ID: " + acc.getAccountNo() + ") Amt: ₹" + amt);
        }
    }

    // 2. WITHDRAW
    public boolean withdraw(Account acc, double amt) {
        if (acc.withdraw(amt)) {
            logger.log("WITHDRAW: " + acc.getName() + " (ID: " + acc.getAccountNo() + ") Amt: ₹" + amt);
            return true;
        }
        logger.log("FAILED_WITHDRAW: " + acc.getName() + " - Low Balance for ₹" + amt);
        return false;
    }

    // 3. TRANSFER (The missing method)
    public boolean transfer(Account from, Account to, double amt) {

        if (from.withdraw(amt)) {
            to.deposit(amt);
            logger.log("TRANSFER: From " + from.getName() + " to " + to.getName() + " | Amt: ₹" + amt);
            return true;
        }
        // Step 4: Log the failure
        logger.log("FAILED_TRANSFER: " + from.getName() + " had insufficient funds for ₹" + amt);
        return false;
    }
}