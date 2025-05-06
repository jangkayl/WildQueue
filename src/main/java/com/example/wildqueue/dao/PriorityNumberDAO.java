package com.example.wildqueue.dao;

import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.utils.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class PriorityNumberDAO {
	private static final String TABLE_NAME = "priority_numbers";

	public static void createPriorityNumberTable() {
		String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				"priorityNumber VARCHAR(20) NOT NULL, " +
				"studentId VARCHAR(50) NOT NULL, " +
				"tellerId VARCHAR(50), " +
				"status VARCHAR(20) DEFAULT 'PENDING', " +
				"createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
				"lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
				"PRIMARY KEY (priorityNumber), " +
				"INDEX idx_student (studentId)" +
				")";

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement()) {
			if (!DatabaseUtil.tableExists(conn, TABLE_NAME)) {
				stmt.execute(query);
				System.out.println("PRIORITY NUMBER TABLE CREATED SUCCESSFULLY");
			} else {
				System.out.println("PRIORITY NUMBER TABLE ALREADY EXISTS");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addPriorityNumber(PriorityNumber priorityNumber) {
		String query = "INSERT INTO " + TABLE_NAME + " (priorityNumber, studentId, status, createdAt, lastModified) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumber.getPriorityNumber());
			pstmt.setString(2, priorityNumber.getStudentId());
			pstmt.setString(3, priorityNumber.getStatus().toString());
			pstmt.setTimestamp(4, priorityNumber.getCreatedAt());
			pstmt.setTimestamp(5, priorityNumber.getLastModified());

			pstmt.executeUpdate();
			System.out.println("PriorityNumber added successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updatePriorityNumber(PriorityNumber priorityNumber) {
		String query = "UPDATE " + TABLE_NAME + " SET " +
				"studentId = ?, " +
				"status = ?, " +
				"lastModified = ? " +
				"WHERE priorityNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumber.getStudentId());
			pstmt.setString(2, priorityNumber.getStatus().toString());
			pstmt.setTimestamp(3, priorityNumber.getLastModified());
			pstmt.setString(4, priorityNumber.getPriorityNumber());

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("PriorityNumber updated successfully.");
			} else {
				System.out.println("No PriorityNumber found with the given ID.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static PriorityNumber getPriorityNumber(String priorityNumberId) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE priorityNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumberId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return new PriorityNumber(
						rs.getString("priorityNumber"),
						rs.getString("studentId"),
						rs.getString("tellerId"),
						PriorityStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("createdAt"),
						rs.getTimestamp("lastModified")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<PriorityNumber> getAllPriorityNumbers() {
		List<PriorityNumber> priorityNumbers = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE_NAME;

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				PriorityNumber pn = new PriorityNumber(
						rs.getString("priorityNumber"),
						rs.getString("studentId"),
						rs.getString("tellerId"),
						PriorityStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("createdAt"),
						rs.getTimestamp("lastModified")
				);
				priorityNumbers.add(pn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return priorityNumbers;
	}

	public static PriorityNumber getLatestPriorityNumber() {
		String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY createdAt DESC LIMIT 1";

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(query)) {

			if (rs.next()) {
				return new PriorityNumber(
						rs.getString("priorityNumber"),
						rs.getString("studentId"),
						rs.getString("tellerId"),
						PriorityStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("createdAt"),
						rs.getTimestamp("lastModified")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<PriorityNumber> getPriorityNumbersSince(Timestamp lastModifiedSince) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE lastModified > ? ORDER BY priorityNumber ASC";

		List<PriorityNumber> priorityNumbers = new ArrayList<>();

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			stmt.setTimestamp(1, Timestamp.valueOf(sdf.format(lastModifiedSince)));
			ResultSet rs = stmt.executeQuery();

			System.out.println("Formatted Timestamp Prio Num: " + Timestamp.valueOf(sdf.format(lastModifiedSince)));

			while (rs.next()) {
				System.out.println("--------------------");
				System.out.println("PRIORITY NUM");
				System.out.println("Comparing DB: " + rs.getTimestamp("lastModified") + " vs Query: " + Timestamp.valueOf(sdf.format(lastModifiedSince)));

				PriorityNumber pn = new PriorityNumber(
						rs.getString("priorityNumber"),
						rs.getString("studentId"),
						rs.getString("tellerId"),
						PriorityStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("createdAt"),
						rs.getTimestamp("lastModified")
				);
				priorityNumbers.add(pn);

				System.out.println("Fetched Priority Number: " + pn.getPriorityNumber());
				System.out.println("Status: " + pn.getStatus());
				System.out.println("Last Modified: " + rs.getTimestamp("lastModified"));
				System.out.println("----");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return priorityNumbers;
	}

	public static boolean updatePriorityNumberStatus(String priorityNumberId, PriorityStatus newStatus, String tellerId, Timestamp lastModified) {
		String query = "UPDATE " + TABLE_NAME + " SET status = ?, tellerId = ?, lastModified = ? WHERE priorityNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, newStatus.toString());
			pstmt.setString(2, tellerId);
			pstmt.setTimestamp(3, lastModified);
			pstmt.setString(4, priorityNumberId);

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("PriorityNumber status updated successfully.");
				return true;
			} else {
				System.out.println("No PriorityNumber found with the given ID.");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void deletePriorityNumber(String priorityNumberId) {
		String query = "DELETE FROM " + TABLE_NAME + " WHERE priorityNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumberId);
			pstmt.executeUpdate();
			System.out.println("PriorityNumber deleted successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
