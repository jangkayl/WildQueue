package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.PriorityNumberManager;
import com.example.wildqueue.utils.SessionManager;
import com.example.wildqueue.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class StudentDetailsFormController {
	@FXML public ScrollPane scrollPane;
	@FXML private ComboBox<String> transactionTypeCombo;
	@FXML private TextArea detailsTextArea;
	@FXML private TextField amountField;
	@FXML private VBox amountContainer;
	@FXML private Text studentIdText;
	@FXML private Text studentNameText;
	@FXML private Button submitButton;
	@FXML private Button backButton;

	private StudentMainController mainController;
	private List<PriorityNumber> priorityNumbers = PriorityNumberManager.getPriorityNumberList();
	private final String currentStudentId = SessionManager.getCurrentUser().getInstitutionalId();
	private final String currentStudentName = SessionManager.getCurrentUser().getName();

	@FXML
	public void initialize() {
		Utils.scrollPaneSetup(scrollPane);

		studentIdText.setText("ID: " + currentStudentId);
		studentNameText.setText("Name: " + currentStudentName);

		transactionTypeCombo.getItems().addAll(
				"Choose a transaction type",
				"Registration",
				"Payment",
				"Document Request",
				"Academic Inquiry",
				"Other"
		);
		transactionTypeCombo.getSelectionModel().selectFirst();

		amountContainer.setVisible(false);
		amountContainer.setManaged(false);
		amountField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*(\\.\\d*)?")) {
				amountField.setText(oldValue);
			}
		});

		transactionTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
			boolean isPayment = "Payment".equals(newVal);
			amountContainer.setVisible(isPayment);
			amountContainer.setManaged(isPayment);
		});

		backButton.setOnAction(event -> {
			if (mainController != null) {
				mainController.navigateToHome();
			}
		});
	}

	public void setMainController(StudentMainController mainController) {
		this.mainController = mainController;
	}

	@FXML
	private void handleFormSubmission() {
		String transactionType = transactionTypeCombo.getValue();
		if (transactionType == null || "Choose a transaction type".equals(transactionType)) {
			Utils.showAlert(
					Alert.AlertType.ERROR,
					"Validation Error",
					"Please select a valid transaction type",
					ButtonType.OK
			);
			transactionTypeCombo.setStyle("-fx-border-color: #FF0000; -fx-border-width: 1px;");
			return;
		}

		String additionalDetails = detailsTextArea.getText().trim();
		if (additionalDetails.length() < 10) {
			Utils.showAlert(
					Alert.AlertType.ERROR,
					"Validation Error",
					"Please provide at least 10 characters of details",
					ButtonType.OK
			);
			return;
		}

		double amount = 0;
		if ("Payment".equals(transactionType)) {
			try {
				amount = Double.parseDouble(amountField.getText());
				if (amount <= 0) {
					Utils.showAlert(
							Alert.AlertType.ERROR,
							"Validation Error",
							"Payment amount must be greater than 0",
							ButtonType.OK
					);
					return;
				}
			} catch (NumberFormatException e) {
				Utils.showAlert(
						Alert.AlertType.ERROR,
						"Validation Error",
						"Please enter a valid payment amount",
						ButtonType.OK
				);
				return;
			}
		}

		PriorityNumber latestPN = PriorityNumberDAO.getLatestPriorityNumber();
		String priorityNumber = Utils.generatePriorityNumberString(latestPN);
		PriorityNumber pn = new PriorityNumber(priorityNumber, currentStudentId, "", PriorityStatus.PENDING, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));

		Transaction transaction = new Transaction(
				0,
				priorityNumber,
				0,
				currentStudentName,
				currentStudentId,
				null,
				amount,
				transactionType,
				additionalDetails,
				new Date(System.currentTimeMillis()),
				new Timestamp(System.currentTimeMillis()),
				"Pending",
				null,
				null
		);

		System.out.println("Generated Priority Number: " + priorityNumber);
		transaction.printAllDetails();

		Utils.showAlert(
				Alert.AlertType.CONFIRMATION,
				"Request Submitted",
				"Your priority number: " + priorityNumber + "\n" +
						"Please wait for your number to be called.",
				ButtonType.OK
		);

		if (mainController != null) {
			mainController.navigateToHome();
			if (mainController.getHomepageController() != null) {
				mainController.getHomepageController().generatePriorityNumber(
						priorityNumber,
						transactionType,
						additionalDetails,
						pn,
						amount
				);
			}
		}
	}


}