package com.example.wildqueue.models;

public class Teller extends User {
	public Teller(String institutionalId, String name, String password, String userType) {
		this.institutionalId = institutionalId;
		this.name = name;
		this.password = password;
		this.userType = userType;
	}

	public void updatePriorityNumber() {}
	public void getPriorityNumber() {}
	public void confirmTransaction() {}
	public void requestTimeout() {}

	@Override
	public void login() {
		System.out.println(name + " has logged in.");
	}

	@Override
	public void logout() {
		System.out.println(name + " has logged out.");
	}
}

