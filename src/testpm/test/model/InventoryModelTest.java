package com.warehouse.model;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryModelTest {

    private static InventoryModel inventoryModel;

    @BeforeAll
    static void setUp() {
        inventoryModel = new InventoryModel();
        System.out.println("✅ Bắt đầu kiểm thử InventoryModel");
    }

    @Test
    @Order(1)
    @DisplayName("TC01: Lấy danh sách tồn kho")
    void testGetAllInventory() {
        List<Map<String, Object>> inventory = inventoryModel.getAllInventory();
        assertNotNull(inventory, "Danh sách tồn kho không được null");
        assertTrue(inventory.size() > 0, "Phải có ít nhất 1 sản phẩm trong kho");
        System.out.println("✅ Có " + inventory.size() + " sản phẩm trong kho");
    }

    @Test
    @Order(2)
    @DisplayName("TC02: Lấy hàng tồn thấp (≤ 10)")
    void testGetLowStockProducts() {
        int threshold = 10;
        List<Map<String, Object>> lowStock = inventoryModel.getLowStockProducts(threshold);
        assertNotNull(lowStock, "Danh sách hàng tồn thấp không được null");

        // Kiểm tra tất cả sản phẩm trong danh sách đều có số lượng ≤ threshold
        for (Map<String, Object> item : lowStock) {
            int quantity = (int) item.get("quantity");
            assertTrue(quantity <= threshold, "Sản phẩm " + item.get("code") + " có số lượng " + quantity + " > " + threshold);
        }
        System.out.println("✅ Có " + lowStock.size() + " sản phẩm tồn thấp (≤ " + threshold + ")");
    }

    @Test
    @Order(3)
    @DisplayName("TC03: Lấy hàng sắp hết hạn")
    void testGetExpiringProducts() {
        List<Map<String, Object>> expiring = inventoryModel.getExpiringProducts();
        assertNotNull(expiring, "Danh sách hàng sắp hết hạn không được null");
        System.out.println("✅ Có " + expiring.size() + " sản phẩm sắp hết hạn");

        // In chi tiết nếu có
        if (!expiring.isEmpty()) {
            for (Map<String, Object> item : expiring) {
                System.out.println("   - " + item.get("code") + ": còn " + item.get("days_left") + " ngày");
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("TC04: Lấy tổng giá trị tồn kho")
    void testGetInventorySummary() {
        Map<String, Double> summary = inventoryModel.getInventorySummary();
        assertNotNull(summary, "Thống kê tồn kho không được null");

        double totalValue = summary.getOrDefault("total_value", 0.0);
        double totalQuantity = summary.getOrDefault("total_quantity", 0.0);
        double totalProducts = summary.getOrDefault("total_products", 0.0);

        assertTrue(totalValue >= 0, "Tổng giá trị tồn kho không được âm");
        assertTrue(totalQuantity >= 0, "Tổng số lượng tồn kho không được âm");
        assertTrue(totalProducts >= 0, "Tổng số sản phẩm không được âm");

        System.out.println("✅ Tổng giá trị tồn kho: " + String.format("%,.0f đ", totalValue));
        System.out.println("✅ Tổng số lượng tồn: " + String.format("%.0f", totalQuantity));
        System.out.println("✅ Tổng số sản phẩm: " + String.format("%.0f", totalProducts));
    }

    @Test
    @Order(5)
    @DisplayName("TC05: Tìm kiếm trong tồn kho")
    void testSearchInventory() {
        // Tìm kiếm với từ khóa thường có trong dữ liệu
        List<Map<String, Object>> results = inventoryModel.searchInventory("cà");
        assertNotNull(results, "Kết quả tìm kiếm không được null");
        System.out.println("✅ Tìm thấy " + results.size() + " sản phẩm chứa từ khóa 'cà'");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("✅ Kết thúc kiểm thử InventoryModel");
    }
}