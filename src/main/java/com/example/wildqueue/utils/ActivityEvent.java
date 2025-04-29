package com.example.wildqueue.utils;

import javafx.scene.paint.Color;

import java.util.Date;

public class ActivityEvent {
	private final String action;
	private final String priorityNumber;
	private final String studentName;
	private final String studentId;
	private final Date date;
	private final Color color;

	public ActivityEvent(String action, String priorityNumber, String studentName,
	                     String studentId, Date date, Color color) {
		this.action = action;
		this.priorityNumber = priorityNumber;
		this.studentName = studentName;
		this.studentId = studentId;
		this.date = date;
		this.color = color;
	}

	// Getters
	public String getAction() { return action; }
	public String getPriorityNumber() { return priorityNumber; }
	public String getStudentName() { return studentName; }
	public String getStudentId() { return studentId; }
	public Date getDate() { return date; }
	public Color getColor() { return color; }
}
