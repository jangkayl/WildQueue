package com.example.wildqueue.controllers.student;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.managers.PriorityNumberManager;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.Timestamp;

public class AlertController {
	@FXML private Button acceptButton;
	@FXML private Button declineButton;
	@FXML private Label lblNumber;
	@FXML private Label lblMessage;

	private Runnable acceptAction;
	private Runnable declineAction;

	public void setAlertText(String number, String message) {
		lblNumber.setText(number);
		lblMessage.setText(message);
	}

	public void setAcceptAction(Runnable action) {
		this.acceptAction = action;
	}

	public void setDeclineAction(Runnable action) {
		this.declineAction = action;
	}

	@FXML
	private void handleAccept() {
		if (acceptAction != null) {
			acceptAction.run();
		}
	}

	@FXML
	private void handleDecline() {
		if (declineAction != null) {
			declineAction.run();
			updateData();
		}
	}

	private void updateData(){
		Transaction latestTransaction = TransactionManager.getLatestTransaction();
		latestTransaction.setStatus(PriorityStatus.CANCELLED.toString());
		latestTransaction.setCompletionDate(new Timestamp(System.currentTimeMillis()));
		latestTransaction.setLastModified(new Timestamp(System.currentTimeMillis()));
		TransactionDAO.updateTransaction(latestTransaction);

		PriorityNumber latestPriorityNumber = PriorityNumberManager.getPriorityNumberList().get(PriorityNumberManager.getPriorityNumberList().size() - 1);
		latestPriorityNumber.setStatus(PriorityStatus.CANCELLED);
		latestPriorityNumber.setLastModified(new Timestamp(System.currentTimeMillis()));
		PriorityNumberDAO.updatePriorityNumber(latestPriorityNumber);

		TellerWindowDAO.removeStudentFromWindow(latestTransaction.getWindowNumber() , new Timestamp(System.currentTimeMillis()));
	}
}