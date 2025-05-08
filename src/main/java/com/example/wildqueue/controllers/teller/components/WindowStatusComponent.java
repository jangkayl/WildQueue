package com.example.wildqueue.controllers.teller.components;

import com.example.wildqueue.controllers.teller.TellerHomepageController;
import com.example.wildqueue.controllers.teller.WindowSelectionPopupController;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.TellerWindow;
import com.example.wildqueue.services.WindowUpdaterService;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.managers.TellerWindowManager;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WindowStatusComponent {
	private List<TellerWindow> tellerWindows;
	private final Text windowNumberText;;
	private final Text windowStatusText;
	private final Text currentNumberText;
	private final Text currentStudentText;

	public WindowStatusComponent(List<TellerWindow> tellerWindows, Text windowNumberText, Text windowStatusText, Text currentNumberText, Text currentStudentText) {
		this.tellerWindows = tellerWindows;
		this.windowNumberText = windowNumberText;
		this.windowStatusText = windowStatusText;
		this.currentNumberText = currentNumberText;
		this.currentStudentText = currentStudentText;
	}

	public void initializeTellerWindows() {
		tellerWindows = TellerWindowManager.getTellerWindowLists();

		TellerWindow lastFetched = !tellerWindows.isEmpty() ?
				tellerWindows.get(tellerWindows.size() - 1) : null;

		WindowUpdaterService windowUpdaterService = WindowUpdaterService.getInstance();
		windowUpdaterService.setLastFetched(lastFetched);
		windowUpdaterService.subscribe(this::handleTellerWindowUpdated);

		updateTellerWindowUI();
	}

	private void updateTellerWindowUI() {
		boolean windowAssigned = false;

		for (TellerWindow window : tellerWindows) {
			if (window.getTellerId() != null &&
					window.getTellerId().equals(SessionManager.getCurrentUser().getInstitutionalId())) {

				windowNumberText.setText("Window " + window.getWindowNumber());
				windowStatusText.setText("OPEN");

				if (window.getStudentId() == null || window.getStudentId().isEmpty()) {
					currentNumberText.setText("--");
					currentStudentText.setText("No student currently");
					TellerHomepageController.currentServingNumber = null;
				} else {
					currentNumberText.setText(window.getPriorityNumber() != null ?
							window.getPriorityNumber() : "--");
					currentStudentText.setText(window.getStudentId());
				}

				windowAssigned = true;
				break;
			}
		}

		if (!windowAssigned) {
			windowNumberText.setText("No Window Assigned");
			windowStatusText.setText("CLOSED");
			currentNumberText.setText("--");
			currentStudentText.setText("No student currently");
			TellerHomepageController.currentServingNumber = null;
		}
	}

	private void handleTellerWindowUpdated(List<TellerWindow> updatedWindows) {
		boolean needsUpdate = false;

		for (TellerWindow updatedWindow : updatedWindows) {
			Optional<TellerWindow> existingWindow = tellerWindows.stream()
					.filter(window -> window.getWindowNumber() == updatedWindow.getWindowNumber())
					.findFirst();

			if (existingWindow.isPresent()) {
				if (!Objects.equals(existingWindow.get().getTellerId(), updatedWindow.getTellerId()) ||
						!Objects.equals(existingWindow.get().getStudentId(), updatedWindow.getStudentId())) {
					existingWindow.get().setTellerId(updatedWindow.getTellerId());
					existingWindow.get().setStudentId(updatedWindow.getStudentId());
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
			WindowSelectionPopupController.getInstance()
					.ifPresent(controller -> controller.updateWindowStatus(
							Objects.equals(tellerWindows.get(0).getTellerId(), ""),
							Objects.equals(tellerWindows.get(1).getTellerId(), "")
					));		}
	}
}
