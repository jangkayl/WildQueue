package com.example.wildqueue.utils.managers;

import com.example.wildqueue.models.TellerWindow;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TellerWindowManager {
	private static List<TellerWindow> tellerWindowLists;

	private TellerWindowManager() {}

	public static void setTellerWindowLists(List<TellerWindow> tellerWindows) {
		System.out.println("Teller Windows are set");
		tellerWindowLists = tellerWindows;
	}

	public static List<TellerWindow> getTellerWindowLists() { return tellerWindowLists; }

	public static TellerWindow getTellerCurrentWindow() {
		for (TellerWindow tellerWindow : tellerWindowLists) {
			if (Objects.equals(tellerWindow.getTellerId(), SessionManager.getCurrentUser().getInstitutionalId())) {
				return tellerWindow;
			}
		}
		return null;
	}

	public static TellerWindow getTellerWindowById(int windowId) {
		for (TellerWindow tellerWindow : tellerWindowLists) {
			if (tellerWindow.getWindowNumber() == windowId) {
				return tellerWindow;
			}
		}
		return null;
	}

	public static boolean hasCurrentTransaction(){
		for (TellerWindow tellerWindow : tellerWindowLists) {
			System.out.println("teller windows " + tellerWindow.getWindowNumber() + " id " + tellerWindow.getTellerId() + " userId " + tellerWindow.getStudentId());
		}
		for (TellerWindow tellerWindow : tellerWindowLists) {
			if (tellerWindow.getStudentId() != null
					&& !tellerWindow.getStudentId().isEmpty()
					&& Objects.equals(tellerWindow.getTellerId(), SessionManager.getCurrentUser().getInstitutionalId())) {
				System.out.println("TRUE");
				return true;
			}
		}
		return false;
	}


	public static void updateTellerWindow(TellerWindow updatedTellerWindow) {
		tellerWindowLists = tellerWindowLists.stream()
				.map(t -> t.getWindowNumber() == updatedTellerWindow.getWindowNumber() ? updatedTellerWindow : t)
				.collect(Collectors.toList());
	}

	public static void addOrUpdateTellerWindow(TellerWindow tellerWindow) {
		Optional<TellerWindow> existing = tellerWindowLists.stream()
				.filter(t -> t.getWindowNumber() == tellerWindow.getWindowNumber())
				.findFirst();

		if (existing.isPresent()) {
			updateTellerWindow(tellerWindow);
		} else {
			tellerWindowLists.add(tellerWindow);
		}
	}
}