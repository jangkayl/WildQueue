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
				"status VARCHAR(20) DEFAULT 'PENDING', " +
				"createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
				"lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
				"PRIMARY KEY (priorityNumber), " +
				"INDEX idx_student (studentId)" +
				")";

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(query);
			System.out.println("PRIORITY NUMBER TABLE CREATED SUCCESSFULLY.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addPriorityNumber(PriorityNumber priorityNumber) {
		String query = "INSERT INTO " + TABLE_NAME + " (priorityNumber, studentId, status) VALUES (?, ?, ?)";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumber.getPriorityNumber());
			pstmt.setString(2, priorityNumber.getStudentId());
			pstmt.setString(3, priorityNumber.getStatus().toString());

			pstmt.executeUpdate();
			System.out.println("PriorityNumber added successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updatePriorityNumber(PriorityNumber priorityNumber) {
		String query = "UPDATE " + TABLE_NAME + " SET " +
				"studentId = ?, " +
				"status = ? " +
				"WHERE priorityNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumber.getStudentId());
			pstmt.setString(2, priorityNumber.getStatus().toString());
			pstmt.setString(3, priorityNumber.getPriorityNumber());

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

	public static List<PriorityNumber> getPriorityNumbersSince(String lastPriorityNumber, Timestamp lastModifiedSince) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE priorityNumber > ? OR lastModified > ? ORDER BY priorityNumber ASC";

		List<PriorityNumber> priorityNumbers = new ArrayList<>();

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			stmt.setString(1, lastPriorityNumber);
			stmt.setTimestamp(2, Timestamp.valueOf(sdf.format(lastModifiedSince)));
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Timestamp dbTimestamp = rs.getTimestamp("lastModified");
				String formattedTimestamp = sdf.format(dbTimestamp);
				System.out.println("Comparing DB: " + formattedTimestamp + " vs Query: " + sdf.format(lastModifiedSince));

				PriorityNumber pn = new PriorityNumber(
						rs.getString("priorityNumber"),
						rs.getString("studentId"),
						PriorityStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("createdAt"),
						dbTimestamp
				);
				priorityNumbers.add(pn);

				System.out.println("Fetched Priority Number: " + pn.getPriorityNumber());
				System.out.println("Status: " + pn.getStatus());
				System.out.println("Last Modified: " + formattedTimestamp);
				System.out.println("----");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return priorityNumbers;
	}

	public static boolean updatePriorityNumberStatus(String priorityNumberId, PriorityStatus newStatus) {
		String query = "UPDATE " + TABLE_NAME + " SET status = ?, lastModified = CURRENT_TIMESTAMP WHERE priorityNumber = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, newStatus.toString());
			pstmt.setString(2, priorityNumberId);

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
