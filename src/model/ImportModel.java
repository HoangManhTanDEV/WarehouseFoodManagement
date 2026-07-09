package com.warehouse.model;
import com.warehouse.utils.DatabaseConnection;
import java.sql.*;
import java.util.*;
public class ImportModel {

    // Lấy danh sách nhà cung cấp
    public List<Map<String, Object>> getAllSuppliers() {
        List<Map<String, Object>> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> supplier = new HashMap<>();
                supplier.put("id", rs.getInt("id"));
                supplier.put("code", rs.getString("code"));
                supplier.put("name", rs.getString("name"));
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Lấy danh sách sản phẩm để nhập
    public List<Map<String, Object>> getAllProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT id, code, name, import_price FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("code", rs.getString("code"));
                product.put("name", rs.getString("name"));
                product.put("import_price", rs.getDouble("import_price"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Tạo phiếu nhập mới
    public boolean createImportReceipt(Map<String, Object> receipt, List<Map<String, Object>> details) {
        // ============ KIỂM TRA GIỎ HÀNG TRỐNG ============
        if (details == null || details.isEmpty()) {
            System.err.println("❌ [ImportModel] Giỏ hàng trống, không thể tạo phiếu nhập!");
            return false;
        }
        // ================================================

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Thêm phiếu nhập
            String receiptSql = "INSERT INTO import_receipts(receipt_code, import_date, supplier_id, total_amount, user_id, note) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement receiptStmt = conn.prepareStatement(receiptSql, Statement.RETURN_GENERATED_KEYS);
            receiptStmt.setString(1, (String) receipt.get("receipt_code"));
            receiptStmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            receiptStmt.setInt(3, (int) receipt.get("supplier_id"));
            receiptStmt.setDouble(4, (double) receipt.get("total_amount"));
            receiptStmt.setInt(5, (int) receipt.get("user_id"));
            receiptStmt.setString(6, (String) receipt.get("note"));
            receiptStmt.executeUpdate();

            ResultSet generatedKeys = receiptStmt.getGeneratedKeys();
            int receiptId = -1;
            if (generatedKeys.next()) {
                receiptId = generatedKeys.getInt(1);
            }
            receiptStmt.close();

            // 2. Thêm chi tiết và cập nhật tồn kho
            String detailSql = "INSERT INTO import_details(receipt_id, product_id, quantity, unit_price) VALUES(?, ?, ?, ?)";
            PreparedStatement detailStmt = conn.prepareStatement(detailSql);

            String updateProductSql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateProductSql);

            for (Map<String, Object> detail : details) {
                detailStmt.setInt(1, receiptId);
                detailStmt.setInt(2, (int) detail.get("product_id"));
                detailStmt.setInt(3, (int) detail.get("quantity"));
                detailStmt.setDouble(4, (double) detail.get("unit_price"));
                detailStmt.executeUpdate();

                updateStmt.setInt(1, (int) detail.get("quantity"));
                updateStmt.setInt(2, (int) detail.get("product_id"));
                updateStmt.executeUpdate();
            }

            detailStmt.close();
            updateStmt.close();
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Tạo mã phiếu nhập tự động
    public String generateReceiptCode() {
        String sql = "SELECT MAX(receipt_code) as max_code FROM import_receipts";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next() && rs.getString("max_code") != null) {
                String lastCode = rs.getString("max_code");
                int number = Integer.parseInt(lastCode.substring(2)) + 1;
                return String.format("PN%05d", number);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PN00001";
    }
}
