package com.example.wildqueue.services;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.utils.Utils;

import java.sql.Timestamp;
import java.util.List;

public class QueueUpdaterService extends AbstractUpdaterService<PriorityNumber, String> {
	private static final QueueUpdaterService INSTANCE = new QueueUpdaterService();

	private QueueUpdaterService() {}

	public static QueueUpdaterService getInstance() {
		return INSTANCE;
	}

	@Override
	protected List<PriorityNumber> fetchUpdatedItems(PriorityNumber lastFetched, Timestamp lastModifiedSince) {
		return PriorityNumberDAO.getPriorityNumbersSince(lastFetched.getPriorityNumber(), lastModifiedSince);
	}

	@Override
	protected String getItemIdentifier(PriorityNumber item) {
		return item.getPriorityNumber();
	}

	@Override
	protected void onItemsUpdated(List<PriorityNumber> updatedItems) {
	}
}
