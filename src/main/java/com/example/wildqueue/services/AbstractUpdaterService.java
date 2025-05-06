package com.example.wildqueue.services;

import com.example.wildqueue.utils.ThreadUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;

public abstract class AbstractUpdaterService<T, ID> {
	private Thread updateThread;
	private boolean isRunning = false;
	private Consumer<List<T>> currentSubscriber;
	private T lastFetchedItem;
	private static final long DEFAULT_UPDATE_INTERVAL = 5000;

	protected abstract List<T> fetchUpdatedItems(T lastFetched, Timestamp lastModifiedSince);
	protected abstract ID getItemIdentifier(T item);
	protected abstract void onItemsUpdated(List<T> updatedItems);

	public synchronized void subscribe(Consumer<List<T>> callback) {
		if (callback == null) throw new IllegalArgumentException("Callback cannot be null");
		currentSubscriber = callback;
		if (!isRunning) startUpdateThread();
	}

	public synchronized void unsubscribe(Consumer<List<T>> callback) {
		if (currentSubscriber == callback) {
			currentSubscriber = null;
			stopUpdateThread();
		}
	}

	public synchronized void setLastFetched(T lastFetched) {
		this.lastFetchedItem = lastFetched;
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
			lastFetchedItem = null;
		}
	}

	private void fetchUpdates() {
		T lastFetched;

		synchronized (this) {
			lastFetched = this.lastFetchedItem;
		}

		if (lastFetched != null) {
			System.out.println("Latest fetched item: " + getItemIdentifier(lastFetched));
		}

		Timestamp lastModifiedSince = new Timestamp(System.currentTimeMillis() - 7000);
		System.out.println("Checking for updates since: " + lastModifiedSince);

		List<T> updatedItems = fetchUpdatedItems(lastFetched, lastModifiedSince);

		if (!updatedItems.isEmpty()) {
			onItemsUpdated(updatedItems);
			synchronized (this) {
				this.lastFetchedItem = updatedItems.get(updatedItems.size() - 1);
			}
			System.out.println("New items found, updating...");
			notifySubscriber(updatedItems);
		}
	}

	private void notifySubscriber(List<T> updates) {
		Consumer<List<T>> subscriber;
		synchronized (this) {
			subscriber = currentSubscriber;
		}
		if (subscriber != null) {
			ThreadUtils.runOnFxThread(() -> subscriber.accept(updates));
		}
	}
}
