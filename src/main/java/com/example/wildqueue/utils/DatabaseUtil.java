package com.example.wildqueue.utils;

import com.example.wildqueue.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
	}
}
