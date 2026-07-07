package com.warehouse.test;

import com.warehouse.model.*;
import com.warehouse.utils.DatabaseConnection;
import com.warehouse.utils.EmailSender;
import java.sql.*;
import java.util.*;

public class SystemTest {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              BẮT ĐẦU KIỂM THỬ HỆ THỐNG                      ║");
        System.out.println("║         QUẢN LÝ KHO THỰC PHẨM - JAVA SWING MVC              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        testDatabaseConnection();
        testLogin();
        testProductManagement();
        testSupplierManagement();
        testUserManagement();
        testImportReceipt();
        testExportReceipt();
        testInventory();
        testStatistics();
        testEmailSender();

        printSummary();
    }

    private static void printResult(String testName, boolean result, String message) {
        if (result) {
            passedTests++;
            System.out.println("   ✅ " + testName + ": " + message);
        } else {
            failedTests++;
            System.out.println("   ❌ " + testName + ": " + message);
        }
    }

    private static void testDatabaseConnection() {
        System.out.println("📌 TEST 1: KẾT NỐI DATABASE");
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean result = (conn != null && !conn.isClosed());
            printResult("Kết nối MySQL", result, result ? "Kết nối thành công!" : "Kết nối thất bại!");
        } catch (SQLException e) {
            printResult("Kết nối MySQL", false, "Lỗi: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testLogin() {
        System.out.println("📌 TEST 2: ĐĂNG NHẬP");
        UserModel userModel = new UserModel();

        Map<String, String> user = userModel.login("admin", "123");
        boolean loginSuccess = (user != null && user.get("role").equals("admin"));
        printResult("Đăng nhập đúng (admin/123)", loginSuccess,
                loginSuccess ? "Đăng nhập thành công với vai trò Admin" : "Đăng nhập thất bại!");

        user = userModel.login("admin", "wrongpassword");
        printResult("Đăng nhập sai mật khẩu", user == null, "Hệ thống từ chối đăng nhập sai!");

        user = userModel.login("nonexist", "123");
        printResult("Đăng nhập tài khoản không tồn tại", user == null, "Hệ thống từ chối tài khoản không tồn tại!");

        System.out.println();
    }

    private static void testProductManagement() {
        System.out.println("📌 TEST 3: QUẢN LÝ SẢN PHẨM");
        ProductModel productModel = new ProductModel();

        // Lấy supplier_id hợp lệ từ database
        List<Map<String, Object>> suppliers = new SupplierModel().getAllSuppliers();
        int validSupplierId = 16; // Mặc định, nhưng sẽ tìm ID thực tế
        for (Map<String, Object> s : suppliers) {
            validSupplierId = (int) s.get("id");
            break;
        }

        // Kiểm tra sản phẩm đã tồn tại chưa
        String testCode = "TEST_P001";
        boolean exists = false;
        for (Map<String, Object> p : productModel.getAllProducts()) {
            if (testCode.equals(p.get("code"))) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            Map<String, Object> product = new HashMap<>();
            product.put("code", testCode);
            product.put("name", "Sản phẩm kiểm thử");
            product.put("category", "Kiểm thử");
            product.put("unit", "cái");
            product.put("quantity", 100);
            product.put("import_price", 10000.0);
            product.put("sell_price", 15000.0);
            product.put("supplier_id", validSupplierId); // Dùng supplier_id hợp lệ
            product.put("expiry_date", null);

            boolean added = productModel.addProduct(product);
            printResult("Thêm sản phẩm mới", added, added ? "Thêm thành công!" : "Thêm thất bại!");
        } else {
            printResult("Thêm sản phẩm mới", true, "Sản phẩm đã tồn tại, bỏ qua thêm mới");
        }

        List<Map<String, Object>> products = productModel.getAllProducts();
        printResult("Lấy danh sách sản phẩm", products != null && products.size() > 0,
                "Tổng số sản phẩm: " + products.size());

        // Tìm kiếm với từ khóa có thật (dùng tên sản phẩm đầu tiên)
        String searchKeyword = "Cà chua"; // Từ khóa có trong dữ liệu thật
        List<Map<String, Object>> searchResult = productModel.searchProducts(searchKeyword);
        printResult("Tìm kiếm sản phẩm", searchResult.size() > 0,
                "Tìm thấy " + searchResult.size() + " sản phẩm với từ khóa '" + searchKeyword + "'");

        System.out.println();
    }

    private static void testSupplierManagement() {
        System.out.println("📌 TEST 4: QUẢN LÝ NHÀ CUNG CẤP");
        SupplierModel supplierModel = new SupplierModel();

        String testCode = "TEST_NCC01";
        boolean exists = false;
        for (Map<String, Object> s : supplierModel.getAllSuppliers()) {
            if (testCode.equals(s.get("code"))) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            Map<String, Object> supplier = new HashMap<>();
            supplier.put("code", testCode);
            supplier.put("name", "Nhà cung cấp kiểm thử");
            supplier.put("phone", "0987654321");
            supplier.put("email", "test@supplier.com");
            supplier.put("address", "Địa chỉ kiểm thử");

            boolean added = supplierModel.addSupplier(supplier);
            printResult("Thêm nhà cung cấp", added, added ? "Thêm thành công!" : "Thêm thất bại!");
        } else {
            printResult("Thêm nhà cung cấp", true, "Nhà cung cấp đã tồn tại, bỏ qua thêm mới");
        }

        List<Map<String, Object>> suppliers = supplierModel.getAllSuppliers();
        printResult("Lấy danh sách nhà cung cấp", suppliers != null && suppliers.size() > 0,
                "Tổng số NCC: " + suppliers.size());

        List<Map<String, Object>> searchResult = supplierModel.searchSuppliers("An Việt");
        printResult("Tìm kiếm nhà cung cấp", searchResult.size() > 0,
                "Tìm thấy " + searchResult.size() + " NCC");

        System.out.println();
    }

    private static void testUserManagement() {
        System.out.println("📌 TEST 5: QUẢN LÝ TÀI KHOẢN");
        UserModel userModel = new UserModel();

        String testUsername = "testuser";
        boolean exists = false;
        for (Map<String, Object> u : userModel.getAllUsers()) {
            if (testUsername.equals(u.get("username"))) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("username", testUsername);
            newUser.put("password", "123456");
            newUser.put("fullname", "Người dùng kiểm thử");
            newUser.put("email", "test@user.com");
            newUser.put("role", "staff");

            boolean added = userModel.addUser(newUser);
            printResult("Thêm tài khoản mới", added, added ? "Thêm thành công!" : "Thêm thất bại!");
        } else {
            printResult("Thêm tài khoản mới", true, "Tài khoản đã tồn tại, bỏ qua thêm mới");
        }

        List<Map<String, Object>> users = userModel.getAllUsers();
        printResult("Lấy danh sách tài khoản", users != null && users.size() > 0,
                "Tổng số tài khoản: " + users.size());

        if (users.size() > 0) {
            Map<String, Object> updateUser = users.get(0);
            updateUser.put("fullname", "Đã cập nhật");
            boolean updated = userModel.updateUser(updateUser);
            printResult("Cập nhật tài khoản", updated, updated ? "Cập nhật thành công!" : "Cập nhật thất bại!");
        }

        System.out.println();
    }

    private static void testImportReceipt() {
        System.out.println("📌 TEST 6: NHẬP HÀNG");
        ImportModel importModel = new ImportModel();

        String receiptCode = importModel.generateReceiptCode();
        printResult("Tạo mã phiếu nhập", receiptCode != null && !receiptCode.isEmpty(),
                "Mã phiếu: " + receiptCode);

        List<Map<String, Object>> products = importModel.getAllProducts();
        printResult("Lấy danh sách sản phẩm nhập", products != null,
                "Có " + (products != null ? products.size() : 0) + " sản phẩm có thể nhập");

        List<Map<String, Object>> suppliers = importModel.getAllSuppliers();
        printResult("Lấy danh sách nhà cung cấp", suppliers != null,
                "Có " + (suppliers != null ? suppliers.size() : 0) + " nhà cung cấp");

        System.out.println();
    }

    private static void testExportReceipt() {
        System.out.println("📌 TEST 7: XUẤT HÀNG");
        ExportModel exportModel = new ExportModel();

        String receiptCode = exportModel.generateReceiptCode();
        printResult("Tạo mã phiếu xuất", receiptCode != null && !receiptCode.isEmpty(),
                "Mã phiếu: " + receiptCode);

        List<Map<String, Object>> products = exportModel.getAvailableProducts();
        printResult("Lấy sản phẩm có tồn kho", products != null,
                "Có " + (products != null ? products.size() : 0) + " sản phẩm có tồn kho");

        System.out.println();
    }

    private static void testInventory() {
        System.out.println("📌 TEST 8: TỒN KHO");
        InventoryModel inventoryModel = new InventoryModel();

        Map<String, Double> summary = inventoryModel.getInventorySummary();
        boolean hasSummary = summary != null && !summary.isEmpty();
        printResult("Lấy tổng giá trị tồn kho", hasSummary,
                hasSummary ? String.format("Tổng giá trị: %,.0f đ", summary.getOrDefault("total_value", 0.0)) : "Lấy thất bại!");

        List<Map<String, Object>> lowStock = inventoryModel.getLowStockProducts(10);
        printResult("Lấy hàng tồn kho thấp (≤10)", lowStock != null,
                "Có " + (lowStock != null ? lowStock.size() : 0) + " sản phẩm tồn thấp");

        List<Map<String, Object>> expiring = inventoryModel.getExpiringProducts();
        printResult("Lấy hàng sắp hết hạn", expiring != null,
                "Có " + (expiring != null ? expiring.size() : 0) + " sản phẩm sắp hết hạn");

        List<Map<String, Object>> allInventory = inventoryModel.getAllInventory();
        printResult("Lấy tất cả tồn kho", allInventory != null,
                "Tổng số sản phẩm: " + (allInventory != null ? allInventory.size() : 0));

        System.out.println();
    }

    private static void testStatistics() {
        System.out.println("📌 TEST 9: THỐNG KÊ");
        StatisticsModel statisticsModel = new StatisticsModel();

        List<Integer> years = statisticsModel.getAvailableYears();
        printResult("Lấy danh sách năm có dữ liệu", years != null && !years.isEmpty(),
                "Các năm: " + (years != null ? years.toString() : "[]"));

        java.sql.Date startDate = new java.sql.Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        java.sql.Date endDate = new java.sql.Date(System.currentTimeMillis());

        Map<String, Object> importStats = statisticsModel.getImportStatistics(startDate, endDate);
        printResult("Thống kê nhập hàng 30 ngày", importStats != null,
                importStats != null ? "Tổng nhập: " + String.format("%,.0f đ", importStats.getOrDefault("total_value", 0.0)) : "Lấy thất bại!");

        Map<String, Object> exportStats = statisticsModel.getExportStatistics(startDate, endDate);
        printResult("Thống kê xuất hàng 30 ngày", exportStats != null,
                exportStats != null ? "Tổng xuất: " + String.format("%,.0f đ", exportStats.getOrDefault("total_revenue", 0.0)) : "Lấy thất bại!");

        System.out.println();
    }

    private static void testEmailSender() {
        System.out.println("📌 TEST 10: GỬI EMAIL");

        try {
            EmailSender.sendEmail("test@example.com", "[TEST] Kiểm thử email",
                    "<h1>Kiểm thử</h1><p>Đây là email kiểm thử từ hệ thống QL Kho</p>");
            printResult("Gửi email thông báo", true, "Đã gửi yêu cầu email (kiểm tra console/log)");
        } catch (Exception e) {
            printResult("Gửi email thông báo", false, "Lỗi: " + e.getMessage());
        }

        System.out.println();
    }

    private static void printSummary() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    KẾT QUẢ KIỂM THỬ                          ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  ✅ PASSED: %-50d ║\n", passedTests);
        System.out.printf("║  ❌ FAILED: %-50d ║\n", failedTests);
        System.out.printf("║  📊 TOTAL:  %-50d ║\n", passedTests + failedTests);
        System.out.printf("║  🎯 RATE:   %-50.1f%% ║\n", (passedTests * 100.0 / (passedTests + failedTests)));
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        if (failedTests == 0) {
            System.out.println("\n🎉 CHÚC MỪNG! TẤT CẢ CÁC TEST ĐỀU PASS! 🎉");
        } else {
            System.out.println("\n⚠️ CÓ " + failedTests + " TEST BỊ LỖI, CẦN KIỂM TRA LẠI! ⚠️");
        }
    }
}