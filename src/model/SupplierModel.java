package com.warehouse.model;

import com.warehouse.utils.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class SupplierModel {

    // Lấy tất cả nhà cung cấp
    public List<Map<String, Object>> getAllSuppliers() {
        List<Map<String, Object>> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> supplier = new HashMap<>();
                supplier.put("id", rs.getInt("id"));
                supplier.put("code", rs.getString("code"));
                supplier.put("name", rs.getString("name"));
                supplier.put("phone", rs.getString("phone"));
                supplier.put("email", rs.getString("email"));
                supplier.put("address", rs.getString("address"));
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Thêm nhà cung cấp
    public boolean addSupplier(Map<String, Object> supplier) {
        String sql = "INSERT INTO suppliers(code, name, phone, email, address) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) supplier.get("code"));
            pstmt.setString(2, (String) supplier.get("name"));
            pstmt.setString(3, (String) supplier.get("phone"));
            pstmt.setString(4, (String) supplier.get("email"));
            pstmt.setString(5, (String) supplier.get("address"));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật nhà cung cấp
    public boolean updateSupplier(Map<String, Object> supplier) {
        String sql = "UPDATE suppliers SET code=?, name=?, phone=?, email=?, address=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) supplier.get("code"));
            pstmt.setString(2, (String) supplier.get("name"));
            pstmt.setString(3, (String) supplier.get("phone"));
            pstmt.setString(4, (String) supplier.get("email"));
            pstmt.setString(5, (String) supplier.get("address"));
            pstmt.setInt(6, (int) supplier.get("id"));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa nhà cung cấp
    public boolean deleteSupplier(int id) {
        String sql = "DELETE FROM suppliers WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm nhà cung cấp
    public List<Map<String, Object>> searchSuppliers(String keyword) {
        List<Map<String, Object>> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE code LIKE ? OR name LIKE ? OR phone LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> supplier = new HashMap<>();
                supplier.put("id", rs.getInt("id"));
                supplier.put("code", rs.getString("code"));
                supplier.put("name", rs.getString("name"));
                supplier.put("phone", rs.getString("phone"));
                supplier.put("email", rs.getString("email"));
                supplier.put("address", rs.getString("address"));
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Tạo mã nhà cung cấp tự động (AN TOÀN - KHÔNG LỖI)
    public String generateSupplierCode() {
        String sql = "SELECT code FROM suppliers ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastCode = rs.getString("code");
                if (lastCode != null && lastCode.length() >= 6) {
                    // Kiểm tra mã có đúng định dạng NCCxxx không
                    if (lastCode.startsWith("NCC")) {
                        try {
                            String numberStr = lastCode.substring(3);
                            int number = Integer.parseInt(numberStr);
                            number++;
                            return String.format("NCC%03d", number);
                        } catch (NumberFormatException e) {
                            // Nếu không parse được, tìm số lớn nhất hiện có
                            return getMaxSupplierCode();
                        }
                    } else {
                        // Nếu mã không đúng định dạng, tìm số lớn nhất
                        return getMaxSupplierCode();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NCC001";
    }

    // Lấy mã NCC lớn nhất hiện có
    private String getMaxSupplierCode() {
        String sql = "SELECT code FROM suppliers WHERE code REGEXP '^NCC[0-9]+$' ORDER BY id DESC";
        int maxNumber = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String code = rs.getString("code");
                if (code != null && code.startsWith("NCC")) {
                    try {
                        int num = Integer.parseInt(code.substring(3));
                        if (num > maxNumber) {
                            maxNumber = num;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        maxNumber++;
        return String.format("NCC%03d", maxNumber);
    }

    // Kiểm tra mã nhà cung cấp đã tồn tại
    public boolean isCodeExists(String code) {
        String sql = "SELECT * FROM suppliers WHERE code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}