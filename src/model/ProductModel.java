package com.warehouse.model;

import com.warehouse.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductModel {

    // Lấy tất cả sản phẩm
    public List<Map<String, Object>> getAllProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT p.*, s.name as supplier_name FROM products p " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "ORDER BY p.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("code", rs.getString("code"));
                product.put("name", rs.getString("name"));
                product.put("category", rs.getString("category"));
                product.put("unit", rs.getString("unit"));
                product.put("expiry_date", rs.getDate("expiry_date"));
                product.put("quantity", rs.getInt("quantity"));
                product.put("import_price", rs.getDouble("import_price"));
                product.put("sell_price", rs.getDouble("sell_price"));
                product.put("supplier_id", rs.getInt("supplier_id"));
                product.put("supplier_name", rs.getString("supplier_name"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Thêm sản phẩm
    public boolean addProduct(Map<String, Object> product) {
        String sql = "INSERT INTO products(code, name, category, unit, expiry_date, " +
                "quantity, import_price, sell_price, supplier_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) product.get("code"));
            pstmt.setString(2, (String) product.get("name"));
            pstmt.setString(3, (String) product.get("category"));
            pstmt.setString(4, (String) product.get("unit"));

            // Xử lý expiry_date (có thể null)
            Date expiryDate = (Date) product.get("expiry_date");
            if (expiryDate != null) {
                pstmt.setDate(5, expiryDate);
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setInt(6, (int) product.get("quantity"));
            pstmt.setDouble(7, (double) product.get("import_price"));
            pstmt.setDouble(8, (double) product.get("sell_price"));
            pstmt.setInt(9, (int) product.get("supplier_id"));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật sản phẩm
    public boolean updateProduct(Map<String, Object> product) {
        String sql = "UPDATE products SET code=?, name=?, category=?, unit=?, " +
                "expiry_date=?, quantity=?, import_price=?, sell_price=?, supplier_id=? " +
                "WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) product.get("code"));
            pstmt.setString(2, (String) product.get("name"));
            pstmt.setString(3, (String) product.get("category"));
            pstmt.setString(4, (String) product.get("unit"));

            // Xử lý expiry_date
            Date expiryDate = (Date) product.get("expiry_date");
            if (expiryDate != null) {
                pstmt.setDate(5, expiryDate);
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setInt(6, (int) product.get("quantity"));
            pstmt.setDouble(7, (double) product.get("import_price"));
            pstmt.setDouble(8, (double) product.get("sell_price"));
            pstmt.setInt(9, (int) product.get("supplier_id"));
            pstmt.setInt(10, (int) product.get("id"));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa sản phẩm
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm sản phẩm
    public List<Map<String, Object>> searchProducts(String keyword) {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT p.*, s.name as supplier_name FROM products p " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "WHERE p.code LIKE ? OR p.name LIKE ? OR p.category LIKE ? " +
                "ORDER BY p.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("code", rs.getString("code"));
                product.put("name", rs.getString("name"));
                product.put("category", rs.getString("category"));
                product.put("unit", rs.getString("unit"));
                product.put("expiry_date", rs.getDate("expiry_date"));
                product.put("quantity", rs.getInt("quantity"));
                product.put("import_price", rs.getDouble("import_price"));
                product.put("sell_price", rs.getDouble("sell_price"));
                product.put("supplier_id", rs.getInt("supplier_id"));
                product.put("supplier_name", rs.getString("supplier_name"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Lấy sản phẩm theo ID
    public Map<String, Object> getProductById(int id) {
        String sql = "SELECT p.*, s.name as supplier_name FROM products p " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("code", rs.getString("code"));
                product.put("name", rs.getString("name"));
                product.put("category", rs.getString("category"));
                product.put("unit", rs.getString("unit"));
                product.put("expiry_date", rs.getDate("expiry_date"));
                product.put("quantity", rs.getInt("quantity"));
                product.put("import_price", rs.getDouble("import_price"));
                product.put("sell_price", rs.getDouble("sell_price"));
                product.put("supplier_id", rs.getInt("supplier_id"));
                product.put("supplier_name", rs.getString("supplier_name"));
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra mã sản phẩm tồn tại
    public boolean isCodeExists(String code, int excludeId) {
        String sql = "SELECT * FROM products WHERE code = ? AND id != ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách sản phẩm có tồn kho thấp (dưới ngưỡng)
    public List<Map<String, Object>> getLowStockProducts(int threshold) {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity < ? ORDER BY quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("code", rs.getString("code"));
                product.put("name", rs.getString("name"));
                product.put("quantity", rs.getInt("quantity"));
                product.put("sell_price", rs.getDouble("sell_price"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}