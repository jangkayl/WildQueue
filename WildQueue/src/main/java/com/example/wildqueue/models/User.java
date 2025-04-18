package com.example.wildqueue.models;

public abstract class User {
	protected String userId;
	protected String institutionalId;
	protected String name;
	protected String password;
	protected String userType;

	public abstract void login();
	public abstract void logout();

	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getUserType() { return userType; }
	public void setUserType(String userType) { this.userType = userType; }

	public String getInstitutionalId() { return institutionalId; }
	public void setInstitutionalId(String institutionalId) { this.institutionalId = institutionalId; }
}
