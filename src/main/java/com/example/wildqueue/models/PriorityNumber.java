package com.example.wildqueue.models;

import java.sql.Timestamp;
import java.util.Date;

public class PriorityNumber {
	private String priorityNumber;
	private String studentId;
	private String tellerId;
	private PriorityStatus status;
	private Timestamp createdAt;
	private Timestamp lastModified;

	public PriorityNumber(String priorityNumber, String studentId, String tellerId, PriorityStatus status, Timestamp createdAt, Timestamp lastModified) {
		this.priorityNumber = priorityNumber;
		this.studentId = studentId;
		this.tellerId = tellerId;
		this.status = status;
		this.createdAt = createdAt;
		this.lastModified = lastModified;
	}

	public Timestamp getLastModified() { return lastModified; }
	public void setLastModified(Timestamp lastModified) { this.lastModified = lastModified; }

	public String getPriorityNumber() { return priorityNumber; }
	public void setPriorityNumber(String priorityNumber) { this.priorityNumber = priorityNumber; }

	public String getStudentId() { return studentId; }
	public void setStudentId(String studentId) { this.studentId = studentId; }

	public String getTellerId() {return tellerId; }
	public void setTellerId(String tellerId) { this.tellerId = tellerId; }

	public PriorityStatus getStatus() { return status; }
	public void setStatus(PriorityStatus status) { this.status = status; }

	public Timestamp getCreatedAt() { return createdAt; }
	public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
