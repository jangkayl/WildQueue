package com.example.wildqueue.models;

import java.util.Date;

public class PriorityNumber {
	private String priorityNumber;
	private String studentId;
	private PriorityStatus status;
	private Date createdAt;

	public PriorityNumber(String priorityNumber, String studentId, PriorityStatus status, Date createdAt) {
		this.priorityNumber = priorityNumber;
		this.studentId = studentId;
		this.status = status;
		this.createdAt = createdAt;
	}

	public String getPriorityNumber() { return priorityNumber; }

	public void setPriorityNumber(String priorityNumber) { this.priorityNumber = priorityNumber; }

	public String getStudentId() { return studentId; }

	public void setStudentId(String studentId) { this.studentId = studentId; }

	public PriorityStatus getStatus() { return status; }

	public void setStatus(PriorityStatus status) { this.status = status; }

	public Date getCreatedAt() { return createdAt; }

	public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
