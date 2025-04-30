package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.TransactionManager;
import com.example.wildqueue.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StudentHistoryController {
	@FXML private ScrollPane scrollPane;
	@FXML private VBox historyContainer;

	@FXML
	public void initialize() {
		Utils.scrollPaneSetup(scrollPane);
		loadTransactionHistory();
	}

	private void loadTransactionHistory() {
		historyContainer.getChildren().clear();
		historyContainer.setStyle("-fx-padding: 15 20 20 20; -fx-background: #F8F9FA;");

		List<Transaction> transactionList = TransactionManager.getTransactionList().stream()
				.filter(t -> PriorityStatus.COMPLETED.toString().equalsIgnoreCase(t.getStatus()))
				.sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
				.toList();

		Map<String, List<Transaction>> transactionsByDate = transactionList.stream()
				.collect(Collectors.groupingBy(
						t -> new SimpleDateFormat("MMMM dd, yyyy").format(t.getTransactionDate())
				));

		List<String> sortedDates = new ArrayList<>(transactionsByDate.keySet());
		sortedDates.sort(Collections.reverseOrder());

		for (String date : sortedDates) {
			VBox dateGroup = createDateGroup(date, transactionsByDate.get(date));
			historyContainer.getChildren().add(dateGroup);
		}

		if (transactionList.isEmpty()) {
			historyContainer.getChildren().add(createEmptyState());
		}
	}

	private VBox createDateGroup(String date, List<Transaction> transactions) {
		VBox dateGroup = new VBox();
		dateGroup.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20;" +
				"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 0, 2, 0, 2);");
		dateGroup.setSpacing(10);

		Text dateHeader = new Text(date);
		dateHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #800000;");
		dateGroup.getChildren().add(dateHeader);

		for (int i = 0; i < transactions.size(); i++) {
			Transaction transaction = transactions.get(i);
			HBox transactionItem = createTransactionItem(transaction);
			dateGroup.getChildren().add(transactionItem);

			if (i < transactions.size() - 1) {
				Rectangle separator = new Rectangle();
				separator.setHeight(1);
				separator.setStyle("-fx-fill: #E9ECEF;");
				dateGroup.getChildren().add(separator);
			}
		}

		return dateGroup;
	}

	private HBox createTransactionItem(Transaction transaction) {
		HBox item = new HBox();
		item.setStyle("-fx-padding: 10 0;");
		item.setSpacing(15);

		VBox windowBox = new VBox();
		windowBox.setStyle("-fx-background-color: rgba(128, 0, 0, 0.1); -fx-background-radius: 8; " +
				"-fx-padding: 10; -fx-min-width: 80; -fx-alignment: CENTER;");
		windowBox.setSpacing(2);

		Label windowLabel = new Label("Window");
		windowLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6C757D;");

		Text windowNumber = new Text(String.valueOf(transaction.getWindowNumber()));
		windowNumber.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #800000;");

		windowBox.getChildren().addAll(windowLabel, windowNumber);

		VBox detailsBox = new VBox();
		detailsBox.setSpacing(2);

		Text queueNumber = new Text("Queue " + transaction.getPriorityNumber());
		queueNumber.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #212529;");

		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		String calledTime = transaction.getCalledTime() != null ?
				"Called at " + timeFormat.format(transaction.getCalledTime()) : "Not called";
		String completedTime = transaction.getCompletionDate() != null ?
				"Completed at " + timeFormat.format(transaction.getCompletionDate()) : "Not completed";

		Text calledText = new Text(calledTime);
		calledText.setStyle("-fx-font-size: 12px; -fx-fill: #6C757D;");

		Text completedText = new Text(completedTime);
		completedText.setStyle("-fx-font-size: 12px; -fx-fill: #6C757D;");

		detailsBox.getChildren().addAll(queueNumber, calledText, completedText);

		item.getChildren().addAll(windowBox, detailsBox);
		return item;
	}

	private VBox createEmptyState() {
		VBox emptyState = new VBox();
		emptyState.setStyle("-fx-padding: 40 20; -fx-alignment: CENTER; -fx-spacing: 15;");

		Rectangle placeholder = new Rectangle(80, 80);
		placeholder.setStyle("-fx-fill: #E9ECEF; -fx-arc-width: 40; -fx-arc-height: 40;");

		Text title = new Text("No Queue History");
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #6C757D;");

		Text subtitle = new Text("Your previous queue numbers will appear here");
		subtitle.setStyle("-fx-font-size: 14px; -fx-fill: #6C757D;");

		emptyState.getChildren().addAll(placeholder, title, subtitle);
		return emptyState;
	}

	public void goToHistory(ActionEvent actionEvent) {
		// Navigation logic
	}

	public void goToProfile(ActionEvent actionEvent) {
		// Navigation logic
	}

	public void goToHome(ActionEvent actionEvent) {
		// Navigation logic
	}
}