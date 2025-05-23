package com.example.wildqueue.models;

import java.sql.Timestamp;
import java.util.Date;

public class Transaction {
	private int transactionId;
	private String priorityNumber;
	private int windowNumber;
	private String studentName;
	private String studentId;
	private String tellerId;
	private double amount;
	private String transactionType;
	private String transactionDetails;
	private Timestamp transactionDate;
	private Timestamp lastModified;
	private String status;
	private Timestamp calledTime;
	private Timestamp completionDate;

	public Transaction(int transactionId, String priorityNumber, int windowNumber, String studentName, String studentId, String tellerId, double amount, String transactionType, String transactionDetails, Timestamp transactionDate, Timestamp lastModified, String status, Timestamp calledTime, Timestamp completionDate) {
		this.transactionId = transactionId;
		this.priorityNumber = priorityNumber;
		this.windowNumber = windowNumber;
		this.studentName = studentName;
		this.studentId = studentId;
		this.tellerId = tellerId;
		this.amount = amount;
		this.transactionType = transactionType;
		this.transactionDetails = transactionDetails;
		this.transactionDate = transactionDate;
		this.lastModified = lastModified;
		this.status = status;
		this.calledTime = calledTime;
		this.completionDate = completionDate;
	}

	public int getTransactionId() { return transactionId; }
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public String getPriorityNumber() { return priorityNumber; }
	public void setPriorityNumber(String priorityNumber) {
		this.priorityNumber = priorityNumber;
	}

	public int getWindowNumber() {
		return windowNumber;
	}
	public void setWindowNumber(int windowNumber) {
		this.windowNumber = windowNumber;
	}

	public String getStudentName() { return studentName; }
	public void setStudentName(String studentName) { this.studentName = studentName; }

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
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionDetails() { return transactionDetails; }
	public void setTransactionDetails(String transactionDetails) { this.transactionDetails = transactionDetails; }

	public Timestamp getTransactionDate() { return transactionDate; }
	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getStatus() { return status; }
	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getLastModified() { return lastModified; }
	public void setLastModified(Timestamp lastModified) { this.lastModified = lastModified; }

	public Timestamp getCalledTime() { return calledTime; }
	public void setCalledTime(Timestamp calledTime) { this.calledTime = calledTime; }

	public Timestamp getCompletionDate() { return completionDate; }
	public void setCompletionDate(Timestamp completionDate) { this.completionDate = completionDate; }

	public void printAllDetails(){
		System.out.println("---------------------------------------");
		System.out.println("Transaction Details:");
		System.out.println("Priority Number: " + getPriorityNumber());
		System.out.println("Window Number: " + getWindowNumber());
		System.out.println("Student ID: " + getStudentId());
		System.out.println("Teller ID: " + getTellerId());
		System.out.println("Amount: " + getAmount());
		System.out.println("Type: " + getTransactionType());
		System.out.println("Date: " + getTransactionDate());
		System.out.println("Status: " + getStatus());
		System.out.println("---------------------------------------");
	}
}
