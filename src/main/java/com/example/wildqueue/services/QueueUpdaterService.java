package com.example.wildqueue.services;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.utils.ThreadUtils;
import com.example.wildqueue.utils.Utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;

public class QueueUpdaterService {
	private static final QueueUpdaterService INSTANCE = new QueueUpdaterService();
	private static final long DEFAULT_UPDATE_INTERVAL = 5000;

	private Thread updateThread;
	private boolean isRunning = false;
	private Consumer<List<PriorityNumber>> currentSubscriber;
	private PriorityNumber lastFetchedByNumber;

	private QueueUpdaterService() {}

	public static QueueUpdaterService getInstance() {
		return INSTANCE;
	}

	public synchronized void subscribe(Consumer<List<PriorityNumber>> callback) {
		if (callback == null) throw new IllegalArgumentException("Callback cannot be null");

		currentSubscriber = callback;

		if (!isRunning) {
			startUpdateThread();
		}
	}

	public synchronized void unsubscribe(Consumer<List<PriorityNumber>> callback) {
		if (currentSubscriber == callback) {
			currentSubscriber = null;
			stopUpdateThread();
		}
	}

	public synchronized void setLastFetched(PriorityNumber lastFetched) {
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
		PriorityNumber lastFetched;
		synchronized (this) {
			lastFetched = this.lastFetchedByNumber;
			if (lastFetched == null) return;
		}

		System.out.println("Latest fetched num " + lastFetched.getPriorityNumber());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		Timestamp lastModifiedSince = new Timestamp(System.currentTimeMillis() - 8000);
		System.out.println("Checking for updates since: " + lastModifiedSince);

		List<PriorityNumber> updatedQueue = PriorityNumberDAO.getPriorityNumbersSince(
				lastFetched.getPriorityNumber(),
				lastModifiedSince
		);

		if (!updatedQueue.isEmpty()) {
			String latestFetchedPriority = updatedQueue.get(updatedQueue.size() - 1).getPriorityNumber();

			if (Utils.comparePriorityNumbers(lastFetched.getPriorityNumber(), latestFetchedPriority) <= 0) {
				synchronized (this) {
					this.lastFetchedByNumber = updatedQueue.get(updatedQueue.size() - 1);
				}
				System.out.println("New priority numbers found, updating...");
			} else {
				System.out.println("Status changes detected (no new numbers)");
			}

			notifySubscriber(updatedQueue);
		}
	}

	private void notifySubscriber(List<PriorityNumber> updates) {
		Consumer<List<PriorityNumber>> subscriber;
		synchronized (this) {
			subscriber = currentSubscriber;
		}
		if (subscriber != null) {
			ThreadUtils.runOnFxThread(() -> subscriber.accept(updates));
		}
	}
}