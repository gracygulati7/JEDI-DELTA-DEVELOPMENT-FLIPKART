package com.flipfit.dao;

import java.sql.*;

public class WaitlistDAO {

	private static WaitlistDAO instance = null;

	// Database credentials.
	private final String URL = "jdbc:mysql://localhost:3306/flipfit_db";
	private final String USER = "root";
	private final String PASS = "password";

	private WaitlistDAO() {
	}

	public static synchronized WaitlistDAO getInstance() {
		if (instance == null) {
			instance = new WaitlistDAO();
		}
		return instance;
	}

	private Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			throw new SQLException("JDBC Driver not found", e);
		}
	}

	public void addToWaitlist(int slotId, int userId) {
		String query = "INSERT INTO waitlist (slotId, userId) VALUES (?, ?)";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, slotId);
			stmt.setInt(2, userId);
			stmt.executeUpdate();
			System.out.println("[DB] User " + userId + " added to waitlist for slot " + slotId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Integer removeFromWaitlist(int slotId) {
		String selectQuery = "SELECT userId FROM waitlist WHERE slotId = ? ORDER BY requestTime ASC LIMIT 1";
		String deleteQuery = "DELETE FROM waitlist WHERE slotId = ? AND userId = ?";

		try (Connection conn = getConnection()) {
			int userId = -1;
			try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
				selectStmt.setInt(1, slotId);
				ResultSet rs = selectStmt.executeQuery();
				if (rs.next()) {
					userId = rs.getInt("userId");
				}
			}

			if (userId != -1) {
				try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
					deleteStmt.setInt(1, slotId);
					deleteStmt.setInt(2, userId);
					deleteStmt.executeUpdate();
					return userId;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean hasWaitlistedCustomers(int slotId) {
		String query = "SELECT COUNT(*) FROM waitlist WHERE slotId = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, slotId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getWaitlistSize(int slotId) {
		String query = "SELECT COUNT(*) FROM waitlist WHERE slotId = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, slotId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}