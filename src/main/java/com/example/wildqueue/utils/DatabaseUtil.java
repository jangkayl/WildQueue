package com.example.wildqueue.utils;

import com.example.wildqueue.DatabaseConfig;

import java.sql.*;

public class DatabaseUtil {
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
	}

	public static boolean tableExists(Connection conn, String tableName) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
			return rs.next();
		}
	}
}
