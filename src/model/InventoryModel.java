package com.warehouse.model;

import com.warehouse.utils.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class InventoryModel {

    // Lấy tất cả sản phẩm với thông tin tồn kho chi tiết
    public List<Map<String, Object>> getAllInventory() {
        List<Map<String, Object>> inventory = new ArrayList<>();
        String sql = "SELECT p.id, p.code, p.name, p.category, p.unit, p.quantity, " +
                "p.import_price, p.sell_price, p.expiry_date, s.name as supplier_name, " +
                "CASE " +
                "   WHEN p.quantity <= 5 THEN 'CẢNH BÁO: Hàng sắp hết' " +
                "   WHEN p.quantity <= 10 THEN 'CẢNH BÁO: Hàng sắp hết' " +
                "   WHEN p.expiry_date < DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND p.expiry_date > CURDATE() THEN 'Sắp hết hạn' " +
                "   WHEN p.expiry_date <= CURDATE() THEN 'Hết hạn' " +
                "   ELSE 'Bình thường' " +
                "END as status " +
                "FROM products p " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "ORDER BY p.quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("code", rs.getString("code"));
                item.put("name", rs.getString("name"));
                item.put("category", rs.getString("category"));
                item.put("unit", rs.getString("unit"));
                item.put("quantity", rs.getInt("quantity"));
                item.put("import_price", rs.getDouble("import_price"));
                item.put("sell_price", rs.getDouble("sell_price"));
                item.put("expiry_date", rs.getDate("expiry_date"));
                item.put("supplier_name", rs.getString("supplier_name"));
                item.put("status", rs.getString("status"));
                inventory.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventory;
    }

    // Lấy sản phẩm tồn kho thấp (<= ngưỡng)
    public List<Map<String, Object>> getLowStockProducts(int threshold) {
        List<Map<String, Object>> lowStock = new ArrayList<>();
        String sql = "SELECT p.id, p.code, p.name, p.quantity, p.sell_price, " +
                "s.name as supplier_name " +
                "FROM products p " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "WHERE p.quantity <= ? " +
                "ORDER BY p.quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("code", rs.getString("code"));
                item.put("name", rs.getString("name"));
                item.put("quantity", rs.getInt("quantity"));
                item.put("sell_price", rs.getDouble("sell_price"));
                item.put("supplier_name", rs.getString("supplier_name"));
                lowStock.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStock;
    }

    // Lấy sản phẩm sắp hết hạn (trong vòng 30 ngày tới)
    public List<Map<String, Object>> getExpiringProducts() {
        List<Map<String, Object>> expiring = new ArrayList<>();
        String sql = "SELECT p.id, p.code, p.name, p.quantity, p.expiry_date, " +
                "DATEDIFF(p.expiry_date, CURDATE()) as days_left " +
                "FROM products p " +
                "WHERE p.expiry_date IS NOT NULL " +
                "AND p.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) " +
                "ORDER BY p.expiry_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("code", rs.getString("code"));
                item.put("name", rs.getString("name"));
                item.put("quantity", rs.getInt("quantity"));
                item.put("expiry_date", rs.getDate("expiry_date"));
                item.put("days_left", rs.getInt("days_left"));
                expiring.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expiring;
    }

    // Lấy tổng giá trị tồn kho
    public Map<String, Double> getInventorySummary() {
        Map<String, Double> summary = new HashMap<>();
        String sql = "SELECT " +
                "SUM(quantity * import_price) as total_value, " +
                "SUM(quantity) as total_quantity, " +
                "COUNT(*) as total_products " +
                "FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                summary.put("total_value", rs.getDouble("total_value"));
                summary.put("total_quantity", (double) rs.getInt("total_quantity"));
                summary.put("total_products", (double) rs.getInt("total_products"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    // Tìm kiếm sản phẩm theo tên hoặc mã
    public List<Map<String, Object>> searchInventory(String keyword) {
        List<Map<String, Object>> inventory = new ArrayList<>();
        String sql = "SELECT p.id, p.code, p.name, p.category, p.unit, p.quantity, " +
                "p.import_price, p.sell_price, p.expiry_date, s.name as supplier_name " +
                "FROM products p " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "WHERE p.code LIKE ? OR p.name LIKE ? OR p.category LIKE ? " +
                "ORDER BY p.quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("code", rs.getString("code"));
                item.put("name", rs.getString("name"));
                item.put("category", rs.getString("category"));
                item.put("unit", rs.getString("unit"));
                item.put("quantity", rs.getInt("quantity"));
                item.put("import_price", rs.getDouble("import_price"));
                item.put("sell_price", rs.getDouble("sell_price"));
                item.put("expiry_date", rs.getDate("expiry_date"));
                item.put("supplier_name", rs.getString("supplier_name"));
                inventory.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventory;
    }
    // Đếm sản phẩm sắp hết hạn trong 30 ngày
public int countExpiringProducts() {
    String sql = "SELECT COUNT(*) FROM products " +
                 "WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}
}
