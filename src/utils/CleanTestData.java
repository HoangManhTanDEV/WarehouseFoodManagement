package com.warehouse.utils;

import java.sql.*;

public class CleanTestData {

    public static void main(String[] args) {
        System.out.println("🗑️ BẮT ĐẦU DỌN DẸP DỮ LIỆU TEST...\n");

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Xóa sản phẩm test
            String deleteProducts = "DELETE FROM products WHERE code LIKE 'TEST_%'";
            int productsDeleted = conn.createStatement().executeUpdate(deleteProducts);
            System.out.println("✅ Đã xóa " + productsDeleted + " sản phẩm test");

            // Xóa nhà cung cấp test
            String deleteSuppliers = "DELETE FROM suppliers WHERE code LIKE 'TEST_%'";
            int suppliersDeleted = conn.createStatement().executeUpdate(deleteSuppliers);
            System.out.println("✅ Đã xóa " + suppliersDeleted + " nhà cung cấp test");

            // Xóa tài khoản test
            String deleteUsers = "DELETE FROM users WHERE username = 'testuser'";
            int usersDeleted = conn.createStatement().executeUpdate(deleteUsers);
            System.out.println("✅ Đã xóa " + usersDeleted + " tài khoản test");

            // Xóa chi tiết phiếu nhập test trước
            String deleteImportDetails = "DELETE FROM import_details WHERE receipt_id IN (SELECT id FROM import_receipts WHERE receipt_code LIKE 'TEST_%')";
            conn.createStatement().executeUpdate(deleteImportDetails);

            // Xóa phiếu nhập test
            String deleteImportReceipts = "DELETE FROM import_receipts WHERE receipt_code LIKE 'TEST_%'";
            int importDeleted = conn.createStatement().executeUpdate(deleteImportReceipts);
            System.out.println("✅ Đã xóa " + importDeleted + " phiếu nhập test");

            // Xóa chi tiết phiếu xuất test trước
            String deleteExportDetails = "DELETE FROM export_details WHERE receipt_id IN (SELECT id FROM export_receipts WHERE receipt_code LIKE 'TEST_%')";
            conn.createStatement().executeUpdate(deleteExportDetails);

            // Xóa phiếu xuất test
            String deleteExportReceipts = "DELETE FROM export_receipts WHERE receipt_code LIKE 'TEST_%'";
            int exportDeleted = conn.createStatement().executeUpdate(deleteExportReceipts);
            System.out.println("✅ Đã xóa " + exportDeleted + " phiếu xuất test");

            System.out.println("\n🎉 DỌN DẸP HOÀN TẤT! 🎉");

        } catch (SQLException e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}