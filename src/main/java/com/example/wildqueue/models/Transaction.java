package com.example.wildqueue.models;

import java.sql.Timestamp;
import java.util.Date;

public class Transaction {
	private int transactionId;
	private String priorityNumber;
	private int windowNumber;
	private String studentId;
	private String tellerId;
	private double amount;
	private String transactionType;
	private Date transactionDate;
	private Timestamp lastModified;
	private String status;
	private Timestamp completionDate;

	public Transaction(int transactionId, String priorityNumber, int windowNumber, String studentId, String tellerId, double amount, String transactionType, Date transactionDate, Timestamp lastModified, String status, Timestamp completionDate) {
		this.transactionId = transactionId;
		this.priorityNumber = priorityNumber;
		this.windowNumber = windowNumber;
		this.studentId = studentId;
		this.tellerId = tellerId;
		this.amount = amount;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.lastModified = lastModified;
		this.status = status;
		this.completionDate = completionDate;
	}

	public int getTransactionId() { return transactionId; }
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
	public void setTellerId(String tellerId) { this.tellerId = tellerId; }

	public double getAmount() {
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

	public Date getTransactionDate() { return transactionDate; }
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getStatus() { return status; }
	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getLastModified() { return lastModified; }
	public void setLastModified(Timestamp lastModified) { this.lastModified = lastModified; }

	public void setAmount(double amount) { this.amount = amount; }
	public Timestamp getCompletionDate() { return completionDate; }

	public void setCompletionDate(Timestamp completionDate) {
		this.completionDate = completionDate;
	}

	public void printAllDetails(){
		System.out.println("Transaction Submitted:");
		System.out.println("Priority Number: " + getPriorityNumber());
		System.out.println("Window Number: " + getWindowNumber());
		System.out.println("Student ID: " + getStudentId());
		System.out.println("Teller ID: " + getTellerId());
		System.out.println("Amount: " + getAmount());
		System.out.println("Type: " + getTransactionType());
		System.out.println("Date: " + getTransactionDate());
		System.out.println("Status: " + getStatus());
	}
}
