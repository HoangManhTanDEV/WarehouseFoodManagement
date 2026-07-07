package com.warehouse.model;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupplierModelTest {

    private static SupplierModel supplierModel;

    @BeforeAll
    static void setUp() {
        supplierModel = new SupplierModel();
        System.out.println("✅ Bắt đầu kiểm thử SupplierModel");
    }

    @Test
    @Order(1)
    @DisplayName("TC01: Lấy danh sách nhà cung cấp")
    void testGetAllSuppliers() {
        List<Map<String, Object>> suppliers = supplierModel.getAllSuppliers();
        assertNotNull(suppliers, "Danh sách nhà cung cấp không được null");
        assertTrue(suppliers.size() > 0, "Phải có ít nhất 1 nhà cung cấp");
        System.out.println("✅ Có " + suppliers.size() + " nhà cung cấp");
    }

    @Test
    @Order(2)
    @DisplayName("TC02: Tạo mã nhà cung cấp tự động")
    void testGenerateSupplierCode() {
        String code = supplierModel.generateSupplierCode();
        assertNotNull(code, "Mã nhà cung cấp không được null");
        assertTrue(code.startsWith("NCC"), "Mã phải bắt đầu bằng NCC");
        System.out.println("✅ Mã NCC mới: " + code);
    }

    @Test
    @Order(3)
    @DisplayName("TC03: Thêm nhà cung cấp mới thành công")
    void testAddSupplier() {
        Map<String, Object> supplier = new java.util.HashMap<>();
        supplier.put("code", supplierModel.generateSupplierCode());
        supplier.put("name", "Nhà cung cấp kiểm thử");
        supplier.put("phone", "0123456789");
        supplier.put("email", "test@supplier.com");
        supplier.put("address", "Địa chỉ kiểm thử");

        boolean result = supplierModel.addSupplier(supplier);
        assertTrue(result, "Thêm nhà cung cấp phải thành công");
        System.out.println("✅ Thêm nhà cung cấp thành công");
    }

    @Test
    @Order(4)
    @DisplayName("TC04: Kiểm tra mã NCC đã tồn tại")
    void testIsCodeExists() {
        // Lấy mã NCC đầu tiên trong danh sách
        List<Map<String, Object>> suppliers = supplierModel.getAllSuppliers();
        if (!suppliers.isEmpty()) {
            String code = (String) suppliers.get(0).get("code");
            boolean exists = supplierModel.isCodeExists(code);
            assertTrue(exists, "Mã NCC phải tồn tại");
            System.out.println("✅ Mã NCC " + code + " tồn tại");
        } else {
            fail("Không có nhà cung cấp để kiểm tra");
        }
    }

    @Test
    @Order(5)
    @DisplayName("TC05: Tìm kiếm nhà cung cấp")
    void testSearchSuppliers() {
        List<Map<String, Object>> results = supplierModel.searchSuppliers("kiểm thử");
        assertNotNull(results);
        assertTrue(results.size() > 0, "Phải tìm thấy nhà cung cấp vừa thêm");
        System.out.println("✅ Tìm thấy " + results.size() + " nhà cung cấp");
    }

    @Test
    @Order(6)
    @DisplayName("TC06: Xóa nhà cung cấp kiểm thử")
    void testDeleteSupplier() {
        List<Map<String, Object>> suppliers = supplierModel.getAllSuppliers();
        for (Map<String, Object> s : suppliers) {
            if ("Nhà cung cấp kiểm thử".equals(s.get("name"))) {
                int id = (int) s.get("id");
                boolean result = supplierModel.deleteSupplier(id);
                assertTrue(result, "Xóa nhà cung cấp phải thành công");
                System.out.println("✅ Xóa nhà cung cấp test thành công");
                return;
            }
        }
        fail("Không tìm thấy nhà cung cấp để xóa");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("✅ Kết thúc kiểm thử SupplierModel");
    }
}