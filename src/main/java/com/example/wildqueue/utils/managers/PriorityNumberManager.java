package com.example.wildqueue.utils.managers;

import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;

import java.util.List;
import java.util.Objects;

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

	public static PriorityNumber getPriorityNumberByTellerId(String tellerId){
		for (PriorityNumber priorityNumber : priorityNumberList) {
			if (Objects.equals(priorityNumber.getTellerId(), tellerId) && priorityNumber.getTellerId() != null && priorityNumber.getStatus() == PriorityStatus.PROCESSING) {
				return priorityNumber;
			}
		}
		return null;
	}
}