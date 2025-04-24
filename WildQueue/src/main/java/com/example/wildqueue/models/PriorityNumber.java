package com.example.wildqueue.models;

public class PriorityNumber {
	private String priorityNumber;
	private String studentId;

	public PriorityNumber(String priorityNumber, String studentId) {
		this.priorityNumber = priorityNumber;
		this.studentId = studentId;
	}

	public String getStudentId() { return studentId; }

	public void setStudentId(String studentId) { this.studentId = studentId; }

	public PriorityNumber(String priorityNumber) {
		this.priorityNumber = priorityNumber;
	}

	public String getPriorityNumber() {
		return priorityNumber;
	}

	public void setPriorityNumber(String priorityNumber) {
		this.priorityNumber = priorityNumber;
	}
}
