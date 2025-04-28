package com.example.wildqueue.dao;

import com.example.wildqueue.models.PriorityStatus;
import com.example.wildqueue.models.Transaction;
import com.example.wildqueue.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
	private static final String TABLE_NAME = "transactions";

	public static void createTransactionTable() {
		String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				"transactionId INT NOT NULL AUTO_INCREMENT, " +
				"priorityNumber VARCHAR(20) NOT NULL, " +
				"windowNumber INT NOT NULL, " +
				"studentId VARCHAR(50) NOT NULL, " +
				"tellerId VARCHAR(50), " +
				"amount DOUBLE DEFAULT 0.0, " +
				"transactionType VARCHAR(30) NOT NULL, " +
				"transactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
				"lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
				"status VARCHAR(20) NOT NULL, " +
				"completionDate TIMESTAMP NULL DEFAULT NULL, " +
				"PRIMARY KEY (transactionId), " +
				"INDEX idx_student (studentId), " +
				"INDEX idx_teller (tellerId), " +
				"INDEX idx_status (status)" +
				")";

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement()) {
			stmt.execute(query);
			System.out.println("TRANSACTION TABLE CREATED SUCCESSFULLY");
		} catch (SQLException e) {
			System.err.println("Error creating transaction table:");
			e.printStackTrace();
		}
	}

	public static int createTransaction(Transaction transaction) {
		String query = "INSERT INTO " + TABLE_NAME +
				"(priorityNumber, windowNumber, studentId, tellerId, amount, transactionType, status) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, transaction.getPriorityNumber());
			pstmt.setInt(2, transaction.getWindowNumber());
			pstmt.setString(3, transaction.getStudentId());
			pstmt.setString(4, transaction.getTellerId());
			pstmt.setDouble(5, transaction.getAmount());
			pstmt.setString(6, transaction.getTransactionType());
			pstmt.setString(7, transaction.getStatus());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error creating transaction:");
			e.printStackTrace();
		}
		return -1;
	}

	public static List<Transaction> getTransactionsByStudentId(String studentId) {
		List<Transaction> transactions = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE studentId = ? ORDER BY transactionDate ASC";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, studentId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Transaction transaction = new Transaction(
							rs.getInt("transactionId"),
							rs.getString("priorityNumber"),
							rs.getInt("windowNumber"),
							rs.getString("studentId"),
							rs.getString("tellerId"),
							rs.getDouble("amount"),
							rs.getString("transactionType"),
							rs.getTimestamp("transactionDate"),
							rs.getTimestamp("lastModified"),
							rs.getString("status"),
							rs.getTimestamp("completionDate")
					);
					transactions.add(transaction);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching transactions by studentId:");
			e.printStackTrace();
		}

		return transactions;
	}

	public static Transaction getTransactionByPriorityNumber(String priorityNumber) {
		Transaction transaction = null;
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE priorityNumber = ? LIMIT 1";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, priorityNumber);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					transaction = new Transaction(
							rs.getInt("transactionId"),
							rs.getString("priorityNumber"),
							rs.getInt("windowNumber"),
							rs.getString("studentId"),
							rs.getString("tellerId"),
							rs.getDouble("amount"),
							rs.getString("transactionType"),
							rs.getTimestamp("transactionDate"),
							rs.getTimestamp("lastModified"),
							rs.getString("status"),
							rs.getTimestamp("completionDate")
					);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching transaction by priorityNumber:");
			e.printStackTrace();
		}

		return transaction;
	}

	public static List<Transaction> getCompletedTransactionsByTeller(String tellerId) {
		List<Transaction> completedTransactions = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE tellerId = ? AND status = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, tellerId);
			pstmt.setString(2, PriorityStatus.COMPLETED.toString());

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Timestamp completionDateTimestamp = rs.getTimestamp("completionDate");
					Transaction transaction = new Transaction(
							rs.getInt("transactionId"),
							rs.getString("priorityNumber"),
							rs.getInt("windowNumber"),
							rs.getString("studentId"),
							rs.getString("tellerId"),
							rs.getDouble("amount"),
							rs.getString("transactionType"),
							rs.getTimestamp("transactionDate"),
							rs.getTimestamp("lastModified"),
							rs.getString("status"),
							rs.getTimestamp("completionDate")
					);
					if (completionDateTimestamp != null) {
						transaction.setCompletionDate(Timestamp.valueOf(completionDateTimestamp.toLocalDateTime()));
					}
					completedTransactions.add(transaction);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return completedTransactions;
	}

	public static List<Transaction> getAllTransactions() {
		List<Transaction> transactions = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY transactionDate ASC";

		try (Connection conn = DatabaseUtil.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Transaction transaction = new Transaction(
						rs.getInt("transactionId"),
						rs.getString("priorityNumber"),
						rs.getInt("windowNumber"),
						rs.getString("studentId"),
						rs.getString("tellerId"),
						rs.getDouble("amount"),
						rs.getString("transactionType"),
						rs.getTimestamp("transactionDate"),
						rs.getTimestamp("lastModified"),
						rs.getString("status"),
						rs.getTimestamp("completionDate")
				);
				transactions.add(transaction);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching all transactions:");
			e.printStackTrace();
		}
		return transactions;
	}

	public static void updateTransaction(Transaction transaction) {
		String query = "UPDATE " + TABLE_NAME + " SET " +
				"priorityNumber = ?, " +
				"windowNumber = ?, " +
				"studentId = ?, " +
				"tellerId = ?, " +
				"amount = ?, " +
				"transactionType = ?, " +
				"status = ? " +
				"WHERE transactionId = ?";

		try (Connection conn = DatabaseUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, transaction.getPriorityNumber());
			pstmt.setInt(2, transaction.getWindowNumber());
			pstmt.setString(3, transaction.getStudentId());
			pstmt.setString(4, transaction.getTellerId());
			pstmt.setDouble(5, transaction.getAmount());
			pstmt.setString(6, transaction.getTransactionType());
			pstmt.setString(7, transaction.getStatus());
			pstmt.setInt(8, transaction.getTransactionId());

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("Transaction updated successfully.");
			} else {
				System.out.println("No transaction found with the given ID.");
			}
		} catch (SQLException e) {
			System.err.println("Error updating transaction:");
			e.printStackTrace();
		}
	}
}
