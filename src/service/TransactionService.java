package service;

import logging.TransactionLogger;
import model.Account;

public class TransactionService {
    private final TransactionLogger logger = new TransactionLogger();

    // 1. DEPOSIT
    public void deposit(Account acc, double amt) {
        if (amt > 0) {
            acc.deposit(amt);
            logger.log(acc.getAccountNo(), "DEPOSIT: ₹" + amt + " | New Balance: ₹" + acc.getBalance());
        }
    }

    // 2. WITHDRAW
    public boolean withdraw(Account acc, double amt) {
        if (acc.withdraw(amt)) {
            logger.log(acc.getAccountNo(), "WITHDRAW: ₹" + amt + " | New Balance: ₹" + acc.getBalance());
            return true;
        }
        logger.log(acc.getAccountNo(), "FAILED_WITHDRAW: Low Balance for ₹" + amt);
        return false;
    }

    // 3. TRANSFER 
    public boolean transfer(Account from, Account to, double amount) {
        // Deadlock prevention logic (Already good!)
        Account firstLock = from.getAccountNo() < to.getAccountNo() ? from : to;
        Account secondLock = from.getAccountNo() < to.getAccountNo() ? to : from;

        synchronized(firstLock) {
            synchronized(secondLock) {
                if (from.withdraw(amount)) {
                    to.deposit(amount);
                    
                    // --- THE FIX: Update History for BOTH accounts ---
                    String logMsgFrom = "TRANSFER_OUT: ₹" + amount + " to ID: " + to.getAccountNo();
                    String logMsgTo = "TRANSFER_IN: ₹" + amount + " from ID: " + from.getAccountNo();
                    
                    logger.log(from.getAccountNo(), logMsgFrom);
                    logger.log(to.getAccountNo(), logMsgTo);

                    System.out.println("Transfer successful: ₹" + amount + " from " + from.getAccountNo() + " to " + to.getAccountNo());
                    return true;
                } else {
                    System.out.println("Transfer failed: Insufficient balance in ID " + from.getAccountNo());
                    return false;
                }
            }
        }
    }
}