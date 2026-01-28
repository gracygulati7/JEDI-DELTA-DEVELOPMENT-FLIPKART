package com.flipfit.dao;

import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.util.DBUtil;

import java.sql.*;
import java.util.*;

public class CustomerDAO {
    private static CustomerDAO instance = null;

    private CustomerDAO() {
        // No need to load the driver here — DBUtil's static block already loads it.
    }

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

//    public FlipFitCustomer addCustomer(String fullName) {
//        String sql = "INSERT INTO customers (full_name) VALUES (?)";
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            ps.setString(1, fullName);
//            int affected = ps.executeUpdate();
//            if (affected == 0) throw new SQLException("Creating customer failed, no rows affected.");
//            try (ResultSet keys = ps.getGeneratedKeys()) {
//                if (keys.next()) {
//                    int id = keys.getInt(1);
//                    FlipFitCustomer c = new FlipFitCustomer();
//                    c.setUserId(id);
//                    c.setFullName(fullName);
//                    return c;
//                } else {
//                    throw new SQLException("Creating customer failed, no ID obtained.");
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
    
    public FlipFitCustomer addCustomer(String fullName) {
        // 1. Create the 'Identity' in the parent table
        String userSql = "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)";
        // 2. Create the 'Profile' in the child table
        String customerSql = "INSERT INTO customers (customer_id, full_name, role) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Ensure both happen or neither happens

            int newId = -1;
            try (PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, fullName);
                // Defaulting email/pass since this is auto-registration during login
                psUser.setString(2, fullName.toLowerCase().replace(" ", "") + "@flipfit.com");
                psUser.setString(3, "password123");
                psUser.setString(4, "CUSTOMER");
                psUser.executeUpdate();

                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) newId = rs.getInt(1);
            }

            if (newId != -1) {
                try (PreparedStatement psCust = conn.prepareStatement(customerSql)) {
                    psCust.setInt(1, newId); // Link the child to the parent
                    psCust.setString(2, fullName);
                    psCust.setString(3, "CUSTOMER");
                    psCust.executeUpdate();
                }
            }

            conn.commit(); // Finalize the "Identity + Profile" creation

            FlipFitCustomer c = new FlipFitCustomer();
            c.setUserId(newId);
            c.setFullName(fullName);
            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Error during customer inheritance setup: " + e.getMessage());
        }
    }

    public FlipFitCustomer getCustomerByName(String name) {
        String sql = "SELECT * FROM customers WHERE LOWER(full_name) = LOWER(?) LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToCustomer(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FlipFitCustomer getOrCreateCustomerByName(String name) {
        FlipFitCustomer c = getCustomerByName(name);
        if (c != null) return c;
        return addCustomer(name);
    }

    public FlipFitCustomer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE customer_id = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToCustomer(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<FlipFitCustomer> getAllCustomers() {
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
            throw new RuntimeException(e);
        }
    }

    public void updateCustomer(FlipFitCustomer user) {
        if (user == null) return;
        String sql = "UPDATE customers SET full_name = ?, role = ?, contact = ?, payment_type = ?, payment_info = ? WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getContact());

            // paymentType might be nullable — handle accordingly
            if (user.getPaymentType() != 0) ps.setInt(4, user.getPaymentType());
            else ps.setNull(4, Types.INTEGER);

            ps.setString(5, user.getPaymentInfo());
            ps.setInt(6, user.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePaymentDetails(int userId, int paymentType, String paymentInfo) {
        String sql = "UPDATE customers SET payment_type = ?, payment_info = ? WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentType);
            ps.setString(2, paymentInfo);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private FlipFitCustomer mapRowToCustomer(ResultSet rs) throws SQLException {
        FlipFitCustomer c = new FlipFitCustomer();
        c.setUserId(rs.getInt("customer_id"));
        c.setFullName(rs.getString("full_name"));
        c.setRole(rs.getString("role"));
        c.setContact(rs.getString("contact"));
        int pt = rs.getInt("payment_type");
        if (!rs.wasNull()) c.setPaymentType(pt);
        c.setPaymentInfo(rs.getString("payment_info"));
        return c;
    }
}
