package com.example.wildqueue.controllers.teller;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.models.User;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.services.TransactionUpdaterService;
import com.example.wildqueue.utils.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
	@FXML private VBox vbRecentActivities;
	@FXML private HBox hbQueue;

	private User currentUser;
	private List<PriorityNumber> priorityQueue;
	private List<Transaction> transactionList;
	private PriorityNumber currentServingNumber;
	private Transaction currentServing = null;
	private int windowNumber = 1;

	@FXML
	public void initialize() {
		loadUserData();
		initializeCurrentServing();
		initializePriorityQueue();
		initializeTransactions();
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

	private void initializePriorityQueue() {
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

	private void initializeTransactions() {
		TransactionManager.setTransactionList(TransactionDAO.getTransactionsByTellerId(currentUser.getInstitutionalId()));

		transactionList = TransactionManager.getTransactionList();
		updateTransactionUI();

		Transaction lastFetched = !transactionList.isEmpty() ?
				transactionList.get(transactionList.size() - 1) : null;

		TransactionUpdaterService transactionUpdaterService = TransactionUpdaterService.getInstance();
		transactionUpdaterService.setLastFetched(lastFetched);
		transactionUpdaterService.subscribe(this::handleTransactionUpdates);

		updateTransactionUI();
	}

	private void initializeCurrentServing(){
		if (currentServing != null) {
			return;
		}

		currentServing = TransactionDAO.getCurrentServing(currentUser.getInstitutionalId());
		if (currentServing == null) {
			return;
		}

		currentServingNumber = PriorityNumberDAO.getPriorityNumber(currentServing.getPriorityNumber());
		if (currentServingNumber == null) {
			return;
		}

		System.out.println("Current Serving is " + currentServingNumber.getPriorityNumber());
		currentNumberText.setText(currentServingNumber.getPriorityNumber());
		currentStudentText.setText(currentServingNumber.getStudentId());
	}

	private void updateServedTodayText() {
		LocalDate today = LocalDate.now();

		List<Transaction> completedTransactionsToday = TransactionDAO.getCompletedTransactionsByTeller(currentUser.getInstitutionalId())
				.stream()
				.filter(transaction -> transaction.getCompletionDate() != null)
				.filter(transaction -> transaction.getCompletionDate().toLocalDateTime().toLocalDate().isEqual(today))
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

	private void handleTransactionUpdates(List<Transaction> updatedTransactions) {
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

	private void updateTransactionUI() {
		activityContainer.getChildren().clear();

		List<ActivityEvent> activityEvents = new ArrayList<>();

		for (Transaction transaction : transactionList) {
			if (transaction.getCalledTime() != null) {
				activityEvents.add(new ActivityEvent(
						"CALLED",
						transaction.getPriorityNumber(),
						transaction.getStudentName(),
						transaction.getStudentId(),
						transaction.getCalledTime(),
						Color.GOLD
				));
			}

			if (transaction.getCompletionDate() != null) {
				activityEvents.add(new ActivityEvent(
						"COMPLETED",
						transaction.getPriorityNumber(),
						transaction.getStudentName(),
						transaction.getStudentId(),
						transaction.getCompletionDate(),
						Color.LIMEGREEN
				));
			}

//			if (transaction.getTransferredDate() != null) {
//				activityEvents.add(new ActivityEvent(
//						"TRANSFERRED",
//						transaction.getPriorityNumber(),
//						transaction.getStudentName(),
//						transaction.getStudentId(),
//						transaction.getTransferredDate(),
//						Color.SKYBLUE
//				));
//			}
		}

		List<ActivityEvent> recentActivities = activityEvents.stream()
				.sorted(Comparator.comparing(ActivityEvent::getDate).reversed())
				.limit(5)
				.collect(Collectors.toList());

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
				transaction.setCalledTime(new Timestamp(System.currentTimeMillis()));
				transaction.setStatus(PriorityStatus.PROCESSING.toString());
				TransactionDAO.updateTransaction(transaction);
			}

			boolean success = PriorityNumberDAO.updatePriorityNumberStatus(currentServingNumber.getPriorityNumber() ,PriorityStatus.PROCESSING, currentUser.getInstitutionalId());

			if (success) {
				currentNumberText.setText(currentServingNumber.getPriorityNumber());
				currentStudentText.setText(currentServingNumber.getStudentId());

				refreshTransactionUI();
				updateTransactionUI();

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
			Optional<ButtonType> result = Utils.showAlert(
					Alert.AlertType.CONFIRMATION,
					"Confirmation",
					"Are you sure you are done?",
					ButtonType.YES,
					ButtonType.NO
			);

			if (result.isPresent() && result.get() == ButtonType.YES) {
				System.out.println("User confirmed: DONE!");
			} else {
				System.out.println("User cancelled.");
				return;
			}

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

			refreshTransactionUI();
			updateTransactionUI();

			updateServedTodayText();
			updateQueueUI();
		} else {
			Utils.showAlert(Alert.AlertType.WARNING, "No Active Number", "There is no number currently being served.", null);
		}
	}

	private void refreshTransactionUI(){
		transactionList = TransactionDAO.getTransactionsByTellerId(currentUser.getInstitutionalId());
		TransactionManager.setTransactionList(transactionList);
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