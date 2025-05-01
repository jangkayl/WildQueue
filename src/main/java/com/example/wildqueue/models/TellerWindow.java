package com.example.wildqueue.models;

import java.sql.Timestamp;

public class TellerWindow {
	private String tellerId;
	private String studentId;
	private int windowNumber;
	private Timestamp createdAt;
	private Timestamp lastModified;

	public TellerWindow(String tellerId, String studentId, int windowNumber, Timestamp createdAt, Timestamp lastModified) {
		this.tellerId = tellerId;
		this.studentId = studentId;
		this.windowNumber = windowNumber;
		this.createdAt = createdAt;
		this.lastModified = lastModified;
	}

	public int getWindowNumber() { return windowNumber; }
	public void setWindowNumber(int windowNumber) { this.windowNumber = windowNumber; }

	public String getStudentId() { return studentId; }
	public void setStudentId(String studentId) { this.studentId = studentId; }

	public String getTellerId() { return tellerId; }
	public void setTellerId(String tellerId) { this.tellerId = tellerId; }

	public Timestamp getCreatedAt() { return createdAt; }
	public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

	public Timestamp getLastModified() { return lastModified; }
	public void setLastModified(Timestamp lastModified) { this.lastModified = lastModified; }
}

