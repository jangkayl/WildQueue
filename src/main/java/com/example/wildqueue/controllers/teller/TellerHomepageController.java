package com.example.wildqueue.controllers.teller;

import com.example.wildqueue.controllers.teller.components.ActivityLogComponent;
import com.example.wildqueue.controllers.teller.components.QueueDisplayComponent;
import com.example.wildqueue.controllers.teller.components.WindowStatusComponent;
import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.*;
import com.example.wildqueue.utils.*;
import com.example.wildqueue.utils.managers.PriorityNumberManager;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.managers.TellerWindowManager;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class TellerHomepageController {
	@FXML private Label tellerNameLabel;
	@FXML private Button logoutButton;
	@FXML private Text tellerNameSidebar;
	@FXML private Text windowNumberText;
	@FXML private Button windowToggleButton;
	@FXML private Text currentNumberText;
	@FXML private Text currentStudentText;
	@FXML private Text waitingCountText;
	@FXML private Text servedTodayText;
	@FXML private Text windowStatusText;
	@FXML private VBox activityContainer;
	@FXML private HBox hbQueue;

	private User currentUser;
	private List<PriorityNumber> priorityQueue;
	private List<Transaction> transactionList;
	private List<TellerWindow> tellerWindows;
	private PriorityNumber currentServingNumber;
	private Transaction currentServing;
	private int windowNumber;

	@FXML
	public void initialize() {
		loadData();
		loadComponents();
		updateServedTodayText();

		TellerWindow currentWindow = TellerWindowManager.getTellerCurrentWindow();
		if (currentWindow != null) {
			windowNumber = currentWindow.getWindowNumber();
			windowNumberText.setText("Window " + windowNumber);
			windowStatusText.setText("OPEN");
		} else {
			windowNumberText.setText("No Window Assigned");
			windowStatusText.setText("CLOSED");
		}
	}

	private void loadData() {
		currentUser = SessionManager.getCurrentUser();

		if (currentUser != null) {
			tellerNameLabel.setText("Teller: " + currentUser.getName());
			tellerNameSidebar.setText(currentUser.getName());
		} else {
			tellerNameLabel.setText("Guest");
			tellerNameSidebar.setText("Guest");
		}

		priorityQueue = PriorityNumberDAO.getAllPriorityNumbers();
		transactionList = TransactionDAO.getTransactionsByTellerId(currentUser.getInstitutionalId());
		tellerWindows = TellerWindowDAO.getAllTellerWindows();

		PriorityNumberManager.setPriorityNumberList(priorityQueue);
		TransactionManager.setTransactionList(transactionList);
		TellerWindowManager.setTellerWindowLists(tellerWindows);

		currentServingNumber = PriorityNumberManager.getPriorityNumberByTellerId(currentUser.getInstitutionalId());
		if(currentServingNumber != null) {
			currentNumberText.setText(currentServingNumber.getPriorityNumber());
			currentStudentText.setText(currentServingNumber.getStudentId());
		}
	}

	private void loadComponents(){
		QueueDisplayComponent queueDisplayComponent = new QueueDisplayComponent(currentServing, currentServingNumber, currentNumberText, currentStudentText, priorityQueue, this);
		queueDisplayComponent.initializeCurrentServing();
		queueDisplayComponent.initializePriorityQueue();
		priorityQueue = PriorityNumberManager.getPriorityNumberList();

		ActivityLogComponent activityLogComponent = new ActivityLogComponent(transactionList, activityContainer, currentServingNumber, currentNumberText, currentStudentText);
		activityLogComponent.initializeTransactions();
		transactionList = TransactionManager.getTransactionList();

		WindowStatusComponent windowStatusComponent = new WindowStatusComponent(tellerWindows, windowNumberText, windowStatusText);
		windowStatusComponent.initializeTellerWindows();
		tellerWindows = TellerWindowManager.getTellerWindowLists();
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

	public void updateQueueUI() {
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

	@FXML
	private void handleCallButton() {
		if (!hasWindowAssignment()) {
			Utils.showAlert(
					Alert.AlertType.WARNING,
					"No Window Assignment",
					"You must be assigned to a window before calling numbers",
					ButtonType.OK
			);
			return;
		}

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
			TellerWindowDAO.assignStudentToWindow(windowNumber, currentServingNumber.getStudentId(), currentServingNumber.getPriorityNumber());

			TellerWindow newUpdate = TellerWindowManager.getTellerCurrentWindow();
			if(newUpdate != null){
				newUpdate.setPriorityNumber(currentServingNumber.getPriorityNumber());
				newUpdate.setStudentId(currentServingNumber.getStudentId());
				TellerWindowManager.addOrUpdateTellerWindow(newUpdate);
			}

			if (success) {
				currentNumberText.setText(currentServingNumber.getPriorityNumber());
				currentStudentText.setText(currentServingNumber.getStudentId());

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
				TellerWindowDAO.removeStudentFromWindow(windowNumber);

				TellerWindow newUpdate = TellerWindowManager.getTellerCurrentWindow();
				if(newUpdate != null){
					newUpdate.setPriorityNumber("");
					newUpdate.setStudentId("");
					TellerWindowManager.addOrUpdateTellerWindow(newUpdate);
					System.out.println("New update " + newUpdate.getStudentId());
				}

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

			updateTransactionUI();
			updateServedTodayText();
			updateQueueUI();
		} else {
			Utils.showAlert(Alert.AlertType.WARNING, "No Active Number", "There is no number currently being served.", null);
		}
	}

	@FXML
	private void handleWindowToggle() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/window-select-popup.fxml"));
			Parent root = loader.load();

			WindowSelectionPopupController controller = loader.getController();
			boolean window1Available = isWindowAvailable(1);
			boolean window2Available = isWindowAvailable(2);

			controller.updateWindowStatus(window1Available, window2Available);

			Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(windowToggleButton.getScene().getWindow());
			dialog.setTitle("Select Window");
			controller.setStage(dialog);

			Scene scene = new Scene(root);
			dialog.setScene(scene);
			dialog.showAndWait();

			if (controller.isConfirmed()) {
				windowNumber = controller.getSelectedWindow();
				windowNumberText.setText("Window " + windowNumber);
				windowStatusText.setText("OPEN");

				TellerWindowDAO.assignTeller(windowNumber, currentUser.getInstitutionalId());
				TellerWindow currentTellerWindow = TellerWindowManager.getTellerWindowById(windowNumber);

				if(currentTellerWindow != null){
					currentTellerWindow.setTellerId(SessionManager.getCurrentUser().getInstitutionalId());
					TellerWindowManager.addOrUpdateTellerWindow(currentTellerWindow);
				}

				Utils.showAlert(Alert.AlertType.INFORMATION, "Success",
						"Window " + windowNumber + " has been assigned to you", ButtonType.OK);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Utils.showAlert(Alert.AlertType.ERROR, "Error", "Could not load window selection dialog", null);
		}
	}

	private boolean hasWindowAssignment() {
		return tellerWindows.stream()
				.anyMatch(window -> window.getTellerId() != null &&
						window.getTellerId().equals(currentUser.getInstitutionalId()));
	}

	private boolean isWindowAvailable(int windowId) {
		TellerWindow window = TellerWindowManager.getTellerWindowById(windowId);
		return window != null && Objects.equals(window.getTellerId(), "");
	}

	@FXML
	private void logout() {
		SessionManager.logout((Stage) logoutButton.getScene().getWindow());
	}
}