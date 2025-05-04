package com.example.wildqueue.controllers.admin;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.*;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.services.TransactionUpdaterService;
import com.example.wildqueue.utils.managers.PriorityNumberManager;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.Utils;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminHomepageController {

	@FXML private TableColumn<Transaction, String> transIdCol;
	@FXML private TableColumn<Transaction, String> transStudentCol;
	@FXML private TableColumn<Transaction, String> transNumberCol;
	@FXML private TableColumn<Transaction, String> transStatusCol;
	@FXML private TableColumn<Transaction, String> transTypeCol;
	@FXML private TableColumn<Transaction, String> transCalledCol;
	@FXML private TableColumn<Transaction, String> transCompletedCol;
	@FXML private TableColumn<PriorityNumber, String> queueNumberCol;
	@FXML private TableColumn<PriorityNumber, String> queueStudentIdCol;
	@FXML private TableColumn<PriorityNumber, String> queueStudentNameCol;
	@FXML private TableColumn<PriorityNumber, String> queueStatusCol;
	@FXML private TableColumn<PriorityNumber, String> queueTimeCol;
	@FXML private TableView<PriorityNumber> queueTable;
	@FXML private TableView<Transaction> transactionTable;
	@FXML private TableView<Teller> tellersTable;
	@FXML private TableView<Student> studentsTable;
	@FXML private ToggleButton onlineRequestsToggle;
	@FXML private TextField searchField;
	@FXML private Button logoutButton;

	@FXML private TableColumn<Student, String> studentIdCol;
	@FXML private TableColumn<Student, String> studentNameCol;
	@FXML private TableColumn<Student, String> studentEmailCol;
	@FXML private TableColumn<Student, Void> studentActionCol;

	@FXML private TableColumn<Teller, String> tellerIdCol;
	@FXML private TableColumn<Teller, String> tellerNameCol;
	@FXML private TableColumn<Teller, String> tellerEmailCol;
	@FXML private TableColumn<Teller, Void> tellerActionCol;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private List<PriorityNumber> priorityQueue;
	private List<Transaction> transactionList;

	@FXML
	void initialize() {
		if (studentsTable != null) {
			studentIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstitutionalId()));
			studentNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
			studentEmailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
		}

		if (tellersTable != null) {
			tellerIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstitutionalId()));
			tellerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
			tellerEmailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
		}

		if (queueTable != null) {
			queueNumberCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getPriorityNumber()));
			queueStudentIdCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getStudentId()));
			queueStudentNameCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getStudentId()));
			queueStatusCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getStatus().toString()));
			queueTimeCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(
							cellData.getValue().getCreatedAt()
									.toLocalDateTime()
									.format(DATE_TIME_FORMATTER)
					)
			);
		}

		if (transactionTable != null) {
			transIdCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(String.valueOf(cellData.getValue().getTransactionId())));
			transNumberCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getPriorityNumber()));
			transStudentCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getStudentName()));
			transTypeCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getTransactionType()));
			transStatusCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(cellData.getValue().getStatus()));
			transCalledCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(
							cellData.getValue().getCalledTime() != null
									? cellData.getValue().getCalledTime()
									.toLocalDateTime()
									.format(DATE_TIME_FORMATTER)
									: ""
					)
			);
			transCompletedCol.setCellValueFactory(cellData ->
					new SimpleStringProperty(
							cellData.getValue().getCompletionDate() != null
									? cellData.getValue().getCalledTime()
									.toLocalDateTime()
									.format(DATE_TIME_FORMATTER)
									: ""
					)
			);
		}

		loadSyncData();
		addActionButtonToTables();
		loadStudentData();
		loadTellerData();
	}

	private void addActionButtonToTables() {
		if (studentActionCol != null) {
			studentActionCol.setCellFactory(param -> new TableCell<>() {
				private final Button changeButton = new Button("Change");
				private final Button deleteButton = new Button("Delete");
				private final HBox buttonsContainer = new HBox(changeButton, deleteButton);

				{
					buttonsContainer.setSpacing(5);
					changeButton.setOnAction(event -> {
						Student student = getTableView().getItems().get(getIndex());
						handleStudentChangeAction(student);
					});
					deleteButton.setOnAction(event -> {
						Student student = getTableView().getItems().get(getIndex());
						handleStudentDeleteAction(student);
					});
				}

				@Override
				protected void updateItem(Void item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setGraphic(null);
					} else {
						setGraphic(buttonsContainer);
					}
				}
			});
		}

		if (tellerActionCol != null) {
			tellerActionCol.setCellFactory(param -> new TableCell<>() {
				private final Button changeButton = new Button("Change");
				private final Button deleteButton = new Button("Delete");
				private final HBox buttonsContainer = new HBox(changeButton, deleteButton);

				{
					buttonsContainer.setSpacing(5);
					changeButton.setOnAction(event -> {
						Teller teller = getTableView().getItems().get(getIndex());
						handleTellerChangeAction(teller);
					});
					deleteButton.setOnAction(event -> {
						Teller teller = getTableView().getItems().get(getIndex());
						handleTellerDeleteAction(teller);
					});
				}

				@Override
				protected void updateItem(Void item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setGraphic(null);
					} else {
						setGraphic(buttonsContainer);
					}
				}
			});
		}
	}

	private void handleStudentChangeAction(Student student) {
		Optional<ButtonType> result = Utils.showAlert(
				Alert.AlertType.CONFIRMATION,
				"Confirmation",
				"Are you sure you want to change this student to a teller?",
				ButtonType.YES,
				ButtonType.NO
		);

		if (result.isPresent() && result.get() == ButtonType.YES) {
			studentsTable.getItems().remove(student);

			Teller newTeller = convertStudentToTeller(student);
			tellersTable.getItems().add(newTeller);
			newTeller.setUserType("TELLER");

			UserDAO.updateUser(newTeller);
		}
	}

	private void handleStudentDeleteAction(Student student) {
		Optional<ButtonType> result = Utils.showAlert(
				Alert.AlertType.CONFIRMATION,
				"Confirmation",
				"Are you sure you want to delete this user?",
				ButtonType.YES,
				ButtonType.NO
		);

		if (result.isPresent() && result.get() == ButtonType.YES) {
			studentsTable.getItems().remove(student);

			UserDAO.deleteUser(student);
		}
	}

	private void handleTellerChangeAction(Teller teller) {
		Optional<ButtonType> result = Utils.showAlert(
				Alert.AlertType.CONFIRMATION,
				"Confirmation",
				"Are you sure you want to change this teller to a student?",
				ButtonType.YES,
				ButtonType.NO
		);

		if (result.isPresent() && result.get() == ButtonType.YES) {
			tellersTable.getItems().remove(teller);

			Student newStudent = convertTellerToStudent(teller);
			studentsTable.getItems().add(newStudent);
			newStudent.setUserType("STUDENT");

			UserDAO.updateUser(newStudent);
		}
	}

	private void handleTellerDeleteAction(Teller teller) {
		Optional<ButtonType> result = Utils.showAlert(
				Alert.AlertType.CONFIRMATION,
				"Confirmation",
				"Are you sure you want to delete this user?",
				ButtonType.YES,
				ButtonType.NO
		);

		if (result.isPresent() && result.get() == ButtonType.YES) {
			tellersTable.getItems().remove(teller);

			UserDAO.deleteUser(teller);
		}
	}

	private Teller convertStudentToTeller(Student student) {
		Teller teller = new Teller(
				student.getInstitutionalId(),
				student.getName(),
				student.getEmail(),
				student.getPassword(),
				UserType.TELLER.toString()
		);

		return teller;
	}

	private Student convertTellerToStudent(Teller teller) {
		Student student = new Student(
				teller.getInstitutionalId(),
				teller.getName(),
				teller.getEmail(),
				teller.getPassword(),
				UserType.TELLER.toString()

		);
		return student;
	}

	private void loadSyncData(){
		transactionList = TransactionDAO.getAllTransactions();
		priorityQueue = PriorityNumberDAO.getAllPriorityNumbers();

		TransactionManager.setTransactionList(transactionList);
		PriorityNumberManager.setPriorityNumberList(priorityQueue);

		initializeTransactions();
		initializePriorityQueue();
	}

	private void initializePriorityQueue(){
		priorityQueue = PriorityNumberManager.getPriorityNumberList();

		PriorityNumber lastFetched = !priorityQueue.isEmpty() ?
				priorityQueue.get(priorityQueue.size() - 1) : null;

		QueueUpdaterService queueUpdaterService = QueueUpdaterService.getInstance();
		queueUpdaterService.setLastFetched(lastFetched);
		queueUpdaterService.subscribe(this::handleQueueUpdates);

		updatePriorityQueueUI();
	}

	private void initializeTransactions() {
		transactionList = TransactionManager.getTransactionList();
		updateTransactionUI();

		Transaction lastFetched = !transactionList.isEmpty() ?
				transactionList.get(transactionList.size() - 1) : null;

		TransactionUpdaterService transactionUpdaterService = TransactionUpdaterService.getInstance();
		transactionUpdaterService.setLastFetched(lastFetched);
		transactionUpdaterService.subscribe(this::handleTransactionUpdates);

		updateTransactionUI();
	}

	public void handleQueueUpdates(List<PriorityNumber> updatedNumbers) {
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
			updatePriorityQueueUI();
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
		}
	}

	private void updatePriorityQueueUI() {
		priorityQueue.sort((pn1, pn2) -> pn2.getPriorityNumber().compareTo(pn1.getPriorityNumber()));

		ObservableList<PriorityNumber> priorityNumbers = FXCollections.observableArrayList(priorityQueue);
		queueTable.setItems(priorityNumbers);
		queueTable.refresh();
	}

	private void updateTransactionUI() {
		transactionList.sort((t1, t2) -> t2.getPriorityNumber().compareTo(t1.getPriorityNumber()));

		ObservableList<Transaction> transactions= FXCollections.observableArrayList(transactionList);
		transactionTable.setItems(transactions);
		transactionTable.refresh();
	}

	private void loadStudentData() {
		List<User> allUsers = getAllUsersFromDatabase();
		List<Student> students = allUsers.stream()
				.filter(user -> user instanceof Student)
				.map(user -> (Student) user)
				.collect(Collectors.toList());

		ObservableList<Student> studentData = FXCollections.observableArrayList(students);
		studentsTable.setItems(studentData);
		studentsTable.refresh();
	}

	private void loadTellerData() {
		List<User> allUsers = getAllUsersFromDatabase();
		List<Teller> tellers = allUsers.stream()
				.filter(user -> user instanceof Teller)
				.map(user -> (Teller) user)
				.collect(Collectors.toList());

		ObservableList<Teller> tellerData = FXCollections.observableArrayList(tellers);
		tellersTable.setItems(tellerData);
		tellersTable.refresh();
	}

	private List<User> getAllUsersFromDatabase() {
		return UserDAO.getAllUsers();
	}

	@FXML
	private void handleEnrollStudent(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/register_student.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Enroll New Student");
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Utils.showAlert(
					Alert.AlertType.ERROR,
					"Error",
					"Failed to open the registration form.",
					ButtonType.OK
			);
		}
	}

	@FXML
	private void handleRegisterTeller(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/register-teller.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Enroll New Student");
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Utils.showAlert(
					Alert.AlertType.ERROR,
					"Error",
					"Failed to open the registration form.",
					ButtonType.OK
			);
		}
	}

	@FXML
	private void toggleOnlineRequests(ActionEvent event) {
		// TODO: Implement toggle logic
	}

	@FXML
	private void logout() {
		if (logoutButton != null) {
			SessionManager.logout((Stage) logoutButton.getScene().getWindow());
		}
	}
}