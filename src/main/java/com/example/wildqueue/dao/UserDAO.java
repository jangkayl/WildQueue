package com.example.wildqueue.dao;

import com.example.wildqueue.models.*;
import com.example.wildqueue.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDAO {
	private static final String TABLE_NAME = "users";

	public static void createUserTable() {
		String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				"userId BIGINT NOT NULL AUTO_INCREMENT, " +
				"institutionalId VARCHAR(50) NOT NULL, " +
				"email VARCHAR(100) NOT NULL, " +
				"name VARCHAR(100) NOT NULL, " +
				"password VARCHAR(100) NOT NULL, " +
				"userType VARCHAR(20) NOT NULL, " +
				"PRIMARY KEY (userId)) AUTO_INCREMENT=1000";

		try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.execute(query);
			System.out.println("USER TABLE CREATED SUCCESSFULLY");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean institutionalIdExists(String institutionalId) {
		String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE institutionalId = ?";
		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, institutionalId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean emailExists(String email) {
		String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE email = ?";
		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void addUser(String institutionalId, String email, String name, String hashedPassword, String userType) {
		String query = "INSERT INTO " + TABLE_NAME + " (institutionalId, email, name, password, userType) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, institutionalId);
			stmt.setString(2, email);
			stmt.setString(3, name);
			stmt.setString(4, hashedPassword);
			stmt.setString(5, userType);

			int rowsAffected = stmt.executeUpdate();
			System.out.println(rowsAffected > 0 ? "User added successfully" : "Failed to add user");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static User getUserByInstitutionalId(String institutionalId) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE institutionalId = ?";
		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, institutionalId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String userType = rs.getString("userType");

				if(Objects.equals(userType, UserType.STUDENT.toString())){
					return new Student(
							rs.getString("institutionalId"),
							rs.getString("name"),
							rs.getString("email"),
							rs.getString("password"),
							rs.getString("userType")
					);
				} else if(Objects.equals(userType, UserType.TELLER.toString())){
					return new Teller(
							rs.getString("institutionalId"),
							rs.getString("name"),
							rs.getString("email"),
							rs.getString("password"),
							rs.getString("userType")
					);
				} else if(Objects.equals(userType, UserType.ADMIN.toString())){
					return new Admin(
							rs.getString("institutionalId"),
							rs.getString("name"),
							rs.getString("email"),
							rs.getString("password"),
							rs.getString("userType")
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE_NAME;

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				System.out.println(rs.getString("institutionalId") + " " + rs.getString("name") + rs.getString("email"));
				String userType = rs.getString("userType");
				if (userType.equals(UserType.STUDENT.toString())) {
					users.add(new Student(
							rs.getString("institutionalId"),
							rs.getString("name"),
							rs.getString("email"),
							rs.getString("password"),
							userType
					));
				} else if (userType.equals(UserType.TELLER.toString())) {
					users.add(new Teller(
							rs.getString("institutionalId"),
							rs.getString("name"),
							rs.getString("email"),
							rs.getString("password"),
							userType
					));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
}
