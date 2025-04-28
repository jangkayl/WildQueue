package com.example.wildqueue.controllers.admin;

import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.Student;
import com.example.wildqueue.models.Teller;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class AdminHomepageController {

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

		addActionButtonToTables();
		loadStudentData();
		loadTellerData();
	}

	private void addActionButtonToTables() {
		if (studentActionCol != null) {
			studentActionCol.setCellFactory(param -> new TableCell<>() {
				private final Button actionButton = new Button("Actions");

				{
					actionButton.setOnAction(event -> {
						Student student = getTableView().getItems().get(getIndex());
						handleStudentAction(student);
					});
				}

				@Override
				protected void updateItem(Void item, boolean empty) {
					super.updateItem(item, empty);
					setGraphic(empty ? null : actionButton);
				}
			});
		}

		if (tellerActionCol != null) {
			tellerActionCol.setCellFactory(param -> new TableCell<>() {
				private final Button actionButton = new Button("Actions");

				{
					actionButton.setOnAction(event -> {
						Teller teller = getTableView().getItems().get(getIndex());
						handleTellerAction(teller);
					});
				}

				@Override
				protected void updateItem(Void item, boolean empty) {
					super.updateItem(item, empty);
					setGraphic(empty ? null : actionButton);
				}
			});
		}
	}

	private void handleStudentAction(Student student) {
		System.out.println("Action performed on student: " + student.getName());
		// Add more detailed student handling logic here
	}

	private void handleTellerAction(Teller teller) {
		System.out.println("Action performed on teller: " + teller.getName());
		// Add more detailed teller handling logic here
	}

	private void loadStudentData() {
		ObservableList<Student> studentData = FXCollections.observableArrayList();
		List<User> allUsers = getAllUsersFromDatabase();
		List<Student> students = allUsers.stream()
				.filter(user -> user instanceof Student)
				.map(user -> (Student) user)
				.collect(Collectors.toList());
		studentData.addAll(students);
		if (studentsTable != null) {
			studentsTable.setItems(studentData);
		}
	}

	private void loadTellerData() {
		ObservableList<Teller> tellerData = FXCollections.observableArrayList();
		List<User> allUsers = getAllUsersFromDatabase();
		List<Teller> tellers = allUsers.stream()
				.filter(user -> user instanceof Teller)
				.map(user -> (Teller) user)
				.collect(Collectors.toList());
		tellerData.addAll(tellers);
		if (tellersTable != null) {
			tellersTable.setItems(tellerData);
		}
	}

	private List<User> getAllUsersFromDatabase() {
		return UserDAO.getAllUsers();
	}

	@FXML
	private void handleEnrollStudent(ActionEvent event) {
		// TODO: Implement enrollment logic
	}

	@FXML
	private void handleRegisterTeller(ActionEvent event) {
		// TODO: Implement teller registration logic
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