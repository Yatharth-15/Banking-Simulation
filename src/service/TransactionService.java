package service;

import logging.TransactionLogger;
import model.Account;

public class TransactionService {
    private final TransactionLogger logger = new TransactionLogger();

    // 1. DEPOSIT
    public void deposit(Account acc, double amt) {
        if (amt > 0) {
            acc.deposit(amt);
            logger.log(acc.getAccountNo(), "DEPOSIT: " + acc.getName() + " (ID: " + acc.getAccountNo() + ") Amt: ₹" + amt);
        }
    }
    // 2. WITHDRAW
    public boolean withdraw(Account acc, double amt) {
        if (acc.withdraw(amt)) {
            logger.log(acc.getAccountNo(), "WITHDRAW: " + acc.getName() + " (ID: " + acc.getAccountNo() + ") Amt: ₹" + amt);
            return true;
        }
        logger.log(acc.getAccountNo(), "FAILED_WITHDRAW: " + acc.getName() + " - Low Balance for ₹" + amt);
        return false;
    }
    // 3. TRANSFER 
    public boolean transfer(Account from, Account to, double amt) {
        if (from.withdraw(amt)) {
            to.deposit(amt);
            
            logger.log(from.getAccountNo(), "TRANSFER: Sent to " + to.getName() + 
            " (ID: " + to.getAccountNo() + ") | Amt: ₹" + amt);
            logger.log(to.getAccountNo(), "TRANSFER: Received from " + from.getName() +
             " (ID: " + from.getAccountNo() + ") | Amt: ₹" + amt);
            
            return true;
        }
        logger.log(from.getAccountNo(), "FAILED_TRANSFER: " + from.getName() + " had insufficient funds for ₹" + amt);
        return false;
    }
}