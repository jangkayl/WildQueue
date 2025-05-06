package com.example.wildqueue.utils.managers;

import com.example.wildqueue.models.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionManager {
	private static List<Transaction> transactionList;

	private TransactionManager() {}

	public static void setTransactionList(List<Transaction> transactions) {
		System.out.println("Transactions are set");
		transactionList = transactions;
	}

	public static List<Transaction> getTransactionList() { return transactionList; }

	public static Transaction getTransactionById(String studentId) {
		for (Transaction transaction : transactionList) {
			if (transaction.getStudentId().equals(studentId)) {
				return transaction;
			}
		}
		return null;
	}

	public static Transaction getLatestTransaction(){
		return transactionList.get(transactionList.size() - 1);
	}

	public static Transaction getTransactionByPriorityNumber(String priorityNumber){
		for (Transaction transaction : transactionList) {
			if (transaction.getPriorityNumber().equals(priorityNumber)) {
				return transaction;
			}
		}
		return null;
	}

	public static void updateTransaction(Transaction updatedTransaction) {
		transactionList = transactionList.stream()
				.map(t -> t.getTransactionId() == updatedTransaction.getTransactionId() ? updatedTransaction : t)
				.collect(Collectors.toList());
	}

	public static void addOrUpdateTransaction(Transaction transaction) {
		Optional<Transaction> existing = transactionList.stream()
				.filter(t -> t.getTransactionId() == transaction.getTransactionId())
				.findFirst();

		if (existing.isPresent()) {
			updateTransaction(transaction);
		} else {
			transactionList.add(transaction);
		}
	}
}