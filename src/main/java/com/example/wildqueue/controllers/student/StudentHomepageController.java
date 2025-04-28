package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
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

import java.sql.Timestamp;
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
    private User currentUser;
    private List<PriorityNumber> priorityQueue;

	@FXML
    public void initialize() {
        Utils.scrollPaneSetup(scrollPane);

        this.currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            System.err.println("Error: No user logged in");
            return;
        }

        initializePriorityQueue();
        setupExistingTransactionIfAny();
    }

    public void initializePriorityQueue() {
        priorityQueue = PriorityNumberManager.getPriorityNumberList();
        updatePriorityQueueUI();

        PriorityNumber lastFetched = !priorityQueue.isEmpty() ?
                priorityQueue.get(priorityQueue.size() - 1) : null;

        QueueUpdaterService queueUpdaterService = QueueUpdaterService.getInstance();
        queueUpdaterService.setLastFetched(lastFetched);
        queueUpdaterService.subscribe(this::handleQueueUpdates);

        updatePriorityQueueUI();
    }

    private void handleQueueUpdates(List<PriorityNumber> updatedNumbers) {
        boolean needsUpdate = false;

        for (PriorityNumber updatedNumber : updatedNumbers) {
            Optional<PriorityNumber> existingNumber = priorityQueue.stream()
                    .filter(pn -> pn.getPriorityNumber().equals(updatedNumber.getPriorityNumber()))
                    .findFirst();

            if (existingNumber.isPresent()) {
                if (!existingNumber.get().getStatus().equals(updatedNumber.getStatus())) {
                    existingNumber.get().setStatus(updatedNumber.getStatus());
                    needsUpdate = true;
                    System.out.println("Status updated for: " + updatedNumber.getPriorityNumber());
                }
            } else {
                priorityQueue.add(updatedNumber);
                needsUpdate = true;
                System.out.println("New number added: " + updatedNumber.getPriorityNumber());
            }
        }

        if (needsUpdate) {
            updatePriorityQueueUI();
        }
    }

    public void setupExistingTransactionIfAny() {
        if(currentUser == null){
            this.currentUser = SessionManager.getCurrentUser();
        }

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
                null, 0, purpose, new Date(), new Timestamp(System.currentTimeMillis()), PriorityStatus.PENDING.toString(), null
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

    public void updatePriorityQueueUI() {
        hbQueue.getChildren().removeIf(node -> {
            if (node instanceof Label label) {
                String labelText = label.getText();
                Optional<PriorityNumber> matchingPN = priorityQueue.stream()
                        .filter(pn -> pn.getPriorityNumber().equals(labelText))
                        .findFirst();

                return matchingPN.isPresent() && !PriorityStatus.PENDING.toString().equalsIgnoreCase(matchingPN.get().getStatus().toString());
            }
            return false;
        });

        hbQueue.getChildren().clear();

        List<PriorityNumber> pendingQueue = priorityQueue.stream()
                .filter(pn -> PriorityStatus.PENDING.toString().equalsIgnoreCase(pn.getStatus().toString()))
                .toList();

        txtNumWaiting.setText("• " + pendingQueue.size() + " people waiting");

        int maxVisibleItems = 3;
        int totalItems = pendingQueue.size();

        for (int i = 0; i < Math.min(totalItems, maxVisibleItems); i++) {
            addPriorityLabel(pendingQueue.get(i));
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