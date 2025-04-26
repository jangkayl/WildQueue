package com.example.wildqueue.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import java.util.Random;

public class StudentHomepageController {
    @FXML private ScrollPane scrollPane;
    @FXML private Text window1Number;
    @FXML private Text window2Number;
    @FXML private Text currentTicketNumber;
    @FXML private Button getNumberButton;

    private int currentQueueNumber = 0;
    private final Random random = new Random();

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);

        scrollPane.getStyleClass().add("edge-to-edge");

        updateWindowNumbers();
    }

    public void generateQueueNumber() {
        currentQueueNumber++;
        currentTicketNumber.setText(String.valueOf(currentQueueNumber));
        updateWindowNumbers();
        getNumberButton.setDisable(true);
        getNumberButton.setText("NUMBER ISSUED");

        scrollPane.requestFocus();
    }

    private void updateWindowNumbers() {
        int baseNumber = Math.max(currentQueueNumber, 140);
        window1Number.setText(String.valueOf(baseNumber - random.nextInt(3)));
        window2Number.setText(String.valueOf(baseNumber - random.nextInt(3)));
    }
}
