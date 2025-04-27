package com.example.wildqueue.utils;

import javafx.application.Platform;

import java.util.function.Consumer;

public class ThreadUtils {

	public static Thread createDaemonIntervalThread(
			Runnable task,
			long intervalMillis,
			Consumer<Exception> exceptionHandler) {

		Thread thread = new Thread(() -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					task.run();
					Thread.sleep(intervalMillis);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				exceptionHandler.accept(e);
			}
		});

		thread.setDaemon(true);
		thread.start();
		return thread;
	}

	public static void runOnFxThread(Runnable task) {
		if (Platform.isFxApplicationThread()) {
			task.run();
		} else {
			Platform.runLater(task);
		}
	}
}