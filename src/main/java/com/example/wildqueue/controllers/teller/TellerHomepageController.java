package com.example.wildqueue.controllers.teller;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.models.User;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.utils.PriorityNumberManager;
import com.example.wildqueue.utils.SessionManager;
import com.example.wildqueue.utils.Utils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TellerHomepageController {
	@FXML private Label tellerNameLabel;
	@FXML private Button logoutButton;
	@FXML private Text tellerNameSidebar;
	@FXML private Text windowNumberText;
	@FXML private Button callButton;
	@FXML private Button completeButton;
	@FXML private Button transferButton;
	@FXML private Button windowToggleButton;
	@FXML private Button priorityButton;
	@FXML private Button recallButton;
	@FXML private Button notifyButton;
	@FXML private Button viewHistoryButton;
	@FXML private Text currentNumberText;
	@FXML private Text currentStudentText;
	@FXML private Text waitingCountText;
	@FXML private Text servedTodayText;
	@FXML private Text avgTimeText;
	@FXML private Text windowStatusText;
	@FXML private Button refreshActivityButton;
	@FXML private VBox activityContainer;
	@FXML private HBox hbQueue;

	private User currentUser;
	private List<PriorityNumber> priorityQueue;
	private PriorityNumber currentServingNumber;
	private int windowNumber = 1;

	@FXML
	public void initialize() {
		loadUserData();
		initializePriorityQueue();
		updateServedTodayText();
//		setupWindowNumber();
	}

	private void loadUserData() {
		currentUser = SessionManager.getCurrentUser();

		if (currentUser != null) {
			tellerNameLabel.setText("Teller: " + currentUser.getName());
			tellerNameSidebar.setText(currentUser.getName());
		} else {
			tellerNameLabel.setText("Guest");
			tellerNameSidebar.setText("Guest");
		}
	}

	private void setupWindowNumber() {
		windowNumber = TellerWindowDAO.getWindowNumberByTellerId(currentUser.getInstitutionalId());
		windowNumberText.setText("Window " + windowNumber);
	}

	public void initializePriorityQueue() {
		PriorityNumberManager.setPriorityNumberList(PriorityNumberDAO.getAllPriorityNumbers());

		priorityQueue = PriorityNumberManager.getPriorityNumberList();
		updateQueueUI();

		PriorityNumber lastFetched = !priorityQueue.isEmpty() ?
				priorityQueue.get(priorityQueue.size() - 1) : null;

		QueueUpdaterService queueUpdaterService = QueueUpdaterService.getInstance();
		queueUpdaterService.setLastFetched(lastFetched);
		queueUpdaterService.subscribe(this::handleQueueUpdates);

		updateQueueUI();
	}

	private void updateServedTodayText() {
		LocalDate today = LocalDate.now();

		List<Transaction> completedTransactionsToday = TransactionDAO.getCompletedTransactionsByTeller(currentUser.getInstitutionalId())
				.stream()
				.filter(transaction -> transaction.getCompletionDate().toLocalDateTime().isEqual(today.atStartOfDay()))
				.toList();

		servedTodayText.setText(String.valueOf(completedTransactionsToday.size()));
	}

	private void handleQueueUpdates(List<PriorityNumber> updatedNumbers) {
		boolean needsUpdate = false;

		for (PriorityNumber updatedNumber : updatedNumbers) {
			Optional<PriorityNumber> existingNumber = priorityQueue.stream()
					.filter(pn -> pn.getPriorityNumber().equals(updatedNumber.getPriorityNumber()))
					.findFirst();

			if (existingNumber.isPresent()) {
				if (!existingNumber.get().getStatus().equals(updatedNumber.getStatus())) {
					existingNumber.get().setStatus(updatedNumber.getStatus());
					needsUpdate = true;
				}
			} else {
				priorityQueue.add(updatedNumber);
				needsUpdate = true;
			}
		}

		if (needsUpdate) {
			updateQueueUI();
		}
	}

	private void updateQueueUI() {
		List<PriorityNumber> pendingQueue = priorityQueue.stream()
				.filter(pn -> PriorityStatus.PENDING.toString().equalsIgnoreCase(pn.getStatus().toString()))
				.toList();

		waitingCountText.setText(pendingQueue.size() + " people waiting");
		hbQueue.getChildren().clear();

		VBox rowsContainer = new VBox(5);
		rowsContainer.setAlignment(Pos.CENTER_LEFT);

		int maxVisibleNumbers = 5;
		int totalItems = pendingQueue.size();
		int numbersToShow = Math.min(totalItems, maxVisibleNumbers);

		HBox firstRow = createNewRow();
		for (int i = 0; i < Math.min(3, numbersToShow); i++) {
			firstRow.getChildren().add(createPriorityLabel(pendingQueue.get(i)));
		}
		rowsContainer.getChildren().add(firstRow);

		if (numbersToShow > 3) {
			HBox secondRow = createNewRow();

			for (int i = 3; i < numbersToShow; i++) {
				secondRow.getChildren().add(createPriorityLabel(pendingQueue.get(i)));
			}

			if (totalItems > maxVisibleNumbers) {
				secondRow.getChildren().add(createEllipsisLabel());
			}

			rowsContainer.getChildren().add(secondRow);
		} else if (totalItems > maxVisibleNumbers) {
			firstRow.getChildren().add(createEllipsisLabel());
		}

		hbQueue.getChildren().add(rowsContainer);
	}

	private HBox createNewRow() {
		HBox row = new HBox(5);
		row.setAlignment(Pos.CENTER_LEFT);
		return row;
	}

	private Label createPriorityLabel(PriorityNumber pn) {
		Label label = new Label(pn.getPriorityNumber());
		String backgroundColor = PriorityStatus.PENDING.toString().equalsIgnoreCase(pn.getStatus().toString())
				? "#8B0000" : "#5E0A15";

		label.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; " +
				"-fx-padding: 5 9; -fx-background-color: " + backgroundColor + "; -fx-background-radius: 20;");
		return label;
	}

	private Label createEllipsisLabel() {
		Label ellipsis = new Label("...");
		ellipsis.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;" +
				"-fx-padding: 5 9; -fx-background-color: #5E0A15; -fx-background-radius: 20; -fx-pref-width: 60px;");
		return ellipsis;
	}

	@FXML
	private void handleCallButton() {
		if (currentServingNumber != null) {
			Utils.showAlert(
					Alert.AlertType.WARNING,
					"Already Processing",
					"You are already processing number: " + currentServingNumber.getPriorityNumber(),
					ButtonType.OK
			);
			return;
		}

		Optional<PriorityNumber> nextNumber = priorityQueue.stream()
				.filter(pn -> PriorityStatus.PENDING.toString().equalsIgnoreCase(pn.getStatus().toString()))
				.findFirst();

		if (nextNumber.isPresent()) {
			currentServingNumber = nextNumber.get();
			currentServingNumber.setStatus(PriorityStatus.PROCESSING);

			Transaction transaction = TransactionDAO.getTransactionByPriorityNumber(currentServingNumber.getPriorityNumber());
			if (transaction != null) {
				transaction.setWindowNumber(windowNumber);
				transaction.setTellerId(currentUser.getInstitutionalId());
				transaction.setStatus(PriorityStatus.PROCESSING.toString());
				TransactionDAO.updateTransaction(transaction);
			}

			boolean success = PriorityNumberDAO.updatePriorityNumberStatus(currentServingNumber.getPriorityNumber() ,PriorityStatus.PROCESSING);

			if (success) {
				currentNumberText.setText(currentServingNumber.getPriorityNumber());
				currentStudentText.setText(currentServingNumber.getStudentId());
				updateQueueUI();
			} else {
				Utils.showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update priority number status", null);
			}
		} else {
			Utils.showAlert(Alert.AlertType.INFORMATION, "No Numbers", "There are no numbers to call.", null);
		}
	}

	@FXML
	private void handleCompleteButton() {
		if (currentServingNumber != null) {
			currentServingNumber.setStatus(PriorityStatus.COMPLETED);
			PriorityNumberDAO.updatePriorityNumber(currentServingNumber);

			Transaction transaction = TransactionDAO.getTransactionByPriorityNumber(currentServingNumber.getPriorityNumber());
			if (transaction != null) {
				transaction.setStatus(PriorityStatus.COMPLETED.toString());
				transaction.setCompletionDate(new Timestamp(System.currentTimeMillis()));
				TransactionDAO.updateTransaction(transaction);
			}

			currentNumberText.setText("--");
			currentStudentText.setText("--");
			currentServingNumber = null;

			updateQueueUI();
		} else {
			Utils.showAlert(Alert.AlertType.WARNING, "No Active Number", "There is no number currently being served.", null);
		}
	}

	@FXML
	private void handleTransferButton() {
//		if (currentServingNumber != null) {
//			currentServingNumber.setStatus(PriorityStatus.PENDING);
//			PriorityNumberDAO.updatePriorityNumber(currentServingNumber);
//
//			currentNumberText.setText("--");
//			currentStudentText.setText("--");
//			currentServingNumber = null;
//
//			updateQueueUI();
//		} else {
//			Utils.showAlert(Alert.AlertType.WARNING, "No Active Number", "There is no number currently being served.", null);
//		}
	}

	@FXML
	private void handlePriorityButton() {
//		// Find the next priority number
//		Optional<PriorityNumber> nextPriority = priorityQueue.stream()
//				.filter(pn -> PriorityStatus.PRIORITY.toString().equalsIgnoreCase(pn.getStatus().toString()))
//				.findFirst();
//
//		if (nextPriority.isPresent()) {
//			currentServingNumber = nextPriority.get();
//			currentServingNumber.setStatus(PriorityStatus.SERVING);
//			currentServingNumber.setWindowNumber(windowNumber);
//
//			PriorityNumberDAO.updatePriorityNumber(currentServingNumber);
//
//			currentNumberText.setText(currentServingNumber.getPriorityNumber());
//			currentStudentText.setText(currentServingNumber.getStudentId());
//
//			updateQueueUI();
//		} else {
//			Utils.showAlert(Alert.AlertType.INFORMATION, "No Priority Numbers", "There are no priority numbers to call.", null);
//		}
	}

	@FXML
	private void handleRecallButton() {
		if (currentServingNumber != null) {
			currentNumberText.setText(currentServingNumber.getPriorityNumber());
			currentStudentText.setText(currentServingNumber.getStudentId());
		} else {
			Utils.showAlert(Alert.AlertType.WARNING, "No Active Number", "There is no number currently being served.", null);
		}
	}

	@FXML
	private void handleWindowToggle() {
		boolean isOpen = windowStatusText.getText().equalsIgnoreCase("OPEN");
		windowStatusText.setText(isOpen ? "CLOSED" : "OPEN");
		windowToggleButton.setText(isOpen ? "Open Window" : "Close Window");
	}

	@FXML
	private void logout() {
		SessionManager.logout((Stage) logoutButton.getScene().getWindow());
	}
}