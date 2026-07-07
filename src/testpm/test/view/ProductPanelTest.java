package com.warehouse.view;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductPanelTest {

    private ProductPanel productPanel;

    @BeforeEach
    void setUp() {
        productPanel = new ProductPanel();
    }

    @Test
    @DisplayName("TC_V_23: Kiểm tra ProductPanel khởi tạo thành công")
    void testProductPanelCreated() {
        assertNotNull(productPanel, "ProductPanel không được null");
        assertTrue(productPanel.getComponentCount() > 0, "ProductPanel phải có các component con");
    }

    @Test
    @DisplayName("TC_V_24: Kiểm tra các button tồn tại")
    void testButtonsExist() {
        assertNotNull(productPanel.getAddButton(), "Add button không được null");
        assertNotNull(productPanel.getEditButton(), "Edit button không được null");
        assertNotNull(productPanel.getDeleteButton(), "Delete button không được null");
        assertNotNull(productPanel.getRefreshButton(), "Refresh button không được null");
        assertNotNull(productPanel.getSearchButton(), "Search button không được null");
    }

    @Test
    @DisplayName("TC_V_25: Kiểm tra search field hoạt động")
    void testSearchField() {
        // Kiểm tra getSearchKeyword() trả về đúng giá trị
        String keyword = "test product";
        // Sử dụng reflection để set text cho searchField
        try {
            java.lang.reflect.Field field = ProductPanel.class.getDeclaredField("searchField");
            field.setAccessible(true);
            JTextField searchField = (JTextField) field.get(productPanel);
            searchField.setText(keyword);

            assertEquals(keyword, productPanel.getSearchKeyword(), "getSearchKeyword() phải trả về giá trị đã set");
        } catch (Exception e) {
            fail("Không thể truy cập searchField: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("TC_V_26: Kiểm tra refreshTable không bị lỗi")
    void testRefreshTable() {
        // refreshTable nhận List<Map>, kiểm tra không ném ngoại lệ
        assertDoesNotThrow(() -> productPanel.refreshTable(new java.util.ArrayList<>()),
                "refreshTable() không được ném ngoại lệ");
    }

    @Test
    @DisplayName("TC_V_27: Kiểm tra getSelectedProductId() trả về -1 khi không chọn")
    void testGetSelectedProductIdNoSelection() {
        assertEquals(-1, productPanel.getSelectedProductId(),
                "Khi không chọn sản phẩm, getSelectedProductId() phải trả về -1");
    }
}