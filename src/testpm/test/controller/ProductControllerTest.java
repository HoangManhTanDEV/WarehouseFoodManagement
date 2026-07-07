package com.warehouse.controller;

import com.warehouse.model.ProductModel;
import com.warehouse.view.ProductPanel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JOptionPane;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductModel mockModel;

    @Mock
    private ProductPanel mockView;

    private ProductController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ProductController(mockView, mockModel, true);
    }

    // ==================== TEST 1: LOAD DỮ LIỆU ====================
    @Test
    @DisplayName("TC_C01: loadProductData() gọi getAllProducts() và refreshTable()")
    void testLoadProductData() {
        // 1. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockProducts = new ArrayList<>();
        Map<String, Object> product = new HashMap<>();
        product.put("id", 1);
        product.put("code", "TEST001");
        product.put("name", "Sản phẩm test");
        mockProducts.add(product);

        // 2. Giả lập Model
        when(mockModel.getAllProducts()).thenReturn(mockProducts);

        // 3. Gọi hàm cần test
        controller.loadProductData();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getAllProducts();
        verify(mockView, times(1)).refreshTable(mockProducts);
    }

    // ==================== TEST 2: TÌM KIẾM SẢN PHẨM ====================
    @Test
    @DisplayName("TC_C02: searchProduct() gọi searchProducts() khi có từ khóa")
    void testSearchProductWithKeyword() {
        // 1. Chuẩn bị dữ liệu
        String keyword = "test";
        List<Map<String, Object>> mockResults = new ArrayList<>();

        // 2. Giả lập
        when(mockView.getSearchKeyword()).thenReturn(keyword);
        when(mockModel.searchProducts(keyword)).thenReturn(mockResults);

        // 3. Gọi hàm
        controller.searchProduct();

        // 4. Kiểm tra
        verify(mockModel, times(1)).searchProducts(keyword);
        verify(mockView, times(1)).refreshTable(mockResults);
    }

    @Test
    @DisplayName("TC_C03: searchProduct() gọi getAllProducts() khi từ khóa rỗng")
    void testSearchProductWithEmptyKeyword() {
        // 1. Chuẩn bị dữ liệu
        List<Map<String, Object>> mockProducts = new ArrayList<>();

        // 2. Giả lập
        when(mockView.getSearchKeyword()).thenReturn("");
        when(mockModel.getAllProducts()).thenReturn(mockProducts);

        // 3. Gọi hàm
        controller.searchProduct();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getAllProducts();
        verify(mockModel, never()).searchProducts(anyString());
        verify(mockView, times(1)).refreshTable(mockProducts);
    }

    // ==================== TEST 3: XÓA SẢN PHẨM ====================
    // Cách 1: Dùng spyController được tạo bằng Mockito.spy()
    @Test
    @DisplayName("TC_C04: deleteProduct() xóa sản phẩm khi có ID hợp lệ và xác nhận YES")
    void testDeleteProductSuccess() {
        // 1. Tạo spy controller
        ProductController spyController = spy(controller);

        // 2. Giả lập View trả về ID = 1
        when(mockView.getSelectedProductId()).thenReturn(1);

        // 3. Giả lập Model.deleteProduct() trả về true
        when(mockModel.deleteProduct(1)).thenReturn(true);

        // 4. Bỏ qua JOptionPane - chọn YES
        doReturn(JOptionPane.YES_OPTION).when(spyController).showConfirmDialog(anyString());
        doNothing().when(spyController).showMessage(anyString());

        // 5. Gọi hàm
        spyController.deleteProduct();

        // 6. Kiểm tra
        verify(mockModel, times(1)).deleteProduct(1);
        verify(mockView, times(1)).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C05: deleteProduct() không xóa khi có ID hợp lệ nhưng chọn NO")
    void testDeleteProductCancel() {
        // 1. Tạo spy controller
        ProductController spyController = spy(controller);

        // 2. Giả lập View trả về ID = 1
        when(mockView.getSelectedProductId()).thenReturn(1);

        // 3. Bỏ qua JOptionPane - chọn NO
        doReturn(JOptionPane.NO_OPTION).when(spyController).showConfirmDialog(anyString());

        // 4. Gọi hàm
        spyController.deleteProduct();

        // 5. Kiểm tra: Model.deleteProduct() KHÔNG được gọi
        verify(mockModel, never()).deleteProduct(anyInt());
        verify(mockView, never()).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C06: deleteProduct() không xóa khi không chọn sản phẩm")
    void testDeleteProductNoSelection() {
        // 1. Tạo spy controller
        ProductController spyController = spy(controller);

        // 2. Giả lập View trả về ID = -1 (không chọn)
        when(mockView.getSelectedProductId()).thenReturn(-1);

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString());

        // 4. Gọi hàm
        spyController.deleteProduct();

        // 5. Kiểm tra: Model.deleteProduct() KHÔNG được gọi
        verify(mockModel, never()).deleteProduct(anyInt());
        verify(mockView, never()).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C07: deleteProduct() hiển thị thông báo lỗi khi xóa thất bại")
    void testDeleteProductFail() {
        // 1. Tạo spy controller
        ProductController spyController = spy(controller);

        // 2. Giả lập View trả về ID = 1
        when(mockView.getSelectedProductId()).thenReturn(1);

        // 3. Giả lập Model.deleteProduct() trả về false (xóa thất bại)
        when(mockModel.deleteProduct(1)).thenReturn(false);

        // 4. Bỏ qua JOptionPane
        doReturn(JOptionPane.YES_OPTION).when(spyController).showConfirmDialog(anyString());
        doNothing().when(spyController).showMessage(anyString());

        // 5. Gọi hàm
        spyController.deleteProduct();

        // 6. Kiểm tra: Model.deleteProduct() được gọi
        verify(mockModel, times(1)).deleteProduct(1);

        // 7. Kiểm tra: showMessage() được gọi với thông báo lỗi
        verify(spyController, times(1)).showMessage(contains("thất bại"));
    }
}