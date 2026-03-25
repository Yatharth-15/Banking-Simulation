package simulation;

import model.Account;
import service.TransactionService;

public class TransferTask implements Runnable {
    private final Account from;
    private final Account to;
    private final double amount;
    private final TransactionService service;

    public TransferTask(Account from, Account to, double amount, TransactionService service) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.service = service;
    }

    @Override
    public void run() {
        service.transfer(from, to, amount);
    }
}