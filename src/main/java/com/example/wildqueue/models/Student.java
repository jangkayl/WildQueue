package com.example.wildqueue.models;

public class Student extends User {

	public Student(String institutionalId, String name, String email, String password, String userType) {
		this.institutionalId = institutionalId;
		this.name = name;
		this.email = email;
		this.password = password;
		this.userType = userType;
	}

	@Override
	public void login() {
		System.out.println(name + " has logged in.");
	}

	@Override
	public void logout() {
		System.out.println(name + " has logged out.");
	}
}
