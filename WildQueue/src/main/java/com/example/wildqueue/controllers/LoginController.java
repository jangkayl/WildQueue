package com.example.wildqueue.controllers;

import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.SessionManager;
import com.example.wildqueue.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField institutionalIdField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink createAccountLink;

    @FXML
    public void initialize() {
        createAccountLink.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/register-page.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) createAccountLink.getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.setTitle("Register");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleLogin() {
        String institutionalId = institutionalIdField.getText();
        String password = passwordField.getText();

        if (institutionalId.isEmpty() || password.isEmpty()) {
            showError("Username or password cannot be empty.");
            return;
        }

        User user = UserDAO.getUserByInstitutionalId(institutionalId);

        if (user == null) {
            showError("User not found.");
            return;
        }

        String hashedPassword = Utils.hashPassword(password);
        if (hashedPassword != null && hashedPassword.equals(user.getPassword())) {
            System.out.println(user.getName() + " logged in successfully.");
            Utils.showAlert(
                    Alert.AlertType.INFORMATION,
                    "Login Success",
                    "You have successfully logged in " + user.getName() + "!",
                    "/com/example/wildqueue/student-main.fxml",
                    (Stage) loginButton.getScene().getWindow(),
                    "Homepage"
            );
        } else {
            showError("Invalid password.");
        }

        SessionManager.setCurrentUser(user);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
