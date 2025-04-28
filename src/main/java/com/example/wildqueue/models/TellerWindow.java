package com.example.wildqueue.models;

public class TellerWindow {
	private String tellerId;
	private String studentId;
	private int windowNumber;

	public TellerWindow(String tellerId, String studentId, int windowNumber) {
		this.tellerId = tellerId;
		this.studentId = studentId;
		this.windowNumber = windowNumber;
	}

	public int getWindowNumber() { return windowNumber; }
	public void setWindowNumber(int windowNumber) { this.windowNumber = windowNumber; }

	public String getStudentId() { return studentId; }
	public void setStudentId(String studentId) { this.studentId = studentId; }

	public String getTellerId() { return tellerId; }
	public void setTellerId(String tellerId) { this.tellerId = tellerId; }
}

