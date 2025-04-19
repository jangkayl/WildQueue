package com.example.wildqueue;

import com.example.wildqueue.models.Student;
import com.example.wildqueue.models.User;

import java.sql.*;

public class CRUD {
	private static final String URL = "gateway01.ap-southeast-1.prod.aws.tidbcloud.com";
	private static final String PORT = "4000";
	private static final String USERNAME = "Yb6TTiydzfUS3EJ.root";
	private static final String PASSWORD = "uQm1CEVIsEqWZWNJ";
	private static final String DATABASE = "test";
	private static final String CA = "E:/PreFiJava/src/main/java/com/example/prefijava/isrgrootx1.pem";

	public static Connection getConnection() throws SQLException {
		String url = String.format(
				"jdbc:mysql://%s:%s/%s?useSSL=true&requireSSL=true&verifyServerCertificate=true&trustCertificate=file:%s",
				URL, PORT, DATABASE, CA
		);
		return DriverManager.getConnection(url, USERNAME, PASSWORD);
	}

	public static void createUserTable() {
		try {
			Connection connection = getConnection();
			System.out.println("USER TABLE CREATION STARTED...");
			Statement statement = connection.createStatement();
			String query = "CREATE TABLE users (" +
					"userId BIGINT NOT NULL AUTO_INCREMENT, " +
					"institutionalId VARCHAR(50) NOT NULL, " +
					"email VARCHAR(100) NOT NULL, " +
					"name VARCHAR(100) NOT NULL, " +
					"password VARCHAR(100) NOT NULL, " +
					"userType VARCHAR(20) NOT NULL, " +
					"PRIMARY KEY (userId)" +
					") AUTO_INCREMENT=1000";
			statement.execute(query);
			System.out.println("USER TABLE CREATED SUCCESSFULLY");
		} catch (SQLException e) {
		}
	}

	public static boolean institutionalIdExists(String institutionalId) {
		String query = "SELECT COUNT(*) FROM users WHERE institutionalId = ?";
		try (Connection conn = getConnection();
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
		try (Connection conn = getConnection();
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

		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, institutionalId);
			statement.setString(2, email);
			statement.setString(3, name);
			statement.setString(4, hashedPassword);
			statement.setString(5, userType);

			int rowsAffected = statement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("User added successfully");
			} else {
				System.out.println("Failed to add user");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static User getUserByInstitutionalId(String institutionalId) {
		try (Connection connection = getConnection();
		     PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE institutionalId = ?")) {

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