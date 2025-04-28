package com.example.wildqueue.models;

public class Admin extends User {
	public Admin(String institutionalId, String name, String password, String userType) {
		this.institutionalId = institutionalId;
		this.name = name;
		this.password = password;
		this.userType = userType;
	}

	public void getAllPriorityNumber() {}
	public void getAllUser() {}
	public void removeUser() {}
	public void addUser() {}

	@Override
	public void login() {}
	@Override
	public void logout() {}
}

