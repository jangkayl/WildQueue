package com.example.wildqueue.controllers.admin;

import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.Student;
import com.example.wildqueue.models.Teller;
import com.example.wildqueue.models.User;
import com.example.wildqueue.models.UserType;
import com.example.wildqueue.utils.SessionManager;
import com.example.wildqueue.utils.Utils;
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
import java.util.List;
import java.util.Optional;
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
				private final Button changeButton = new Button("Change");
				private final Button deleteButton = new Button("Delete");
				private final HBox buttonsContainer = new HBox(changeButton, deleteButton);

				{
					buttonsContainer.setSpacing(5); // Add some spacing between buttons
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
					buttonsContainer.setSpacing(5); // Add some spacing between buttons
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
			// Remove from students table
			studentsTable.getItems().remove(student);

			// Convert to teller and add to tellers table
			Teller newTeller = convertStudentToTeller(student);
			tellersTable.getItems().add(newTeller);
			newTeller.setUserType("TELLER");

			// Update in database
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
			// Remove from UI
			studentsTable.getItems().remove(student);

			// Delete from database
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
			// Remove from tellers table
			tellersTable.getItems().remove(teller);

			// Convert to student and add to students table
			Student newStudent = convertTellerToStudent(teller);
			studentsTable.getItems().add(newStudent);
			newStudent.setUserType("STUDENT");

			// Update in database
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
			// Remove from UI
			tellersTable.getItems().remove(teller);

			// Delete from database
			UserDAO.deleteUser(teller);
		}
	}

	// Helper methods for conversion
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

	private void loadStudentData() {
		List<User> allUsers = getAllUsersFromDatabase();
		List<Student> students = allUsers.stream()
				.filter(user -> user instanceof Student)
				.map(user -> (Student) user)
				.collect(Collectors.toList());

		ObservableList<Student> studentData = FXCollections.observableArrayList(students);
		studentsTable.setItems(studentData);
		studentsTable.refresh();  // Force table refresh
	}

	private void loadTellerData() {
		List<User> allUsers = getAllUsersFromDatabase();
		List<Teller> tellers = allUsers.stream()
				.filter(user -> user instanceof Teller)
				.map(user -> (Teller) user)
				.collect(Collectors.toList());

		ObservableList<Teller> tellerData = FXCollections.observableArrayList(tellers);
		tellersTable.setItems(tellerData);
		tellersTable.refresh();  // Force table refresh
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