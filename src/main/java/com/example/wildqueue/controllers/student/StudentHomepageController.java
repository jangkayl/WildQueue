package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.models.User;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.utils.PriorityNumberManager;
import com.example.wildqueue.utils.SessionManager;
import com.example.wildqueue.utils.ThreadUtils;
import com.example.wildqueue.utils.Utils;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudentHomepageController {
    @FXML private ScrollPane scrollPane;
    @FXML private Text window1Number;
    @FXML private Text window2Number;
    @FXML private Text currentTicketNumber;
    @FXML private Text ticketDetailText;
    @FXML private Text txtNumWaiting;
    @FXML private Button getNumberButton;
    @FXML private HBox hbQueue;

    private StudentMainController mainController;
    private final User currentUser = SessionManager.getCurrentUser();
    private List<PriorityNumber> priorityQueue;

	@FXML
    public void initialize() {
        Utils.scrollPaneSetup(scrollPane);

        initializePriorityQueue();
        setupExistingTransactionIfAny();
    }

    private void initializePriorityQueue() {
        priorityQueue = PriorityNumberManager.getPriorityNumberList();
        PriorityNumber lastFetched = !priorityQueue.isEmpty() ?
                priorityQueue.get(priorityQueue.size() - 1) : null;

        QueueUpdaterService queueUpdaterService = QueueUpdaterService.getInstance();

        queueUpdaterService.setLastFetched(lastFetched);
        queueUpdaterService.subscribe(this::handleQueueUpdates);

        updatePriorityQueueUI();
    }

    private void handleQueueUpdates(List<PriorityNumber> updatedNumbers) {
        for (PriorityNumber number : updatedNumbers) {
            boolean alreadyExists = priorityQueue.stream()
                    .anyMatch(pn -> pn.getPriorityNumber().equals(number.getPriorityNumber()));

            if (!alreadyExists) {
                priorityQueue.add(number);
            }

            System.out.println("Updated PriorityNumber:");
            System.out.println("Priority Number: " + number.getPriorityNumber());
            System.out.println("Student ID: " + number.getStudentId());
            System.out.println("Status: " + number.getStatus());
            System.out.println("Created At: " + number.getCreatedAt());
        }
        updatePriorityQueueUI();
    }

    private void setupExistingTransactionIfAny() {
        List<Transaction> transactions = TransactionDAO.getTransactionsByStudentId(currentUser.getInstitutionalId());

        if (!transactions.isEmpty()) {
            Transaction transactionQueue = transactions.get(0);
            setupExistingTransaction(transactionQueue);
        }
    }

    public void setMainController(StudentMainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleGetNumberButton() {
        if (mainController != null) {
            mainController.showTransactionForm();
        }
    }

    public void generatePriorityNumber(String priorityNumber, String purpose, PriorityNumber pn) {
	    Transaction transaction = new Transaction(
                0, priorityNumber, 0, SessionManager.getCurrentUser().getInstitutionalId(),
                null, 0, purpose, new Date(), "Pending"
        );

        PriorityNumberDAO.addPriorityNumber(pn);

        int transactionId = TransactionDAO.createTransaction(transaction);
        if (transactionId > 0) {
            transaction.setTransactionId(transactionId);
            priorityQueue.add(pn);
            currentTicketNumber.setText(priorityNumber);
            ticketDetailText.setText("Purpose: " + purpose + "\nStudent: " + SessionManager.getCurrentUser().getInstitutionalId() +
                    "\nGenerated: " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));

            getNumberButton.setDisable(true);
            getNumberButton.setText("NUMBER ISSUED");
            scrollPane.requestFocus();

            updatePriorityQueueUI();
        }
    }

    private void setupExistingTransaction(Transaction transaction) {
        if (PriorityNumberManager.getPriorityNumberById(currentUser.getInstitutionalId()) != null && "Pending".equalsIgnoreCase(transaction.getStatus())) {
            currentTicketNumber.setText(transaction.getPriorityNumber());
            LocalDateTime localDateTime = transaction.getTransactionDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            String formattedTime = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            ticketDetailText.setText(
                    "Purpose: " + transaction.getTransactionType() + "\n" +
                            "Student: " + transaction.getStudentId() + "\n" +
                            "Generated: " + formattedTime
            );
            getNumberButton.setDisable(true);
            getNumberButton.setText("NUMBER ISSUED");
            scrollPane.requestFocus();
        }
    }

    private void updatePriorityQueueUI() {
        hbQueue.getChildren().clear();
        txtNumWaiting.setText("• " + priorityQueue.size() + " people waiting");

        int maxVisibleItems = 3;
        int totalItems = priorityQueue.size();

        for (int i = 0; i < Math.min(totalItems, maxVisibleItems); i++) {
            addPriorityLabel(priorityQueue.get(i));
        }

        if (totalItems > maxVisibleItems) {
            addEllipsisLabel();
        }
    }

    private void addPriorityLabel(PriorityNumber pn) {
        Label label = new Label(pn.getPriorityNumber());
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 5 9; -fx-background-color: #5E0A15; -fx-background-radius: 20;");
        hbQueue.getChildren().add(label);
    }

    private void addEllipsisLabel() {
        Label ellipsis = new Label("...");
        ellipsis.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 5 9; -fx-background-color: #5E0A15; -fx-background-radius: 20;");
        hbQueue.getChildren().add(ellipsis);
    }
}