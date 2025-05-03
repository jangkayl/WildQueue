package com.example.wildqueue.services;

import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.models.TellerWindow;
import com.example.wildqueue.utils.managers.TellerWindowManager;
import com.example.wildqueue.utils.managers.TransactionManager;

import java.sql.Timestamp;
import java.util.List;

public class WindowUpdaterService extends AbstractUpdaterService<TellerWindow, Integer> {
	private static final WindowUpdaterService INSTANCE = new WindowUpdaterService();

	private WindowUpdaterService() {}

	public static WindowUpdaterService getInstance() {
		return INSTANCE;
	}

	@Override
	protected List<TellerWindow> fetchUpdatedItems(TellerWindow lastFetched, Timestamp lastModifiedSince) {
		return TellerWindowDAO.getWindowSince(lastModifiedSince);
	}

	@Override
	protected Integer getItemIdentifier(TellerWindow item) {
		return item.getWindowNumber();
	}

	@Override
	protected void onItemsUpdated(List<TellerWindow> updatedItems) {
		updatedItems.forEach(TellerWindowManager::addOrUpdateTellerWindow);
	}
}
