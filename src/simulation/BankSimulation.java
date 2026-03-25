package simulation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import model.Account;
import service.TransactionService;

public class BankSimulation {
    private final TransactionService service;

    public BankSimulation(TransactionService service) {
        this.service = service;
    }

    public void run(List<Account> accounts, int threads) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            Account b = accounts.get((i + 1) % accounts.size());
            pool.submit(new TransferTask(a, b, 100.0, service));
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}