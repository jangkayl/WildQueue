package com.example.wildqueue.services;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.ThreadUtils;
import com.example.wildqueue.utils.TransactionManager;
import com.example.wildqueue.utils.Utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;

public class TransactionUpdaterService {
    private static final TransactionUpdaterService INSTANCE = new TransactionUpdaterService();
    private static final long DEFAULT_UPDATE_INTERVAL = 5000;

    private Thread updateThread;
    private boolean isRunning = false;
    private Consumer<List<Transaction>> currentSubscriber;
    private Transaction lastFetchedByNumber;

    private TransactionUpdaterService() {}

    public static TransactionUpdaterService getInstance() {
        return INSTANCE;
    }

    public synchronized void subscribe(Consumer<List<Transaction>> callback) {
        if (callback == null) throw new IllegalArgumentException("Callback cannot be null");

        currentSubscriber = callback;

        if (!isRunning) {
            startUpdateThread();
        }
    }

    public synchronized void unsubscribe(Consumer<List<Transaction>> callback) {
        if (currentSubscriber == callback) {
            currentSubscriber = null;
            stopUpdateThread();
        }
    }

    public synchronized void setLastFetched(Transaction lastFetched) {
        this.lastFetchedByNumber = lastFetched;
    }

    private void startUpdateThread() {
        isRunning = true;
        updateThread = ThreadUtils.createDaemonIntervalThread(
                this::fetchUpdates,
                DEFAULT_UPDATE_INTERVAL,
                e -> {
                    synchronized (this) { isRunning = false; }
                    e.printStackTrace();
                }
        );
    }

    public void stopUpdateThread() {
        if (updateThread != null) {
            updateThread.interrupt();
            isRunning = false;
            updateThread = null;
            currentSubscriber = null;
            lastFetchedByNumber = null;
        }
    }

    private void fetchUpdates() {
        Transaction lastFetched;
        synchronized (this) {
            lastFetched = this.lastFetchedByNumber;
            if (lastFetched == null) return;
        }

        System.out.println("Transaction latest fetched " + lastFetched.getPriorityNumber());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Timestamp lastModifiedSince = new Timestamp(System.currentTimeMillis() - 6500);
        System.out.println("Checking for updates since: " + lastModifiedSince);

        List<Transaction> updatedTransaction = TransactionDAO.getTransactionsSince(
                lastFetched.getPriorityNumber(),
                lastModifiedSince
        );

        if (!updatedTransaction.isEmpty()) {
            String latestFetchedPriority = updatedTransaction.get(updatedTransaction.size() - 1).getPriorityNumber();

            updatedTransaction.forEach(TransactionManager::addOrUpdateTransaction);

            if (Utils.comparePriorityNumbers(lastFetched.getPriorityNumber(), latestFetchedPriority) <= 0) {
                synchronized (this) {
                    this.lastFetchedByNumber = updatedTransaction.get(updatedTransaction.size() - 1);
                }
                System.out.println("New transaction found, updating...");
            } else {
                System.out.println("Status changes detected (no new numbers)");
            }

            notifySubscriber(updatedTransaction);
        }
    }

    private void notifySubscriber(List<Transaction> updates) {
        Consumer<List<Transaction>> subscriber;
        synchronized (this) {
            subscriber = currentSubscriber;
        }
        if (subscriber != null) {
            ThreadUtils.runOnFxThread(() -> subscriber.accept(updates));
        }
    }
}
