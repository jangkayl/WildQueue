package com.example.oopcapstone;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    public TextField usernameField;
    public PasswordField passwordField;
    public CheckBox rememberMe;
    public Button loginButton;
    public Label errorLabel;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}