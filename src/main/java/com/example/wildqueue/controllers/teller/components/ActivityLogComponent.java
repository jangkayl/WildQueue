package com.example.wildqueue.controllers.teller.components;

import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.services.TransactionUpdaterService;
import com.example.wildqueue.utils.ActivityEvent;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityLogComponent {
	private List<Transaction> transactionList;
	private final VBox activityContainer;
	private PriorityNumber currentServingNumber;
	private final Text currentNumberText;
	private final Text currentStudentText;

	public ActivityLogComponent(List<Transaction> transactionList, VBox activityContainer, PriorityNumber currentServingNumber, Text currentNumberText, Text currentStudentText) {
		this.transactionList = transactionList;
		this.activityContainer = activityContainer;
		this.currentServingNumber = currentServingNumber;
		this.currentNumberText = currentNumberText;
		this.currentStudentText = currentStudentText;
	}

	public void initializeTransactions() {
		transactionList = TransactionManager.getTransactionList();
		updateTransactionUI();

		Transaction lastFetched = !transactionList.isEmpty() ?
				transactionList.get(transactionList.size() - 1) : null;

		TransactionUpdaterService transactionUpdaterService = TransactionUpdaterService.getInstance();
		transactionUpdaterService.setLastFetched(lastFetched);
		transactionUpdaterService.subscribe(this::handleTransactionUpdates);

		updateTransactionUI();
	}

	public void updateTransactionUI() {
		activityContainer.getChildren().clear();

		List<ActivityEvent> activityEvents = new ArrayList<>();

		for (Transaction transaction : transactionList) {
			if(Objects.equals(transaction.getStatus(), PriorityStatus.CANCELLED.toString())){
				activityEvents.add(new ActivityEvent(
						"CANCELLED",
						transaction.getPriorityNumber(),
						transaction.getStudentName(),
						transaction.getStudentId(),
						transaction.getCalledTime(),
						Color.RED
				));
			}

			if (transaction.getCalledTime() != null && !Objects.equals(transaction.getStatus(), PriorityStatus.CANCELLED.toString())) {
				activityEvents.add(new ActivityEvent(
						"CALLED",
						transaction.getPriorityNumber(),
						transaction.getStudentName(),
						transaction.getStudentId(),
						transaction.getCalledTime(),
						Color.GOLD
				));
			}

			if (transaction.getCompletionDate() != null && !Objects.equals(transaction.getStatus(), PriorityStatus.CANCELLED.toString())) {
				activityEvents.add(new ActivityEvent(
						"COMPLETED",
						transaction.getPriorityNumber(),
						transaction.getStudentName(),
						transaction.getStudentId(),
						transaction.getCompletionDate(),
						Color.LIMEGREEN
				));
			}
		}

		List<ActivityEvent> recentActivities = activityEvents.stream()
				.sorted(Comparator.comparing(ActivityEvent::getDate).reversed())
				.limit(5)
				.toList();

		for (ActivityEvent event : recentActivities) {
			HBox activityItem = new HBox(8);
			activityItem.setAlignment(Pos.CENTER_LEFT);
			activityItem.setStyle("-fx-padding: 6; -fx-background-color: #fff9f0; -fx-background-radius: 3;");

			Circle statusCircle = new Circle(4);
			statusCircle.setFill(event.getColor());

			String formattedTime = new SimpleDateFormat("hh:mm a").format(event.getDate());

			Text activityText = new Text(String.format("%s #%s | %s - %s (%s)",
					event.getAction(),
					event.getPriorityNumber(),
					event.getStudentName(),
					event.getStudentId(),
					formattedTime));
			activityText.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

			activityItem.getChildren().addAll(statusCircle, activityText);
			activityContainer.getChildren().add(activityItem);
		}
	}

	public void handleTransactionUpdates(List<Transaction> updatedTransactions) {
		boolean needsUpdate = false;

		for (Transaction updatedTransaction : updatedTransactions) {
			Optional<Transaction> existingTransaction = transactionList.stream()
					.filter(t -> t.getTransactionId() == updatedTransaction.getTransactionId())
					.findFirst();

			if (existingTransaction.isPresent()) {
				if (!Objects.equals(existingTransaction.get().getStatus(), updatedTransaction.getStatus()) ||
						!Objects.equals(existingTransaction.get().getCalledTime(), updatedTransaction.getCalledTime()) ||
						!Objects.equals(existingTransaction.get().getCompletionDate(), updatedTransaction.getCompletionDate())) {

					existingTransaction.get().setStatus(updatedTransaction.getStatus());
					existingTransaction.get().setCalledTime(updatedTransaction.getCalledTime());
					existingTransaction.get().setCompletionDate(updatedTransaction.getCompletionDate());
					needsUpdate = true;
				}
			} else {
				transactionList.add(updatedTransaction);
				needsUpdate = true;
			}
		}

		if (needsUpdate) {
			updateTransactionUI();

			if (currentServingNumber != null) {
				Optional<Transaction> currentTransaction = transactionList.stream()
						.filter(t -> t.getPriorityNumber().equals(currentServingNumber.getPriorityNumber()))
						.findFirst();

				if (currentTransaction.isPresent() &&
						currentTransaction.get().getStatus().equals(PriorityStatus.COMPLETED.toString())) {
					currentNumberText.setText("--");
					currentStudentText.setText("--");
					currentServingNumber = null;
				}
			}
		}
	}
}
