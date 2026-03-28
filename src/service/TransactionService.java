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
        // Step 1: Try to take money from the sender
        if (from.withdraw(amt)) {
            // Step 2: If successful, give it to the receiver
            to.deposit(amt);
            
            // Step 3: Log the success with both names
            logger.log("TRANSFER: From " + from.getName() + " to " + to.getName() + " | Amt: ₹" + amt);
            return true;
        }
        
        // Step 4: Log the failure
        logger.log("FAILED_TRANSFER: " + from.getName() + " had insufficient funds for ₹" + amt);
        return false;
    }
}