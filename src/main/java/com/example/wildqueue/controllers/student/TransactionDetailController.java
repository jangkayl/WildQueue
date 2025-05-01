package com.example.wildqueue.controllers.student;

import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class TransactionDetailController {
	@FXML private ScrollPane scrollPane;
	@FXML private Text queueNumberText;
	@FXML private Text statusText;
	@FXML private Text generatedTimeText;
	@FXML private Text calledTimeText;
	@FXML private Text completedTimeText;
	@FXML private Text windowText;
	@FXML private Text studentText;
	@FXML private Text studentIdText;
	@FXML private Text purposeText;
	@FXML private Text amountText;
	@FXML private Label descriptionText;
	@FXML private Button backButton;
	@FXML private Circle statusCircle;
	@FXML private Text tellerIdText;
	@FXML private Text txtAmount;

	private Transaction transaction;
	private StudentMainController mainController;

	@FXML
	public void initialize(){
		Utils.scrollPaneSetup(scrollPane);
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
		updateUI();
	}

	public void setMainController(StudentMainController mainController) {
		this.mainController = mainController;
	}

	private void updateUI() {
		if (transaction == null) return;

		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

		queueNumberText.setText(transaction.getPriorityNumber());
		windowText.setText(String.valueOf(transaction.getWindowNumber()));
		studentText.setText(transaction.getStudentName());
		studentIdText.setText(transaction.getStudentId());
		purposeText.setText(transaction.getTransactionType());
		tellerIdText.setText(transaction.getTellerId() != null ? transaction.getTellerId() : "--");

		if (Objects.equals(transaction.getTransactionType(), "Payment")) {
			amountText.setText(String.format("₱%,.2f", transaction.getAmount()));
			txtAmount.setVisible(true);
			txtAmount.setManaged(true);
			amountText.setVisible(true);
			amountText.setManaged(true);
		} else {
			txtAmount.setVisible(false);
			txtAmount.setManaged(false);
			amountText.setVisible(false);
			amountText.setManaged(false);
		}

		descriptionText.setText(transaction.getTransactionDetails());
		generatedTimeText.setText(timeFormat.format(transaction.getTransactionDate()));

		if (transaction.getCalledTime() != null) {
			calledTimeText.setText(timeFormat.format(transaction.getCalledTime()));
		} else {
			calledTimeText.setText("--");
		}

		if (transaction.getCompletionDate() != null) {
			completedTimeText.setText(timeFormat.format(transaction.getCompletionDate()));
		} else {
			completedTimeText.setText("--");
		}

		String status = transaction.getStatus();
		statusText.setText(status);

		switch (status.toUpperCase()) {
			case "COMPLETED":
				statusText.setStyle("-fx-fill: #4CAF50;");
				statusCircle.setFill(Color.valueOf("#4CAF50"));
				break;
			case "PROCESSING":
				statusText.setStyle("-fx-fill: #FFC107;");
				statusCircle.setFill(Color.valueOf("#FFC107"));
				break;
			case "PENDING":
				statusText.setStyle("-fx-fill: #2196F3;");
				statusCircle.setFill(Color.valueOf("#2196F3"));
				break;
			default:
				statusText.setStyle("-fx-fill: #9E9E9E;");
				statusCircle.setFill(Color.valueOf("#9E9E9E"));
		}
	}

	@FXML
	public void goBack() {
		if (mainController != null) {
			mainController.navigateToHistory();
		}
	}
}