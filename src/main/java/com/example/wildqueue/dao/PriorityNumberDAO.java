package com.example.wildqueue.dao;

import com.example.wildqueue.models.PriorityNumber;
import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.wildqueue.utils.Utils.comparePriorityNumbers;

public class PriorityNumberDAO {
	private static final String TABLE_NAME = "priority_numbers";

	public static void createPriorityNumberTable() {
		String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				"priorityNumber VARCHAR(20) NOT NULL, " +
				"studentId VARCHAR(50) NOT NULL, " +
				"status VARCHAR(20) DEFAULT 'PENDING', " +
				"createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
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
		String query = "INSERT INTO " + TABLE_NAME + " (priorityNumber, studentId, status, createdAt) VALUES (?, ?, ?, ?)";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumber.getPriorityNumber());
			pstmt.setString(2, priorityNumber.getStudentId());
			pstmt.setString(3, priorityNumber.getStatus().toString());

			Date sqlDate = new java.sql.Date(priorityNumber.getCreatedAt().getTime());
			pstmt.setDate(4, sqlDate);

			pstmt.executeUpdate();
			System.out.println("PriorityNumber added successfully.");
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
						rs.getDate("createdAt")
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
						rs.getDate("createdAt")
				);
				priorityNumbers.add(pn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return priorityNumbers;
	}

	public static List<PriorityNumber> getPriorityNumbersSince(String lastPriorityNumber) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE priorityNumber > ?";

		List<PriorityNumber> priorityNumbers = new ArrayList<>();

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, lastPriorityNumber);
			ResultSet rs = stmt.executeQuery();

			System.out.println("Last Priority Number: " + lastPriorityNumber);

			while (rs.next()) {
				String priorityNumber = rs.getString("priorityNumber");

				if (comparePriorityNumbers(lastPriorityNumber, priorityNumber) < 0) {
					System.out.println("Fetched Priority Number: " + priorityNumber);

					PriorityStatus status = PriorityStatus.valueOf(rs.getString("status"));
					Timestamp createdAtTimestamp = rs.getTimestamp("createdAt");
					Date createdAt = new Date(createdAtTimestamp.getTime());

					PriorityNumber pn = new PriorityNumber(
							priorityNumber,
							rs.getString("studentId"),
							status,
							createdAt
					);
					priorityNumbers.add(pn);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return priorityNumbers;
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
