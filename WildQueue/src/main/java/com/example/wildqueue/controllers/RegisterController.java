package com.example.wildqueue.controllers;

import com.example.wildqueue.CRUD;
import com.example.wildqueue.utils.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RegisterController {

	@FXML private TextField fullNameField;
	@FXML private TextField emailField;
	@FXML private TextField institutionalIdField;
	@FXML private PasswordField passwordField;
	@FXML private PasswordField confirmPasswordField;
	@FXML private Hyperlink loginLink;

	@FXML private Label nameError;
	@FXML private Label emailError;
	@FXML private Label institutionalIdError;
	@FXML private Label passwordError;
	@FXML private Label confirmError;

	public void initialize() {
		loginLink.setOnAction(event -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/login-page.fxml"));
				Parent root = loader.load();

				Stage stage = (Stage) loginLink.getScene().getWindow();

				stage.setScene(new Scene(root));
				stage.setTitle("Register");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	private void handleRegister() {
		clearErrors();

		boolean isValid = true;

		String fullName = fullNameField.getText().trim();
		String email = emailField.getText().trim();
		String institutionalId = institutionalIdField.getText().trim();
		String password = passwordField.getText();
		String confirmPassword = confirmPasswordField.getText();

		if (fullName.isEmpty()) {
			showError(fullNameField, nameError, "Full name is required.");
			isValid = false;
		}

		if (email.isEmpty()) {
			showError(emailField, emailError, "Email is required.");
			isValid = false;
		} else if (!email.matches("^[a-zA-Z0-9._%+-]+@cit\\.edu$")) {
			showError(emailField, emailError, "Must be an institutional email (e.g., name@cit.edu).");
			isValid = false;
		} else if (CRUD.emailExists(email)){
			showError(emailField, emailError, "Institutional email already exists.");
			isValid = false;
		}

		if (institutionalId.isEmpty()) {
			showError(institutionalIdField, institutionalIdError, "Institutional ID is required.");
			isValid = false;
		} else if (CRUD.institutionalIdExists(institutionalId)) {
			showError(institutionalIdField, institutionalIdError, "Institutional ID already exists.");
			isValid = false;
		}

		if (password.isEmpty()) {
			showError(passwordField, passwordError, "Password is required.");
			isValid = false;
		} else if (password.length() < 6) {
			showError(passwordField, passwordError, "Password must be at least 6 characters.");
			isValid = false;
		}

		if (!password.equals(confirmPassword)) {
			showError(passwordField, passwordError, "Passwords do not match.");
			showError(confirmPasswordField, confirmError, "Passwords do not match.");
			isValid = false;
		}

		if (isValid) {
			System.out.println("Full Name: " + fullName);
			System.out.println("Email: " + email);
			System.out.println("Username: " + institutionalId);
			System.out.println("Password: " + password);

			String hashedPassword = Utils.hashPassword(password);
			CRUD.addUser(institutionalId, email, fullName, hashedPassword, "STUDENT");

			Utils.showAlert(
					Alert.AlertType.INFORMATION,
					"Registration Success",
					"You have successfully registered!",
					"/com/example/wildqueue/login-page.fxml",
					(Stage) loginLink.getScene().getWindow(),
					"Login"
			);
		}
	}

	private void showError(Control field, Label errorLabel, String message) {
		field.setStyle("-fx-border-color: #e53935; -fx-border-radius: 5; -fx-border-width: 1;");

		errorLabel.setText(message);
		errorLabel.setOpacity(0);
		errorLabel.setVisible(true);

		Timeline fadeIn = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(errorLabel.opacityProperty(), 0)),
				new KeyFrame(Duration.millis(200), new KeyValue(errorLabel.opacityProperty(), 1))
		);
		fadeIn.play();

		Timeline shake = new Timeline(
				new KeyFrame(Duration.millis(0), new KeyValue(field.translateXProperty(), 0)),
				new KeyFrame(Duration.millis(50), new KeyValue(field.translateXProperty(), 5)),
				new KeyFrame(Duration.millis(100), new KeyValue(field.translateXProperty(), -5)),
				new KeyFrame(Duration.millis(150), new KeyValue(field.translateXProperty(), 5)),
				new KeyFrame(Duration.millis(200), new KeyValue(field.translateXProperty(), 0))
		);
		shake.play();
	}

	private void clearErrors() {
		fullNameField.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-border-width: 1;");
		emailField.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-border-width: 1;");
		institutionalIdField.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-border-width: 1;");
		passwordField.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-border-width: 1;");
		confirmPasswordField.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-border-width: 1;");

		nameError.setVisible(false);
		emailError.setVisible(false);
		institutionalIdError.setVisible(false);
		passwordError.setVisible(false);
		confirmError.setVisible(false);
	}
}