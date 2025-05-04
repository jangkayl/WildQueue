package com.example.wildqueue.controllers.teller;

import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RecentActivitiesController implements Initializable {

	@FXML private VBox activityContainer;
	@FXML private Label titleLabel;
	@FXML private Label countLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadRecentActivities();
	}

	private void loadRecentActivities() {
		List<Transaction> transactions = TransactionManager.getTransactionList();
		transactions = transactions.stream()
				.sorted((t1, t2) -> t2.getCalledTime().compareTo(t1.getCalledTime()))
				.toList();

		countLabel.setText("Showing " + transactions.size() + " activities");
		activityContainer.getChildren().clear();

		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm a");

		for (Transaction transaction : transactions) {
			HBox activityItem = new HBox(15);
			activityItem.setStyle("-fx-background-color: #fff9f0; -fx-background-radius: 8; -fx-padding: 15;");
			activityItem.setAlignment(Pos.CENTER_LEFT);
			activityItem.setPrefWidth(800);

			// Status Indicator (Left-most element)
			VBox statusContainer = new VBox();
			statusContainer.setAlignment(Pos.CENTER);
			statusContainer.setMinWidth(30);

			Circle statusCircle = new Circle(6);
			String statusText = "";
			Color statusColor = Color.GRAY;

			if (transaction.getCompletionDate() != null) {
				statusText = "COMPLETED";
				statusColor = Color.LIMEGREEN;
			} else if (transaction.getCalledTime() != null) {
				statusText = "CALLED";
				statusColor = Color.GOLD;
			} else {
				statusText = "PENDING";
				statusColor = Color.LIGHTGRAY;
			}
			statusCircle.setFill(statusColor);
			statusContainer.getChildren().add(statusCircle);

			// Priority Number (Fixed width)
			VBox numberContainer = new VBox();
			numberContainer.setAlignment(Pos.CENTER_LEFT);
			numberContainer.setMinWidth(70);

			Label numberLabel = new Label(transaction.getPriorityNumber());
			numberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #800000;");
			numberContainer.getChildren().add(numberLabel);

			// Student Information (Flexible width)
			VBox studentInfo = new VBox(3);
			studentInfo.setAlignment(Pos.CENTER_LEFT);
			studentInfo.setMinWidth(150);
			HBox.setHgrow(studentInfo, Priority.SOMETIMES);

			Label nameLabel = new Label(transaction.getStudentName());
			nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

			Label idLabel = new Label(transaction.getStudentId());
			idLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");

			studentInfo.getChildren().addAll(nameLabel, idLabel);

			// Transaction Details (Flexible width)
			VBox transactionDetails = new VBox(3);
			transactionDetails.setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(transactionDetails, Priority.ALWAYS);

			Label typeLabel = new Label("Transaction: " + transaction.getTransactionType());
			typeLabel.setStyle("-fx-font-size: 13;");

			Label purposeLabel = new Label("Purpose: " + transaction.getTransactionDetails());
			purposeLabel.setStyle("-fx-font-size: 13; -fx-wrap-text: true;");
			purposeLabel.setMaxWidth(300);

			transactionDetails.getChildren().addAll(typeLabel, purposeLabel);

			// Time and Status (Right-aligned)
			VBox timeStatus = new VBox(5);
			timeStatus.setAlignment(Pos.CENTER_RIGHT);
			timeStatus.setMinWidth(200);

			Label statusLabel = new Label(statusText);
			statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + toHexString(statusColor) + ";");

			String timeText = buildTimeText(transaction, dateFormat);
			Label timeLabel = new Label(timeText);
			timeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
			timeLabel.setAlignment(Pos.CENTER_RIGHT);

			timeStatus.getChildren().addAll(statusLabel, timeLabel);

			activityItem.getChildren().addAll(
					statusContainer,
					numberContainer,
					studentInfo,
					transactionDetails,
					timeStatus
			);

			Separator separator = new Separator();
			separator.setPadding(new Insets(5, 0, 5, 0));
			activityContainer.getChildren().addAll(activityItem, separator);
		}
	}

	private String buildTimeText(Transaction transaction, SimpleDateFormat dateFormat) {
		if (transaction.getCalledTime() != null) {
			String timeText = "Called: " + dateFormat.format(transaction.getCalledTime());
			if (transaction.getCompletionDate() != null) {
				timeText += "\nCompleted: " + dateFormat.format(transaction.getCompletionDate());
			}
			return timeText;
		}
		return "Created: " + dateFormat.format(transaction.getTransactionDate());
	}

	private String toHexString(Color color) {
		return String.format("#%02X%02X%02X",
				(int)(color.getRed() * 255),
				(int)(color.getGreen() * 255),
				(int)(color.getBlue() * 255));
	}

	@FXML
	public void handleBack(ActionEvent actionEvent) {
		Stage stage = (Stage) titleLabel.getScene().getWindow();
		stage.close();
	}
}