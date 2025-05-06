package com.example.wildqueue.controllers.teller;

import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.models.TellerWindow;
import com.example.wildqueue.utils.Utils;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.managers.TellerWindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

public class WindowSelectionPopupController {
	@FXML private Button leaveButton;
	@FXML private Button leaveWindowButton;
	@FXML private ImageView imageWindow1;
	@FXML private ImageView imageWindow2;
	@FXML private VBox window1Card;
	@FXML private VBox window2Card;
	@FXML private Text window1Status;
	@FXML private Text window2Status;
	@FXML private Button closeButton;
	@FXML private Button confirmButton;

	private int selectedWindow = 0;
	private Stage stage;
	private boolean confirmed = false;
	private static WindowSelectionPopupController instance;

	public WindowSelectionPopupController() {
		instance = this;
	}

	public static Optional<WindowSelectionPopupController> getInstance() {
		return Optional.ofNullable(instance);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public int getSelectedWindow() {
		return selectedWindow;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	@FXML
	private void selectWindow1() {
		selectedWindow = 1;
		System.out.println("Selected Window: 1");
		showConfirmationButton();
		highlightSelectedWindow(window1Card, window2Card);
	}

	@FXML
	private void selectWindow2() {
		selectedWindow = 2;
		System.out.println("Selected Window: 2");
		showConfirmationButton();
		highlightSelectedWindow(window2Card, window1Card);
	}

	@FXML
	private void confirmSelection() {
		confirmed = true;
		stage.close();
	}

	@FXML
	private void closePopup() {
		confirmed = false;
		stage.close();
	}

	@FXML
	private void leaveWindow() {
		Utils.showAlert(
				Alert.AlertType.WARNING,
				"Leave Window",
				"Are you sure you want to leave the window?",
				ButtonType.OK,
				ButtonType.CANCEL
		).ifPresent(response -> {
			if (response == ButtonType.OK && !TellerWindowManager.hasCurrentTransaction()) {
				TellerWindow currentWindow = Objects.requireNonNull(TellerWindowManager.getTellerCurrentWindow());
				TellerWindowManager.addOrUpdateTellerWindow(currentWindow);
				TellerWindowDAO.removeTeller(currentWindow.getWindowNumber(), new Timestamp(System.currentTimeMillis()));
				stage.close();
			} else if(response == ButtonType.OK && TellerWindowManager.hasCurrentTransaction()){
				Utils.showAlert(
						Alert.AlertType.WARNING,
						"Pending Transaction",
						"Please continue your transaction first!",
						ButtonType.OK
				);
			}
		});
	}

	private void highlightSelectedWindow(VBox selected, VBox unselected) {
		if (!selected.isDisabled()) {
			selected.setStyle("-fx-background-color: #FFD700; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0); -fx-cursor: hand; -fx-padding: 20; -fx-min-width: 200;");
		}
		if (!unselected.isDisabled()) {
			unselected.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0); -fx-cursor: hand; -fx-padding: 20; -fx-min-width: 200;");
		}
	}

	private void showConfirmationButton() {
		confirmButton.setDisable(false);
	}

	public void updateWindowStatus(boolean window1Available, boolean window2Available) {
		boolean hasWindow = TellerWindowManager.getTellerWindowLists().stream()
				.anyMatch(window -> window.getTellerId() != null &&
						window.getTellerId().equals(SessionManager.getCurrentUser().getInstitutionalId()));

		if (hasWindow) {
			window1Card.setDisable(true);
			window2Card.setDisable(true);
			window1Status.setText("Please leave current window first");
			window2Status.setText("Please leave current window first");
			leaveButton.setDisable(false);
			confirmButton.setDisable(true);
			return;
		}

		leaveButton.setDisable(true);
		window1Card.setDisable(false);
		window2Card.setDisable(false);

		if (window1Available) {
			window1Status.setText("Available");
			window1Status.setFill(Color.valueOf("#5cb85c"));
		} else {
			window1Status.setText("Occupied");
			window1Status.setFill(Color.valueOf("#FFFFFF"));
			window1Card.setDisable(true);
			window1Card.setStyle("-fx-background-color: #f19432; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0); -fx-cursor: hand; -fx-padding: 20; -fx-min-width: 200;");
			imageWindow1.setImage(new Image("https://cdn-icons-png.flaticon.com/128/4961/4961600.png"));
		}

		if (window2Available) {
			window2Status.setText("Available");
			window2Status.setFill(Color.valueOf("#5cb85c"));
		} else {
			window2Status.setText("Occupied");
			window2Status.setFill(Color.valueOf("#FFFFFF"));
			window2Card.setDisable(true);
			window2Card.setStyle("-fx-background-color: #f19432; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0); -fx-cursor: hand; -fx-padding: 20; -fx-min-width: 200;");
			imageWindow2.setImage(new Image("https://cdn-icons-png.flaticon.com/128/4961/4961600.png"));
		}

		confirmButton.setDisable(true);
	}


}