package com.flipfit.dao;

import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.util.DBUtil;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;

import java.sql.*;
import java.util.*;

public class CustomerDAO {
    private static CustomerDAO instance = null;

    private CustomerDAO() {}

    public static CustomerDAO getInstance() {
        if (instance == null) {
            synchronized (CustomerDAO.class) {
                if (instance == null) instance = new CustomerDAO();
            }
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    public FlipFitCustomer addCustomer(String fullName) throws DbConnectionException {
        String sql = "INSERT INTO customers (full_name) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fullName);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Creating customer failed, no rows affected.");
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    FlipFitCustomer c = new FlipFitCustomer();
                    c.setUserId(id);
                    c.setFullName(fullName);
                    return c;
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error adding customer: " + fullName, e);
        }
    }

    public FlipFitCustomer getCustomerByName(String name) throws DbConnectionException, UserNotFoundException {
        String sql = "SELECT * FROM customers WHERE LOWER(full_name) = LOWER(?) LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToCustomer(rs);
                throw new UserNotFoundException("Customer not found with name: " + name);
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching customer by name", e);
        }
    }

    public FlipFitCustomer getOrCreateCustomerByName(String name) throws DbConnectionException {
        try {
            return getCustomerByName(name);
        } catch (UserNotFoundException e) {
            // If not found, create new
            return addCustomer(name);
        }
    }

    public FlipFitCustomer getCustomerById(int id) throws DbConnectionException, UserNotFoundException {
        String sql = "SELECT * FROM customers WHERE customerId = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToCustomer(rs);
                throw new UserNotFoundException("Customer not found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching customer by ID", e);
        }
    }

    public Collection<FlipFitCustomer> getAllCustomers() throws DbConnectionException {
        List<FlipFitCustomer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToCustomer(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching all customers", e);
        }
    }

    public void updateCustomer(FlipFitCustomer user) throws DbConnectionException {
        if (user == null) return;
        String sql = "UPDATE customers SET full_name = ?, role = ?, contact = ?, payment_type = ?, payment_info = ? WHERE customerId = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getContact());

            if (user.getPaymentType() != 0) ps.setInt(4, user.getPaymentType());
            else ps.setNull(4, Types.INTEGER);

            ps.setString(5, user.getPaymentInfo());
            ps.setInt(6, user.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbConnectionException("Error updating customer", e);
        }
    }

    public void updatePaymentDetails(int userId, int paymentType, String paymentInfo) throws DbConnectionException {
        String sql = "UPDATE customers SET payment_type = ?, payment_info = ? WHERE customerId = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentType);
            ps.setString(2, paymentInfo);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbConnectionException("Error updating payment details", e);
        }
    }

    private FlipFitCustomer mapRowToCustomer(ResultSet rs) throws SQLException {
        FlipFitCustomer c = new FlipFitCustomer();
        c.setUserId(rs.getInt("customerId"));
        c.setFullName(rs.getString("full_name"));
        c.setRole(rs.getString("role"));
        c.setContact(rs.getString("contact"));
        int pt = rs.getInt("payment_type");
        if (!rs.wasNull()) c.setPaymentType(pt);
        c.setPaymentInfo(rs.getString("payment_info"));
        return c;
    }
}