package com.warehouse.model;

import com.warehouse.utils.DatabaseConnection;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.*;

public class ExportModel {

    // Lấy danh sách sản phẩm có tồn kho
    public List<Map<String, Object>> getAvailableProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT id, code, name, quantity, sell_price FROM products WHERE quantity > 0 ORDER BY name";

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("code", rs.getString("code"));
                product.put("name", rs.getString("name"));
                product.put("quantity", rs.getInt("quantity"));
                product.put("sell_price", rs.getDouble("sell_price"));
                products.add(product);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Tạo mã phiếu xuất tự động
    public String generateReceiptCode() {
        String sql = "SELECT MAX(receipt_code) as max_code FROM export_receipts";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next() && rs.getString("max_code") != null) {
                String lastCode = rs.getString("max_code");
                int number = Integer.parseInt(lastCode.substring(2)) + 1;
                rs.close();
                stmt.close();
                conn.close();
                return String.format("PX%05d", number);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PX00001";
    }

    // Tạo phiếu xuất mới
    public boolean createExportReceipt(Map<String, Object> receipt, List<Map<String, Object>> details) {
        // ============ KIỂM TRA GIỎ HÀNG TRỐNG ============
        if (details == null || details.isEmpty()) {
            System.err.println("❌ [ExportModel] Giỏ hàng trống, không thể tạo phiếu xuất!");
            return false;
        }
        // ================================================

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra tồn kho đủ không
            for (Map<String, Object> detail : details) {
                if (!checkStock((int) detail.get("product_id"), (int) detail.get("quantity"))) {
                    JOptionPane.showMessageDialog(null, "Sản phẩm không đủ tồn kho để xuất!");
                    return false;
                }
            }

            // 2. Thêm phiếu xuất
            String receiptSql = "INSERT INTO export_receipts(receipt_code, export_date, customer_name, total_amount, user_id, note) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement receiptStmt = conn.prepareStatement(receiptSql, Statement.RETURN_GENERATED_KEYS);
            receiptStmt.setString(1, (String) receipt.get("receipt_code"));
            receiptStmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            receiptStmt.setString(3, (String) receipt.get("customer_name"));
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

            // 3. Thêm chi tiết và cập nhật tồn kho
            String detailSql = "INSERT INTO export_details(receipt_id, product_id, quantity, unit_price) VALUES(?, ?, ?, ?)";
            PreparedStatement detailStmt = conn.prepareStatement(detailSql);

            String updateProductSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateProductSql);

            for (Map<String, Object> detail : details) {
                detailStmt.setInt(1, receiptId);
                detailStmt.setInt(2, (int) detail.get("product_id"));
                detailStmt.setInt(3, (int) detail.get("quantity"));
                detailStmt.setDouble(4, (double) detail.get("sell_price"));
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

    // Kiểm tra tồn kho
    private boolean checkStock(int productId, int requestedQuantity) {
        String sql = "SELECT quantity FROM products WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("quantity");
                rs.close();
                pstmt.close();
                conn.close();
                return currentStock >= requestedQuantity;
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}