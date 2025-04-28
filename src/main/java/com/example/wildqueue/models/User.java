package com.example.wildqueue.models;

import java.io.Serializable;

public abstract class User implements Serializable {
	protected String userId;
	protected String institutionalId;
	protected String name;
	protected String password;
	protected String email;
	protected String userType;

	public abstract void login();
	public abstract void logout();

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

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
