package com.warehouse.view;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductDialogTest {

    private ProductDialog dialog;

    @BeforeEach
    void setUp() {
        dialog = new ProductDialog(null, "Thêm sản phẩm", false);
        dialog.setVisible(false);
    }

    @AfterEach
    void tearDown() {
        dialog.dispose();
    }

    @Test
    @DisplayName("TC_V_28: Kiểm tra ProductDialog khởi tạo thành công")
    void testDialogCreated() {
        assertNotNull(dialog, "ProductDialog không được null");
        assertEquals("Thêm sản phẩm", dialog.getTitle(), "Tiêu đề phải là 'Thêm sản phẩm'");
    }

    @Test
    @DisplayName("TC_V_29: Kiểm tra các button tồn tại")
    void testButtonsExist() {
        assertNotNull(dialog.getSaveButton(), "Save button không được null");
        assertNotNull(dialog.getCancelButton(), "Cancel button không được null");
    }

    @Test
    @DisplayName("TC_V_30: Kiểm tra validateInput() với dữ liệu hợp lệ")
    void testValidateInputValid() {
        setFieldValue("codeField", "TEST001");
        setFieldValue("nameField", "Sản phẩm test");
        setFieldValue("quantityField", "10");
        setFieldValue("importPriceField", "10000");
        setFieldValue("sellPriceField", "15000");

        assertTrue(dialog.validateInput(), "validateInput() phải trả về true với dữ liệu hợp lệ");
    }

    @Test
    @DisplayName("TC_V_31: Kiểm tra validateInput() với mã trống")
    void testValidateInputEmptyCode() {
        setFieldValue("codeField", "");
        setFieldValue("nameField", "Sản phẩm test");
        setFieldValue("quantityField", "10");
        setFieldValue("importPriceField", "10000");
        setFieldValue("sellPriceField", "15000");

        assertFalse(dialog.validateInput(), "validateInput() phải trả về false khi mã trống");
    }

    @Test
    @DisplayName("TC_V_32: Kiểm tra validateInput() với tên trống")
    void testValidateInputEmptyName() {
        setFieldValue("codeField", "TEST001");
        setFieldValue("nameField", "");
        setFieldValue("quantityField", "10");
        setFieldValue("importPriceField", "10000");
        setFieldValue("sellPriceField", "15000");

        assertFalse(dialog.validateInput(), "validateInput() phải trả về false khi tên trống");
    }

    @Test
    @DisplayName("TC_V_33: Kiểm tra validateInput() với số lượng không hợp lệ")
    void testValidateInputInvalidQuantity() {
        setFieldValue("codeField", "TEST001");
        setFieldValue("nameField", "Sản phẩm test");
        setFieldValue("quantityField", "abc");
        setFieldValue("importPriceField", "10000");
        setFieldValue("sellPriceField", "15000");

        assertFalse(dialog.validateInput(), "validateInput() phải trả về false khi số lượng không phải số");
    }

    @Test
    @DisplayName("TC_V_34: Kiểm tra setProductData() hoạt động")
    void testSetProductData() {
        Map<String, Object> product = new HashMap<>();
        product.put("id", 1);
        product.put("code", "TEST001");
        product.put("name", "Sản phẩm test");
        product.put("category", "Kiểm thử");
        product.put("unit", "cái");
        product.put("quantity", 10);
        product.put("import_price", 10000.0);
        product.put("sell_price", 15000.0);
        product.put("supplier_id", 1);

        // setProductData không ném ngoại lệ
        assertDoesNotThrow(() -> dialog.setProductData(product), "setProductData() không được ném ngoại lệ");
    }

    @Test
    @DisplayName("TC_V_35: Kiểm tra getProductData() trả về dữ liệu")
    void testGetProductData() {
        setFieldValue("codeField", "TEST001");
        setFieldValue("nameField", "Sản phẩm test");
        setFieldValue("categoryField", "Kiểm thử");
        setFieldValue("unitField", "cái");
        setFieldValue("quantityField", "10");
        setFieldValue("importPriceField", "10000");
        setFieldValue("sellPriceField", "15000");

        Map<String, Object> data = dialog.getProductData();

        assertNotNull(data, "getProductData() không được null");
        assertEquals("TEST001", data.get("code"), "Mã sản phẩm phải là 'TEST001'");
        assertEquals("Sản phẩm test", data.get("name"), "Tên sản phẩm phải là 'Sản phẩm test'");
        assertEquals(10, data.get("quantity"), "Số lượng phải là 10");
        assertEquals(10000.0, data.get("import_price"), "Giá nhập phải là 10000");
        assertEquals(15000.0, data.get("sell_price"), "Giá bán phải là 15000");
    }

    // Helper method để set giá trị cho field private
    private void setFieldValue(String fieldName, String value) {
        try {
            Field field = ProductDialog.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            JTextField textField = (JTextField) field.get(dialog);
            textField.setText(value);
        } catch (Exception e) {
            fail("Không thể set field " + fieldName + ": " + e.getMessage());
        }
    }
}