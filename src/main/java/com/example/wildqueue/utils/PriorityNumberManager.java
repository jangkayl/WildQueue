package com.example.wildqueue.utils;

import com.example.wildqueue.models.PriorityNumber;
import java.util.List;

public class PriorityNumberManager {
	private static List<PriorityNumber> priorityNumberList;

	private PriorityNumberManager() {}

	public static void setPriorityNumberList(List<PriorityNumber> priorityNumbers) {
		System.out.println("Priority Numbers are set");
		priorityNumberList = priorityNumbers;
	}

	public static List<PriorityNumber> getPriorityNumberList() { return priorityNumberList; }

	public static PriorityNumber getPriorityNumberById(String studentId) {
		for (PriorityNumber priorityNumber : priorityNumberList) {
			if (priorityNumber.getStudentId().equals(studentId)) {
				return priorityNumber;
			}
		}
		return null;
	}

	public static PriorityNumber getLatestPriorityNumber(){ return priorityNumberList.get(0); }
}