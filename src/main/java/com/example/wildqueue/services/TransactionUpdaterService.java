package com.example.wildqueue.services;

import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.SessionManager;
import com.example.wildqueue.utils.TransactionManager;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class TransactionUpdaterService extends AbstractUpdaterService<Transaction, String> {
    private static final TransactionUpdaterService INSTANCE = new TransactionUpdaterService();

    private TransactionUpdaterService() {}

    public static TransactionUpdaterService getInstance() {
        return INSTANCE;
    }

    @Override
    protected List<Transaction> fetchUpdatedItems(Transaction lastFetched, Timestamp lastModifiedSince) {
        if (Objects.equals(SessionManager.getCurrentUser().getUserType(), "STUDENT")) {
            return TransactionDAO.getStudentTransactionsSince(
                    SessionManager.getCurrentUser().getInstitutionalId(),
                    lastModifiedSince
            );
        } else {
            return TransactionDAO.getTransactionsSince(
                    lastFetched.getPriorityNumber(),
                    lastModifiedSince
            );
        }
    }

    @Override
    protected String getItemIdentifier(Transaction item) {
        return item.getPriorityNumber();
    }

    @Override
    protected void onItemsUpdated(List<Transaction> updatedItems) {
        updatedItems.forEach(TransactionManager::addOrUpdateTransaction);
    }
}
