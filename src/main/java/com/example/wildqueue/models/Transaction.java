package com.example.wildqueue.models;

import java.util.Date;

public class Transaction {
	private int transactionId;
	private String priorityNumber;
	private int windowNumber;
	private String studentId;
	private String tellerId;
	private int amount;
	private String transactionType;
	private Date transactionDate;
	private String status;

	public Transaction(int transactionId, String priorityNumber, int windowNumber, String studentId, String tellerId, int amount, String transactionType, Date transactionDate, String status) {
		this.transactionId = transactionId;
		this.priorityNumber = priorityNumber;
		this.windowNumber = windowNumber;
		this.studentId = studentId;
		this.tellerId = tellerId;
		this.amount = amount;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.status = status;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public String getPriorityNumber() {
		return priorityNumber;
	}

	public void setPriorityNumber(String priorityNumber) {
		this.priorityNumber = priorityNumber;
	}

	public int getWindowNumber() {
		return windowNumber;
	}

	public void setWindowNumber(int windowNumber) {
		this.windowNumber = windowNumber;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getTellerId() {
		return tellerId;
	}

	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
