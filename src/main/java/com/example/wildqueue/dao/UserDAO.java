package com.example.wildqueue.dao;

import com.example.wildqueue.models.Student;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.DatabaseUtil;

import java.sql.*;

public class UserDAO {

	public static void createUserTable() {
		String query = "CREATE TABLE IF NOT EXISTS users (" +
				"userId BIGINT NOT NULL AUTO_INCREMENT, " +
				"institutionalId VARCHAR(50) NOT NULL, " +
				"email VARCHAR(100) NOT NULL, " +
				"name VARCHAR(100) NOT NULL, " +
				"password VARCHAR(100) NOT NULL, " +
				"userType VARCHAR(20) NOT NULL, " +
				"PRIMARY KEY (userId)) AUTO_INCREMENT=1000";

		try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
			System.out.println("USER TABLE CREATION STARTED...");
			stmt.execute(query);
			System.out.println("USER TABLE CREATED SUCCESSFULLY");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean institutionalIdExists(String institutionalId) {
		String query = "SELECT COUNT(*) FROM users WHERE institutionalId = ?";
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
		String query = "SELECT COUNT(*) FROM users WHERE email = ?";
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
		String query = "INSERT INTO users (institutionalId, email, name, password, userType) VALUES (?, ?, ?, ?, ?)";
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
		String query = "SELECT * FROM users WHERE institutionalId = ?";
		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, institutionalId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Student(
						rs.getString("institutionalId"),
						rs.getString("name"),
						rs.getString("password"),
						rs.getString("userType")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
