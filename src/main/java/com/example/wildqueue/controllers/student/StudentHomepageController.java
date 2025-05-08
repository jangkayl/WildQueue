package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.*;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.services.TransactionUpdaterService;
import com.example.wildqueue.services.WindowUpdaterService;
import com.example.wildqueue.utils.*;
import com.example.wildqueue.utils.managers.PriorityNumberManager;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.managers.TellerWindowManager;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudentHomepageController {
    public Text window1Status;
    public Text window2Status;
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
    private List<Transaction> transactionList;
    private List<TellerWindow> tellerWindows;

	@FXML
    public void initialize() {
        Utils.scrollPaneSetup(scrollPane);

        currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            System.err.println("Error: No user logged in");
            return;
        }

        initializePriorityQueue();
        initializeTransactions();
        setupExistingTransactionIfAny();
        initializeWindow();
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

    private void initializeTransactions() {
        transactionList = TransactionManager.getTransactionList();

        Transaction lastFetched = !transactionList.isEmpty() ?
                transactionList.get(transactionList.size() - 1) : null;

        TransactionUpdaterService transactionUpdaterService = TransactionUpdaterService.getInstance();
        transactionUpdaterService.setLastFetched(lastFetched);
        transactionUpdaterService.subscribe(this::handleTransactionUpdates);

        updateTransactionUI();
    }

    private void initializeWindow(){
        tellerWindows = TellerWindowManager.getTellerWindowLists();

        TellerWindow lastFetched = !tellerWindows.isEmpty() ?
                tellerWindows.get(tellerWindows.size() - 1) : null;

        WindowUpdaterService windowUpdaterService = WindowUpdaterService.getInstance();
        windowUpdaterService.setLastFetched(lastFetched);
        windowUpdaterService.subscribe(this::handleTellerWindowUpdated);

        updateTellerWindowUI();
    }

    private void handleTellerWindowUpdated(List<TellerWindow> updatedWindows) {
        boolean needsUpdate = false;

            for (TellerWindow updatedWindow : updatedWindows) {
                Optional<TellerWindow> existingWindow = tellerWindows.stream()
                        .filter(window -> window.getWindowNumber() == updatedWindow.getWindowNumber())
                        .findFirst();

                if (existingWindow.isPresent()) {
                    if (!Objects.equals(existingWindow.get().getTellerId(), updatedWindow.getTellerId()) ||
                            !Objects.equals(existingWindow.get().getStudentId(), updatedWindow.getStudentId()) ||
                            !Objects.equals(existingWindow.get().getPriorityNumber(), updatedWindow.getPriorityNumber())) {

                        existingWindow.get().setTellerId(updatedWindow.getTellerId());
                        existingWindow.get().setStudentId(updatedWindow.getStudentId());
                        existingWindow.get().setPriorityNumber(updatedWindow.getPriorityNumber());

                        needsUpdate = true;
                    }
                } else {
                    tellerWindows.add(updatedWindow);
                    TellerWindowManager.addOrUpdateTellerWindow(updatedWindow);
                    needsUpdate = true;
                }
            }

        if (needsUpdate) {
            updateTellerWindowUI();
        }
    }

    private void handleTransactionUpdates(List<Transaction> updatedTransactions) {
        boolean needsUpdate = false;

        for (Transaction updatedTransaction : updatedTransactions) {
            Optional<Transaction> existingTransaction = transactionList.stream()
                    .filter(t -> Objects.equals(t.getPriorityNumber(), updatedTransaction.getPriorityNumber()))
                    .findFirst();

            if (existingTransaction.isPresent()) {
                if (!Objects.equals(existingTransaction.get().getStatus(), updatedTransaction.getStatus()) ||
                        !Objects.equals(existingTransaction.get().getCalledTime(), updatedTransaction.getCalledTime()) ||
                        !Objects.equals(existingTransaction.get().getCompletionDate(), updatedTransaction.getCompletionDate())) {

                    existingTransaction.get().setStatus(updatedTransaction.getStatus());
                    existingTransaction.get().setCalledTime(updatedTransaction.getCalledTime());
                    existingTransaction.get().setCompletionDate(updatedTransaction.getCompletionDate());
                    needsUpdate = true;

                    if (PriorityStatus.PROCESSING.toString().equals(updatedTransaction.getStatus())) {
                        Platform.runLater(() -> {
                            showCalledNumberAlert(
                                    updatedTransaction.getPriorityNumber(),
                                    String.valueOf(updatedTransaction.getWindowNumber())
                            );
                        });
                    }
                }
            } else {
                TransactionManager.addOrUpdateTransaction(updatedTransaction);
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            updateTransactionUI();
        }
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

    private void setupExistingTransactionIfAny() {
        if(currentUser == null){
            this.currentUser = SessionManager.getCurrentUser();
        }

        transactionList = TransactionManager.getTransactionList();

        if (!transactionList.isEmpty()) {
            Transaction transactionQueue = transactionList.get(0);
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

    public void generatePriorityNumber(String priorityNumber, String purpose, String additionalDetails, PriorityNumber pn, double amount) {
	    Transaction transaction = new Transaction(
                0, priorityNumber, 0, SessionManager.getCurrentUser().getName(), SessionManager.getCurrentUser().getInstitutionalId(),
                null, amount, purpose, additionalDetails, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                PriorityStatus.PENDING.toString(), null, null
        );

        PriorityNumberDAO.addPriorityNumber(pn);

        int transactionId = TransactionDAO.createTransaction(transaction);
        if (transactionId > 0) {
            transaction.setTransactionId(transactionId);
            priorityQueue.add(pn);
            transactionList.add(transaction);

            updatePriorityQueueUI();
            updateTransactionUI();
        }
    }

    private void setupExistingTransaction(Transaction transaction) {
        if (PriorityNumberManager.getPriorityNumberById(currentUser.getInstitutionalId()) != null && "Pending".equalsIgnoreCase(transaction.getStatus())) {
            updateTransactionUI();
        }
    }

    public void updateTellerWindowUI() {
        for (TellerWindow window : tellerWindows) {
            int windowNumber = window.getWindowNumber();
            String tellerId = window.getTellerId();
            String studentId = window.getStudentId();
            String priorityNumber = window.getPriorityNumber();

            String statusText;
            if (tellerId == null || tellerId.isEmpty()) {
                statusText = "CLOSED";
            } else if (studentId == null || studentId.isEmpty()) {
                statusText = "VACANT";
            } else {
                statusText = "OCCUPIED";
            }

            String numberText = (priorityNumber != null && !priorityNumber.isEmpty()) ? priorityNumber : "-";

            if (windowNumber == 1) {
                window1Status.setText(statusText);
                window1Number.setText(numberText);
            } else if (windowNumber == 2) {
                window2Status.setText(statusText);
                window2Number.setText(numberText);
            }
        }
    }

    private void updateTransactionUI() {
        if (transactionList.isEmpty()) {
            currentTicketNumber.setText("--");
            ticketDetailText.setText("No current ticket");
            getNumberButton.setDisable(false);
            getNumberButton.setText("GET NUMBER");
            return;
        }

        Transaction latestTransaction = TransactionManager.getLatestTransaction();

        System.out.println("Latest Transaction " + latestTransaction.getPriorityNumber());

        if (PriorityStatus.COMPLETED.toString().equals(latestTransaction.getStatus()) ||
                PriorityStatus.CANCELLED.toString().equals(latestTransaction.getStatus())) {
            currentTicketNumber.setText("--");
            ticketDetailText.setText("");
            ticketDetailText.setText("No current ticket");
            getNumberButton.setDisable(false);
            getNumberButton.setText("GET NUMBER");
            return;
        }

        currentTicketNumber.setText(latestTransaction.getPriorityNumber());

        LocalDateTime localDateTime = latestTransaction.getTransactionDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String formattedTime = localDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));

        StringBuilder details = new StringBuilder();
        details.append("Purpose: ").append(latestTransaction.getTransactionType()).append("\n")
                .append("Student: ").append(latestTransaction.getStudentId()).append("\n")
                .append("Generated: ").append(formattedTime).append("\n");

        if (PriorityStatus.PROCESSING.toString().equalsIgnoreCase(latestTransaction.getStatus())) {
            details.append("Status: Being served at window ").append(latestTransaction.getWindowNumber());
        } else if(PriorityStatus.PENDING.toString().equalsIgnoreCase(latestTransaction.getStatus())){
            details.append("Status: Waiting in queue");
        }

        String newDetails = details.toString();
        if (!ticketDetailText.getText().equals(newDetails)) {
            ticketDetailText.setText(newDetails);
        }
        getNumberButton.setDisable(true);
        getNumberButton.setText("NUMBER ISSUED");
        scrollPane.requestFocus();
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

    private void showCalledNumberAlert(String priorityNumber, String windowNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/wildqueue/alert.fxml"));
            Parent root = loader.load();

            AlertController alertController = loader.getController();
            alertController.setAlertText(priorityNumber, "Is now called in Window " + windowNumber);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.setScene(new Scene(root));
            dialog.getScene().setFill(Color.TRANSPARENT);

            if (mainController != null && mainController.getPrimaryStage() != null) {
                dialog.initOwner(mainController.getPrimaryStage());
            }

            dialog.show();

            alertController.setAcceptAction(dialog::close);

            alertController.setDeclineAction(dialog::close);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}