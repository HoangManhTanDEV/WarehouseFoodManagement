package com.warehouse.controller;

import com.warehouse.model.ExportModel;
import com.warehouse.view.ExportPanel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExportControllerTest {

    @Mock
    private ExportModel mockModel;

    @Mock
    private ExportPanel mockView;

    @Mock
    private JTable mockCartTable;

    @Mock
    private JTextField mockReceiptCodeField;

    private Map<String, String> mockUserInfo;
    private ExportController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUserInfo = new HashMap<>();
        mockUserInfo.put("id", "1");
        mockUserInfo.put("fullname", "Test User");
        mockUserInfo.put("email", "test@warehouse.com");

        controller = new ExportController(mockView, mockModel, mockUserInfo, true);
    }

    // ==================== TEST 1: LOAD DỮ LIỆU ====================
    @Test
    @DisplayName("TC_C31: loadInitialData() gọi refreshProductTable() và setReceiptCode()")
    void testLoadInitialData() {
        // 1. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockProducts = new ArrayList<>();
        String mockCode = "PX00001";

        // 2. Giả lập Model
        when(mockModel.getAvailableProducts()).thenReturn(mockProducts);
        when(mockModel.generateReceiptCode()).thenReturn(mockCode);

        // 3. Gọi hàm cần test
        controller.loadInitialData();

        // 4. Kiểm tra
        verify(mockView, times(1)).refreshProductTable(mockProducts);
        verify(mockView, times(1)).setReceiptCode(mockCode);
    }

    // ==================== TEST 2: THÊM VÀO GIỎ ====================
    @Test
    @DisplayName("TC_C32: addToCart() thêm sản phẩm vào giỏ khi chọn đúng")
    void testAddToCartSuccess() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        int productId = 1;
        Map<String, Object> mockProduct = new HashMap<>();
        mockProduct.put("id", 1);
        mockProduct.put("name", "Sản phẩm test");
        mockProduct.put("quantity", 100);
        mockProduct.put("sell_price", 10000.0);

        List<Map<String, Object>> mockProducts = new ArrayList<>();
        mockProducts.add(mockProduct);

        // 3. Giả lập View và Model
        when(mockView.getSelectedProductId()).thenReturn(productId);
        when(mockModel.getAvailableProducts()).thenReturn(mockProducts);
        when(mockView.getCartDetails()).thenReturn(new ArrayList<>());

        // 4. Bỏ qua JOptionPane
        doReturn("10").when(spyController).showInputDialog(anyString());
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.addToCart();

        // 6. Kiểm tra: View.addToCart() được gọi
        verify(mockView, times(1)).addToCart(anyMap(), eq(10));
    }

    @Test
    @DisplayName("TC_C33: addToCart() không thêm khi không chọn sản phẩm")
    void testAddToCartNoSelection() {
        // 1. Giả lập View trả về -1
        when(mockView.getSelectedProductId()).thenReturn(-1);

        // 2. Gọi hàm
        controller.addToCart();

        // 3. Kiểm tra: View.addToCart() KHÔNG được gọi
        verify(mockView, never()).addToCart(anyMap(), anyInt());
    }

    @Test
    @DisplayName("TC_C34: addToCart() không thêm khi số lượng không hợp lệ")
    void testAddToCartInvalidQuantity() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        int productId = 1;
        Map<String, Object> mockProduct = new HashMap<>();
        mockProduct.put("id", 1);
        mockProduct.put("name", "Sản phẩm test");
        mockProduct.put("quantity", 100);

        List<Map<String, Object>> mockProducts = new ArrayList<>();
        mockProducts.add(mockProduct);

        // 3. Giả lập View và Model
        when(mockView.getSelectedProductId()).thenReturn(productId);
        when(mockModel.getAvailableProducts()).thenReturn(mockProducts);

        // 4. Bỏ qua JOptionPane - nhập chữ
        doReturn("abc").when(spyController).showInputDialog(anyString());
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.addToCart();

        // 6. Kiểm tra: View.addToCart() KHÔNG được gọi
        verify(mockView, never()).addToCart(anyMap(), anyInt());
    }

    // ==================== TEST 3: XÓA KHỎI GIỎ ====================
    @Test
    @DisplayName("TC_C35: removeFromCart() xóa sản phẩm khỏi giỏ khi chọn YES")
    void testRemoveFromCartYes() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        Map<String, Object> mockItem = new HashMap<>();
        mockItem.put("product_id", 1);
        mockItem.put("product_name", "Sản phẩm test");

        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        mockCartDetails.add(mockItem);

        // 3. Giả lập View
        when(mockView.getCartTable()).thenReturn(mockCartTable);
        when(mockView.getCartTable().getSelectedRow()).thenReturn(0);
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);

        // 4. Bỏ qua JOptionPane - chọn YES
        doReturn(JOptionPane.YES_OPTION).when(spyController).showConfirmDialog(anyString());
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.removeFromCart();

        // 6. Kiểm tra: View.removeFromCart() được gọi
        verify(mockView, times(1)).removeFromCart(1);
    }

    @Test
    @DisplayName("TC_C36: removeFromCart() không xóa khi chọn NO")
    void testRemoveFromCartNo() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        Map<String, Object> mockItem = new HashMap<>();
        mockItem.put("product_id", 1);
        mockItem.put("product_name", "Sản phẩm test");

        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        mockCartDetails.add(mockItem);

        // 3. Giả lập View
        when(mockView.getCartTable()).thenReturn(mockCartTable);
        when(mockView.getCartTable().getSelectedRow()).thenReturn(0);
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);

        // 4. Bỏ qua JOptionPane - chọn NO
        doReturn(JOptionPane.NO_OPTION).when(spyController).showConfirmDialog(anyString());

        // 5. Gọi hàm
        spyController.removeFromCart();

        // 6. Kiểm tra: View.removeFromCart() KHÔNG được gọi
        verify(mockView, never()).removeFromCart(anyInt());
    }

    // ==================== TEST 4: TẠO PHIẾU XUẤT ====================
    @Test
    @DisplayName("TC_C37: createExportReceipt() tạo phiếu xuất thành công")
    void testCreateExportReceiptSuccess() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", 1);
        detail.put("quantity", 10);
        mockCartDetails.add(detail);

        String mockReceiptCode = "PX00001";

        // 3. Giả lập View
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);
        when(mockView.getCustomerName()).thenReturn("Khách hàng test");
        when(mockView.getReceiptCodeField()).thenReturn(mockReceiptCodeField);
        when(mockView.getReceiptCodeField().getText()).thenReturn(mockReceiptCode);
        when(mockView.getTotalAmount()).thenReturn(100000.0);

        // 4. Giả lập Model
        when(mockModel.createExportReceipt(anyMap(), anyList())).thenReturn(true);
        when(mockModel.generateReceiptCode()).thenReturn("PX00002");

        // 5. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 6. Gọi hàm
        spyController.createExportReceipt();

        // 7. Kiểm tra
        verify(mockModel, times(1)).createExportReceipt(anyMap(), anyList());
        verify(mockView, times(1)).clearCart();
        verify(mockView, times(1)).setReceiptCode("PX00002");
        verify(mockView, times(1)).refreshProductTable(anyList());
    }

    @Test
    @DisplayName("TC_C38: createExportReceipt() thất bại khi giỏ hàng trống")
    void testCreateExportReceiptEmptyCart() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Giả lập View trả về giỏ trống
        when(mockView.getCartDetails()).thenReturn(new ArrayList<>());

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm
        spyController.createExportReceipt();

        // 5. Kiểm tra: Model KHÔNG được gọi
        verify(mockModel, never()).createExportReceipt(anyMap(), anyList());
    }

    @Test
    @DisplayName("TC_C39: createExportReceipt() thất bại khi không nhập tên khách hàng")
    void testCreateExportReceiptNoCustomer() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", 1);
        detail.put("quantity", 10);
        mockCartDetails.add(detail);

        // 3. Giả lập View
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);
        when(mockView.getCustomerName()).thenReturn("");

        // 4. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.createExportReceipt();

        // 6. Kiểm tra: Model KHÔNG được gọi
        verify(mockModel, never()).createExportReceipt(anyMap(), anyList());
    }

    @Test
    @DisplayName("TC_C40: createExportReceipt() thất bại khi Model trả về false")
    void testCreateExportReceiptModelFail() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", 1);
        detail.put("quantity", 10);
        mockCartDetails.add(detail);

        // 3. Giả lập View
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);
        when(mockView.getCustomerName()).thenReturn("Khách hàng test");
        when(mockView.getReceiptCodeField()).thenReturn(mockReceiptCodeField);
        when(mockView.getReceiptCodeField().getText()).thenReturn("PX00001");
        when(mockView.getTotalAmount()).thenReturn(100000.0);

        // 4. Giả lập Model trả về false
        when(mockModel.createExportReceipt(anyMap(), anyList())).thenReturn(false);

        // 5. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 6. Gọi hàm
        spyController.createExportReceipt();

        // 7. Kiểm tra: Model được gọi
        verify(mockModel, times(1)).createExportReceipt(anyMap(), anyList());
        verify(mockView, never()).clearCart();
    }

    // ==================== TEST 5: XUẤT EXCEL ====================
    @Test
    @DisplayName("TC_C41: exportToExcel() xuất Excel khi có dữ liệu")
    void testExportToExcelSuccess() {
        // 1. Tạo spy controller - KHÔNG gọi thật ExcelExporter
        ExportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", 1);
        detail.put("quantity", 10);
        detail.put("code", "TEST001");
        detail.put("product_name", "Sản phẩm test");
        detail.put("sell_price", 10000.0);
        mockCartDetails.add(detail);

        // 3. Giả lập View
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);
        when(mockView.getReceiptCodeField()).thenReturn(mockReceiptCodeField);
        when(mockView.getReceiptCodeField().getText()).thenReturn("PX00001");
        when(mockView.getCustomerName()).thenReturn("Khách hàng test");

        // 4. Giả lập Model.getAvailableProducts() trả về danh sách sản phẩm
        List<Map<String, Object>> mockProducts = new ArrayList<>();
        Map<String, Object> product = new HashMap<>();
        product.put("id", 1);
        product.put("code", "TEST001");
        mockProducts.add(product);
        when(mockModel.getAvailableProducts()).thenReturn(mockProducts);

        // 5. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 6. Gọi hàm
        spyController.exportToExcel();

        // 7. Kiểm tra: buildReceiptData() được gọi
        verify(spyController, times(1)).buildReceiptData();
    }

    @Test
    @DisplayName("TC_C42: exportToExcel() thất bại khi giỏ hàng trống")
    void testExportToExcelEmptyCart() {
        // 1. Tạo spy controller
        ExportController spyController = spy(controller);

        // 2. Giả lập View trả về giỏ trống
        when(mockView.getCartDetails()).thenReturn(new ArrayList<>());

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm
        spyController.exportToExcel();

        // 5. Kiểm tra: buildReceiptData() KHÔNG được gọi
        verify(spyController, never()).buildReceiptData();
    }
}