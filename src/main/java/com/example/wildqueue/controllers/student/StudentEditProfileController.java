package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StudentEditProfileController {

    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label nameError;
    @FXML private Label passwordError;
    @FXML private Label confirmError;
    @FXML private Hyperlink profileLink;
    private Runnable onSaveCallback;
    private User currentStudent;
    private StudentMainController mainController;

    public void setCurrentStudent(User student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        this.currentStudent = student;
        initializeFields();
    }

    public void setMainController(StudentMainController mainController) {
        this.mainController = mainController;
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void initializeFields() {
        if (currentStudent != null) {
            nameField.setText(currentStudent.getName());
        }
        profileLink.setOnAction(event -> {
            mainController.navigateToProfile();
        });
    }

    @FXML
    private void handleSave() {
        clearErrorMessages();

        if (currentStudent == null) {
            showAlert("Error", "No student data loaded", "Please restart the application.");
            return;
        }

        boolean isValid = validateInputs();

        if (isValid) {
            try {
                currentStudent.setName(nameField.getText().trim());

                String newPassword = passwordField.getText().trim();
                String hashedPassword = Utils.hashPassword(newPassword);

                if (!newPassword.isEmpty()) {
                    currentStudent.setPassword(hashedPassword);
                }
                UserDAO.updateUser(currentStudent);
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }
                closeWindow();
            } catch (Exception e) {
                showAlert("Error", "Failed to save changes", e.getMessage());
            }
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (nameField.getText().trim().isEmpty()) {
            nameError.setText("Name cannot be empty");
            nameError.setTextFill(Color.RED);
            isValid = false;
        }

        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (!password.isEmpty()) {
            if (password.length() < 6) {
                passwordError.setText("Password must be at least 6 characters");
                passwordError.setTextFill(Color.RED);
                isValid = false;
            }

            if (!password.equals(confirmPassword)) {
                confirmError.setText("Passwords do not match");
                confirmError.setTextFill(Color.RED);
                isValid = false;
            }
        }

        return isValid;
    }

    private void clearErrorMessages() {
        nameError.setText("");
        passwordError.setText("");
        confirmError.setText("");
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}