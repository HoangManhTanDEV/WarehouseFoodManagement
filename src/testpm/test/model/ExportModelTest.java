package com.warehouse.model;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExportModelTest {

    private static ExportModel exportModel;

    @BeforeAll
    static void setUp() {
        exportModel = new ExportModel();
        System.out.println("✅ Bắt đầu kiểm thử ExportModel");
    }

    @Test
    @Order(1)
    @DisplayName("TC01: Lấy danh sách sản phẩm có tồn kho")
    void testGetAvailableProducts() {
        List<Map<String, Object>> products = exportModel.getAvailableProducts();
        assertNotNull(products, "Danh sách sản phẩm không được null");
        System.out.println("✅ Có " + products.size() + " sản phẩm có tồn kho");
    }

    @Test
    @Order(2)
    @DisplayName("TC02: Tạo mã phiếu xuất tự động")
    void testGenerateReceiptCode() {
        String code = exportModel.generateReceiptCode();
        assertNotNull(code, "Mã phiếu xuất không được null");
        assertTrue(code.startsWith("PX"), "Mã phải bắt đầu bằng PX");
        System.out.println("✅ Mã phiếu xuất mới: " + code);
    }

    @Test
    @Order(3)
    @DisplayName("TC03: Tạo phiếu xuất mới thành công")
    void testCreateExportReceipt() {
        // Chuẩn bị dữ liệu phiếu xuất
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", exportModel.generateReceiptCode());
        receipt.put("export_date", new java.sql.Date(System.currentTimeMillis()));
        receipt.put("customer_name", "Khách hàng kiểm thử");
        receipt.put("total_amount", 1000000.0);
        receipt.put("user_id", 1);
        receipt.put("note", "Phiếu xuất kiểm thử");

        // Chuẩn bị chi tiết phiếu xuất
        List<Map<String, Object>> details = new ArrayList<>();

        // Lấy sản phẩm đầu tiên có tồn kho để xuất
        List<Map<String, Object>> products = exportModel.getAvailableProducts();
        if (!products.isEmpty()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("product_id", products.get(0).get("id"));
            detail.put("quantity", 1); // Chỉ xuất 1 để không ảnh hưởng nhiều
            detail.put("sell_price", products.get(0).get("sell_price"));
            details.add(detail);
        } else {
            fail("Không có sản phẩm để xuất");
        }

        boolean result = exportModel.createExportReceipt(receipt, details);
        assertTrue(result, "Tạo phiếu xuất phải thành công");
        System.out.println("✅ Tạo phiếu xuất thành công");
    }

    @Test
    @Order(4)
    @DisplayName("TC04: Tạo phiếu xuất với giỏ hàng trống phải thất bại")
    void testCreateExportReceiptEmptyCart() {
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", exportModel.generateReceiptCode());
        receipt.put("export_date", new java.sql.Date(System.currentTimeMillis()));
        receipt.put("customer_name", "Khách hàng kiểm thử");
        receipt.put("total_amount", 0.0);
        receipt.put("user_id", 1);
        receipt.put("note", "Phiếu xuất trống");

        List<Map<String, Object>> emptyDetails = new ArrayList<>();

        boolean result = exportModel.createExportReceipt(receipt, emptyDetails);
        assertFalse(result, "Tạo phiếu xuất với giỏ trống phải thất bại");
        System.out.println("✅ Hệ thống từ chối phiếu xuất trống");
    }

    @Test
    @Order(5)
    @DisplayName("TC05: Xuất quá số lượng tồn kho phải thất bại")
    void testExportExceedStock() {
        // Lấy sản phẩm đầu tiên có tồn kho
        List<Map<String, Object>> products = exportModel.getAvailableProducts();
        if (products.isEmpty()) {
            fail("Không có sản phẩm để kiểm tra");
        }

        Map<String, Object> product = products.get(0);
        int currentStock = (int) product.get("quantity");

        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", exportModel.generateReceiptCode());
        receipt.put("export_date", new java.sql.Date(System.currentTimeMillis()));
        receipt.put("customer_name", "Khách hàng kiểm thử");
        receipt.put("total_amount", 10000000.0);
        receipt.put("user_id", 1);
        receipt.put("note", "Phiếu xuất vượt tồn");

        List<Map<String, Object>> details = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", product.get("id"));
        detail.put("quantity", currentStock + 100); // Xuất nhiều hơn tồn
        detail.put("sell_price", product.get("sell_price"));
        details.add(detail);

        boolean result = exportModel.createExportReceipt(receipt, details);
        assertFalse(result, "Xuất quá tồn kho phải thất bại");
        System.out.println("✅ Hệ thống từ chối xuất quá tồn kho");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("✅ Kết thúc kiểm thử ExportModel");
    }
}