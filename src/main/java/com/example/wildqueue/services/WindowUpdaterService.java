package com.example.wildqueue.services;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.TellerWindow;
import com.example.wildqueue.utils.Utils;

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
	}
}
