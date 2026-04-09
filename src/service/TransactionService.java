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
   public void transfer(Account from, Account to, double amount) {

    Account firstLock = from.getAccountNo() < to.getAccountNo() ? from : to;
    Account secondLock = from.getAccountNo() < to.getAccountNo() ? to : from;

    synchronized(firstLock) {
        synchronized(secondLock) {

            if (from.withdraw(amount)) {
                to.deposit(amount);
                System.out.println("Transfer successful");
            } else {
                System.out.println("Insufficient balance");
            }

        }
    }
   }
}