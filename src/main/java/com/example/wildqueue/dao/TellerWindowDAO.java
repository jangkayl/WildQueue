package com.example.wildqueue.dao;

import com.example.wildqueue.models.Teller;
import com.example.wildqueue.models.TellerWindow;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.DatabaseUtil;
import com.example.wildqueue.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TellerWindowDAO {
	private static final String TABLE_NAME = "teller_window";

	public static void createTellerWindowTable() {
		String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				"windowNumber INT NOT NULL, " +
				"tellerId VARCHAR(50), " +
				"createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
				"lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
				"PRIMARY KEY (windowNumber), " +
				"INDEX idx_teller (tellerId)" +
				")";

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(query);
			System.out.println("TELLER WINDOW TABLE CREATED SUCCESSFULLY.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertTellerWindow(TellerWindow tellerWindow) {
		String query = "INSERT INTO " + TABLE_NAME + " (windowNumber, tellerId) VALUES (?, ?)";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, tellerWindow.getWindowNumber());
			pstmt.setString(2, tellerWindow.getTeller().getInstitutionalId());
			pstmt.executeUpdate();
			System.out.println("TELLER WINDOW INSERTED SUCCESSFULLY.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static TellerWindow getTellerWindowByNumber(int windowNumber) {
		String query = "SELECT windowNumber, tellerId FROM " + TABLE_NAME + " WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, windowNumber);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String tellerId = rs.getString("tellerId");
					User currentUser = SessionManager.getCurrentUser();

					Teller teller = new Teller(tellerId, currentUser.getName(), currentUser.getPassword(), currentUser.getUserType());

					return new TellerWindow(teller, windowNumber);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
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

	public static void assignTeller(int windowNumber, String tellerId) {
		String query = "UPDATE " + TABLE_NAME + " SET tellerId = ? WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, tellerId);
			pstmt.setInt(2, windowNumber);
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

	public static void removeTeller(int windowNumber) {
		String query = "UPDATE " + TABLE_NAME + " SET tellerId = NULL WHERE windowNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, windowNumber);
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
