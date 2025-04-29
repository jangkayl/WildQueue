package com.example.wildqueue.utils;

import com.example.wildqueue.models.Transaction;
import java.util.List;

public class TransactionManager {
	private static List<Transaction> transactionList;

	private TransactionManager() {}

	public static void setTransactionList(List<Transaction> transactions) {
		System.out.println("Priority Numbers are set");
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

	public static Transaction getLatestTransaction(){ return transactionList.get(0); }
}