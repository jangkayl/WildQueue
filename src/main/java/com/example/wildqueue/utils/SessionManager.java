package com.example.wildqueue.utils;

import com.example.wildqueue.models.User;

public class SessionManager {
	private static User currentUser;

	private SessionManager() {} 

	public static void setCurrentUser(User user) {
		Serialize.serialize(user,"user.ser");
		currentUser = user;
	}

	public static User getCurrentUser() { return currentUser; }

	public static void clearSession() {
		Serialize.serialize(null, "user.ser");
		currentUser = null;
	}
}