package com.example.wildqueue.utils;

import com.example.wildqueue.models.User;

public class SessionManager {
	private static User currentUser;

	private SessionManager() {} 

	public static void setCurrentUser(User user) {
		currentUser = user;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void clearSession() {
		currentUser = null;
	}

	public static boolean isLoggedIn() {
		return currentUser != null;
	}
}