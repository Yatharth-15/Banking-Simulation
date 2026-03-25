package deadlock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private final ConcurrentHashMap<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ReentrantLock getLock(int accountId) {
        return locks.computeIfAbsent(accountId, id -> new ReentrantLock());
    }
}