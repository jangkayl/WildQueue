package com.example.wildqueue.controllers.teller.components;

import com.example.wildqueue.controllers.teller.TellerHomepageController;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.utils.managers.PriorityNumberManager;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.managers.TransactionManager;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class QueueDisplayComponent {
	private Transaction currentServing = null;
	private PriorityNumber currentServingNumber;
	private final Text currentNumberText;
	private final Text currentStudentText;
	private List<PriorityNumber> priorityQueue;

	private final TellerHomepageController tellerHomepageController;

	public QueueDisplayComponent(Transaction currentServing, PriorityNumber currentServingNumber, Text currentNumberText, Text currentStudentText, List<PriorityNumber> priorityQueue, TellerHomepageController tellerHomepageController) {
		this.currentServing = currentServing;
		this.currentServingNumber = currentServingNumber;
		this.currentNumberText = currentNumberText;
		this.currentStudentText = currentStudentText;
		this.priorityQueue = priorityQueue;
		this.tellerHomepageController = tellerHomepageController;
	}

	public void initializeCurrentServing(){
		if (currentServing != null) {
			return;
		}

		currentServing = TransactionManager.getTransactionById(SessionManager.getCurrentUser().getInstitutionalId());
		if (currentServing == null) {
			return;
		}

		currentServingNumber = PriorityNumberManager.getPriorityNumberById(currentServing.getPriorityNumber());
		if (currentServingNumber == null) {
			return;
		}

		System.out.println("Current Serving is " + currentServingNumber.getPriorityNumber());
		currentNumberText.setText(currentServingNumber.getPriorityNumber());
		currentStudentText.setText(currentServingNumber.getStudentId());
	}

	public void initializePriorityQueue() {
		priorityQueue = PriorityNumberManager.getPriorityNumberList();
		tellerHomepageController.updateQueueUI();

		PriorityNumber lastFetched = !priorityQueue.isEmpty() ?
				priorityQueue.get(priorityQueue.size() - 1) : null;

		QueueUpdaterService queueUpdaterService = QueueUpdaterService.getInstance();
		queueUpdaterService.setLastFetched(lastFetched);
		queueUpdaterService.subscribe(this::handleQueueUpdates);

		tellerHomepageController.updateQueueUI();
	}

	public void handleQueueUpdates(List<PriorityNumber> updatedNumbers) {
		boolean needsUpdate = false;

		for (PriorityNumber updatedNumber : updatedNumbers) {
			Optional<PriorityNumber> existingNumber = priorityQueue.stream()
					.filter(pn -> pn.getPriorityNumber().equals(updatedNumber.getPriorityNumber()))
					.findFirst();

			if (existingNumber.isPresent()) {
				if (!existingNumber.get().getStatus().equals(updatedNumber.getStatus())) {
					existingNumber.get().setStatus(updatedNumber.getStatus());
					needsUpdate = true;
				}
			} else {
				priorityQueue.add(updatedNumber);
				needsUpdate = true;
			}
		}

		if (needsUpdate) {
			tellerHomepageController.updateQueueUI();
		}
	}

}
