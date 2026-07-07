package com.warehouse.view;

import com.warehouse.model.*;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DashboardPanelTest {

    private DashboardPanel dashboardPanel;

    @BeforeEach
    void setUp() {
        dashboardPanel = new DashboardPanel();
    }

    @Test
    @DisplayName("TC_V_14: Kiểm tra DashboardPanel khởi tạo thành công")
    void testDashboardPanelCreated() {
        assertNotNull(dashboardPanel, "DashboardPanel không được null");
        assertTrue(dashboardPanel.getComponentCount() > 0, "DashboardPanel phải có các component con");
    }

    @Test
    @DisplayName("TC_V_15: Kiểm tra refresh() không bị lỗi")
    void testRefresh() {
        assertDoesNotThrow(() -> dashboardPanel.refresh(), "refresh() không được ném ngoại lệ");
    }

    @Test
    @DisplayName("TC_V_16: Kiểm tra các label tồn tại và có giá trị")
    void testLabelsExistAndHaveValues() {
        dashboardPanel.refresh();

        JLabel[] labels = {
                dashboardPanel.getTotalProductsLabel(),
                dashboardPanel.getTotalSuppliersLabel(),
                dashboardPanel.getTotalUsersLabel(),
                dashboardPanel.getTotalValueLabel(),
                dashboardPanel.getLowStockLabel(),
                dashboardPanel.getExpiringLabel()
        };

        for (JLabel label : labels) {
            assertNotNull(label, "Label không được null");
            assertFalse(label.getText().isEmpty(), "Label không được rỗng");
        }
    }

    @Test
    @DisplayName("TC_V_17: Kiểm tra các textarea tồn tại và có nội dung")
    void testTextAreasExistAndHaveContent() {
        dashboardPanel.refresh();

        JTextArea lowStockArea = dashboardPanel.getLowStockArea();
        assertNotNull(lowStockArea, "lowStockArea không được null");
        assertFalse(lowStockArea.getText().isEmpty(), "lowStockArea không được rỗng");

        JTextArea recentActivityArea = dashboardPanel.getRecentActivityArea();
        assertNotNull(recentActivityArea, "recentActivityArea không được null");
        assertFalse(recentActivityArea.getText().isEmpty(), "recentActivityArea không được rỗng");
    }

    @Test
    @DisplayName("TC_V_18: Kiểm tra panel background không bị null")
    void testPanelBackground() {
        // Chỉ kiểm tra background không null, không kiểm tra màu cụ thể
        assertNotNull(dashboardPanel.getBackground(), "Panel background không được null");
        // Màu nền có thể khác nhau tùy theo Look and Feel, không kiểm tra cụ thể
    }

    @Test
    @DisplayName("TC_V_19: Kiểm tra lowStockArea hiển thị đúng thông tin")
    void testLowStockAreaContent() {
        dashboardPanel.refresh();

        JTextArea lowStockArea = dashboardPanel.getLowStockArea();
        String content = lowStockArea.getText();

        assertNotNull(content);
        assertTrue(content.contains("Mã SP") || content.contains("Không có"),
                "lowStockArea phải chứa thông tin về hàng tồn thấp");
    }

    @Test
    @DisplayName("TC_V_20: Kiểm tra recentActivityArea hiển thị đúng thông tin")
    void testRecentActivityAreaContent() {
        dashboardPanel.refresh();

        JTextArea recentActivityArea = dashboardPanel.getRecentActivityArea();
        String content = recentActivityArea.getText();

        assertNotNull(content);
        assertTrue(content.contains("THÔNG TIN HỆ THỐNG") || content.contains("CẢNH BÁO"),
                "recentActivityArea phải chứa thông tin hệ thống");
    }

    @Test
    @DisplayName("TC_V_21: Kiểm tra lowStockLabel hiển thị số lượng chính xác")
    void testLowStockLabelValue() {
        dashboardPanel.refresh();

        // Lấy dữ liệu thực tế để so sánh
        InventoryModel inventoryModel = new InventoryModel();
        int expectedLowStock = inventoryModel.getLowStockProducts(10).size();

        String actualText = dashboardPanel.getLowStockLabel().getText();
        int actualValue = Integer.parseInt(actualText);

        assertEquals(expectedLowStock, actualValue, "Số lượng hàng tồn thấp phải khớp với dữ liệu");
    }

    @Test
    @DisplayName("TC_V_22: Kiểm tra tổng số sản phẩm hiển thị đúng")
    void testTotalProductsLabelValue() {
        dashboardPanel.refresh();

        ProductModel productModel = new ProductModel();
        int expectedCount = productModel.getAllProducts().size();

        String actualText = dashboardPanel.getTotalProductsLabel().getText();
        int actualValue = Integer.parseInt(actualText);

        assertEquals(expectedCount, actualValue, "Tổng số sản phẩm phải khớp với dữ liệu");
    }
}