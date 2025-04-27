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
					long startTime = System.currentTimeMillis();

					try {
						task.run();
					} catch (Exception e) {
						exceptionHandler.accept(e);
					}

					long elapsed = System.currentTimeMillis() - startTime;
					long remainingSleep = intervalMillis - elapsed;

					if (remainingSleep > 0) {
						try {
							Thread.sleep(remainingSleep);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}
			} finally {
				System.out.println("Thread exiting");
			}
		});

		thread.setDaemon(true);
		thread.setName("QueueUpdaterThread");
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