package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.managers.PriorityNumberManager;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.managers.TellerWindowManager;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.IOException;

public class StudentMainController {
	@FXML private StackPane contentPane;
	@FXML private VBox homeTab;
	@FXML private VBox historyTab;
	@FXML private VBox profileTab;
	@FXML private Text homeText;
	@FXML private Text historyText;
	@FXML private Text profileText;
	private StudentHomepageController homepageController;
	private StudentHistoryController historyController;

	@FXML
	public void initialize() throws IOException {
		PriorityNumberManager.setPriorityNumberList(PriorityNumberDAO.getAllPriorityNumbers());
		TransactionManager.setTransactionList(TransactionDAO.getTransactionsByStudentId(SessionManager.getCurrentUser().getInstitutionalId()));
		TellerWindowManager.setTellerWindowLists(TellerWindowDAO.getAllTellerWindows());

		navigateToHome();
	}

	@FXML
	protected void navigateToHome() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/student-homepage.fxml"));
			AnchorPane homeContent = loader.load();

			homepageController = loader.getController();
			homepageController.setMainController(this);

			homeContent.getProperties().put("controller", homepageController);
			contentPane.getChildren().clear();
			contentPane.getChildren().add(homeContent);

			setActiveTab(homeTab, homeText);
		} catch (IOException e) {
			System.err.println("Error loading home content:");
			e.printStackTrace();
		}
	}


	@FXML
	protected void navigateToHistory() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/student-history-view.fxml"));
			AnchorPane historyContent = loader.load();

			historyController = loader.getController();
			historyController.setMainController(this);

			historyContent.getProperties().put("controller", historyController);
			contentPane.getChildren().clear();
			contentPane.getChildren().add(historyContent);
			setActiveTab(historyTab, historyText);
		} catch (IOException e) {
			System.err.println("Error loading history content:");
			e.printStackTrace();
		}
	}

	@FXML
	private void navigateToProfile() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/student-profile-view.fxml"));
			AnchorPane profileContent = loader.load();
			contentPane.getChildren().clear();
			contentPane.getChildren().add(profileContent);
			setActiveTab(profileTab, profileText);
		} catch (IOException e) {
			System.err.println("Error loading profile content:");
			e.printStackTrace();
		}
	}

	public void showTransactionForm() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/student-details-form.fxml"));
			AnchorPane transactionContent = loader.load();

			StudentDetailsFormController detailsFormController= loader.getController();
			detailsFormController.setMainController(this);

			contentPane.getChildren().clear();
			contentPane.getChildren().add(transactionContent);

		} catch (IOException e) {
			System.err.println("Error loading transaction content:");
			e.printStackTrace();
		}
	}

	public void showTransactionDetails(Transaction transaction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/transaction-detail-page.fxml"));
			AnchorPane detailsPane = loader.load();

			TransactionDetailController transactionController = loader.getController();
			transactionController.setTransaction(transaction);
			transactionController.setMainController(this);

			contentPane.getChildren().setAll(detailsPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public StudentHomepageController getHomepageController() {
		return homepageController;
	}

	private void setActiveTab(VBox activeTab, Text activeText) {
		homeTab.setStyle("-fx-padding: 5; -fx-cursor: hand;");
		historyTab.setStyle("-fx-padding: 5; -fx-cursor: hand;");
		profileTab.setStyle("-fx-padding: 5; -fx-cursor: hand;");

		homeText.setStyle("-fx-font-size: 10px; -fx-fill: #8E8E8E;");
		historyText.setStyle("-fx-font-size: 10px; -fx-fill: #8E8E8E;");
		profileText.setStyle("-fx-font-size: 10px; -fx-fill: #8E8E8E;");

		activeTab.setStyle("-fx-padding: 5; -fx-background-color: #F0E6D2; -fx-cursor: hand;");
		activeText.setStyle("-fx-font-size: 10px; -fx-fill: #5E0A15;");
	}
}