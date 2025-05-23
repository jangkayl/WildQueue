package com.example.wildqueue.dao;

import com.example.wildqueue.models.*;
import com.example.wildqueue.utils.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class TellerWindowDAO {
	private static final String TABLE_NAME = "teller_window";

	public static void createTellerWindowTable() {
		String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				"windowNumber INT NOT NULL, " +
				"tellerId VARCHAR(50), " +
				"studentId VARCHAR(50), " +
				"priorityNumber VARCHAR(50), " +
				"createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
				"lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
				"PRIMARY KEY (windowNumber), " +
				"INDEX idx_teller (tellerId), " +
				"INDEX idx_student (studentId)" +
				")";

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement()) {
			if (!DatabaseUtil.tableExists(conn, TABLE_NAME)) {
				stmt.execute(query);
				System.out.println("TELLER WINDOW TABLE CREATED SUCCESSFULLY");
			} else {
				System.out.println("TELLER WINDOW TABLE ALREADY EXISTS");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<TellerWindow> getWindowSince(Timestamp lastModifiedSince) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE lastModified > ? ORDER BY windowNumber ASC";

		List<TellerWindow> tellerWindows = new ArrayList<>();

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			stmt.setTimestamp(1, Timestamp.valueOf(sdf.format(lastModifiedSince)));
			ResultSet rs = stmt.executeQuery();

			System.out.println("Formatted Timestamp Window: " + Timestamp.valueOf(sdf.format(lastModifiedSince)));

			while (rs.next()) {
				System.out.println("--------------------");
				System.out.println("TELLER WINDOW");
				System.out.println("Comparing DB: " + rs.getTimestamp("lastModified") + " vs Query: " + Timestamp.valueOf(sdf.format(lastModifiedSince)));

				TellerWindow window = new TellerWindow(
						rs.getString("tellerId"),
						rs.getString("studentId"),
						rs.getInt("windowNumber"),
						rs.getString("priorityNumber"),
						rs.getTimestamp("createdAt"),
						rs.getTimestamp("lastModified")
				);
				tellerWindows.add(window);

				System.out.println("Fetched Window Number: " + window.getWindowNumber());
				System.out.println("StudentId: " + window.getStudentId());
				System.out.println("TellerId: " + window.getTellerId());
				System.out.println("Last Modified: " + rs.getTimestamp("lastModified"));
				System.out.println("----");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tellerWindows;
	}

	public static List<TellerWindow> getAllTellerWindows() {
		List<TellerWindow> tellerWindows = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE_NAME;

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query);
		     ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				TellerWindow window = new TellerWindow(
						rs.getString("tellerId"),
						rs.getString("studentId"),
						rs.getInt("windowNumber"),
						rs.getString("priorityNumber"),
						rs.getTimestamp("createdAt"),
						rs.getTimestamp("lastModified")
				);

				tellerWindows.add(window);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tellerWindows;
	}


	public static void assignStudentToWindow(int windowNumber, String studentId, String priorityNumber, Timestamp lastModified) {
		String query = "UPDATE " + TABLE_NAME + " SET studentId = ?, priorityNumber = ?, lastModified = ? WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, studentId);
			pstmt.setString(2, priorityNumber);
			pstmt.setTimestamp(3, lastModified);
			pstmt.setInt(4, windowNumber);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("STUDENT ASSIGNED TO WINDOW SUCCESSFULLY.");
			} else {
				System.out.println("NO WINDOW FOUND TO ASSIGN STUDENT.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeStudentFromWindow(int windowNumber, Timestamp lastModified) {
		String query = "UPDATE " + TABLE_NAME + " SET studentId = NULL, priorityNumber = NULL, lastModified = ? WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setTimestamp(1, lastModified);
			pstmt.setInt(2, windowNumber);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("STUDENT REMOVED FROM WINDOW SUCCESSFULLY.");
			} else {
				System.out.println("NO WINDOW FOUND TO REMOVE STUDENT.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteTellerWindow(int windowNumber) {
		String query = "DELETE FROM " + TABLE_NAME + " WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, windowNumber);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("TELLER WINDOW DELETED SUCCESSFULLY.");
			} else {
				System.out.println("NO TELLER WINDOW FOUND TO DELETE.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void assignTeller(int windowNumber, String tellerId, Timestamp lastModified) {
		String query = "UPDATE " + TABLE_NAME + " SET tellerId = ?, lastModified = ? WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, tellerId);
			pstmt.setTimestamp(2, lastModified);
			pstmt.setInt(3, windowNumber);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("TELLER ASSIGNED SUCCESSFULLY.");
			} else {
				System.out.println("NO TELLER WINDOW FOUND TO ASSIGN.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeTeller(int windowNumber, Timestamp lastModified) {
		String query = "UPDATE " + TABLE_NAME + " SET tellerId = '', lastModified = ? WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setTimestamp(1, lastModified);
			pstmt.setInt(2, windowNumber);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("TELLER REMOVED SUCCESSFULLY.");
			} else {
				System.out.println("NO TELLER WINDOW FOUND TO REMOVE TELLER.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}