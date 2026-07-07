package com.warehouse.model;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImportModelTest {

    private static ImportModel importModel;

    @BeforeAll
    static void setUp() {
        importModel = new ImportModel();
        System.out.println("✅ Bắt đầu kiểm thử ImportModel");
    }

    @Test
    @Order(1)
    @DisplayName("TC01: Lấy danh sách sản phẩm để nhập")
    void testGetAllProducts() {
        List<Map<String, Object>> products = importModel.getAllProducts();
        assertNotNull(products, "Danh sách sản phẩm không được null");
        assertTrue(products.size() > 0, "Phải có ít nhất 1 sản phẩm");
        System.out.println("✅ Có " + products.size() + " sản phẩm có thể nhập");
    }

    @Test
    @Order(2)
    @DisplayName("TC02: Lấy danh sách nhà cung cấp")
    void testGetAllSuppliers() {
        List<Map<String, Object>> suppliers = importModel.getAllSuppliers();
        assertNotNull(suppliers, "Danh sách nhà cung cấp không được null");
        assertTrue(suppliers.size() > 0, "Phải có ít nhất 1 nhà cung cấp");
        System.out.println("✅ Có " + suppliers.size() + " nhà cung cấp");
    }

    @Test
    @Order(3)
    @DisplayName("TC03: Tạo mã phiếu nhập tự động")
    void testGenerateReceiptCode() {
        String code = importModel.generateReceiptCode();
        assertNotNull(code, "Mã phiếu nhập không được null");
        assertTrue(code.startsWith("PN"), "Mã phải bắt đầu bằng PN");
        System.out.println("✅ Mã phiếu nhập mới: " + code);
    }

    @Test
    @Order(4)
    @DisplayName("TC04: Tạo phiếu nhập mới thành công")
    void testCreateImportReceipt() {
        // Chuẩn bị dữ liệu phiếu nhập
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", importModel.generateReceiptCode());
        receipt.put("import_date", new java.sql.Date(System.currentTimeMillis()));
        receipt.put("supplier_id", 1); // NCC001
        receipt.put("total_amount", 1000000.0);
        receipt.put("user_id", 1); // admin
        receipt.put("note", "Phiếu nhập kiểm thử");

        // Chuẩn bị chi tiết phiếu nhập
        List<Map<String, Object>> details = new ArrayList<>();

        // Lấy sản phẩm đầu tiên để nhập
        List<Map<String, Object>> products = importModel.getAllProducts();
        if (!products.isEmpty()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("product_id", products.get(0).get("id"));
            detail.put("quantity", 10);
            detail.put("unit_price", products.get(0).get("import_price"));
            details.add(detail);
        } else {
            fail("Không có sản phẩm để nhập");
        }

        boolean result = importModel.createImportReceipt(receipt, details);
        assertTrue(result, "Tạo phiếu nhập phải thành công");
        System.out.println("✅ Tạo phiếu nhập thành công");
    }

    @Test
    @Order(5)
    @DisplayName("TC05: Tạo phiếu nhập với giỏ hàng trống")
    void testCreateImportReceiptEmptyCart() {
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", importModel.generateReceiptCode());
        receipt.put("import_date", new java.sql.Date(System.currentTimeMillis()));
        receipt.put("supplier_id", 1);
        receipt.put("total_amount", 0.0);
        receipt.put("user_id", 1);
        receipt.put("note", "Phiếu nhập trống");

        List<Map<String, Object>> emptyDetails = new ArrayList<>();

        boolean result = importModel.createImportReceipt(receipt, emptyDetails);
        assertFalse(result, "Tạo phiếu nhập với giỏ trống phải thất bại");
        System.out.println("✅ Hệ thống từ chối phiếu nhập trống");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("✅ Kết thúc kiểm thử ImportModel");
    }
}