package com.warehouse.controller;

import com.warehouse.model.ImportModel;
import com.warehouse.view.ImportPanel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ImportControllerTest {

    @Mock
    private ImportModel mockModel;

    @Mock
    private ImportPanel mockView;

    @Mock
    private JTable mockCartTable;

    @Mock
    private JTextField mockReceiptCodeField;

    private Map<String, String> mockUserInfo;
    private ImportController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUserInfo = new HashMap<>();
        mockUserInfo.put("id", "1");
        mockUserInfo.put("email", "test@warehouse.com");
        mockUserInfo.put("fullname", "Test User");

        controller = new ImportController(mockView, mockModel, mockUserInfo, true);
    }

    // ==================== TEST 1: LOAD DỮ LIỆU ====================
    @Test
    @DisplayName("TC_C21: loadInitialData() gọi refreshProductTable(), refreshSupplierCombo(), setReceiptCode()")
    void testLoadInitialData() {
        // 1. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockProducts = new ArrayList<>();
        List<Map<String, Object>> mockSuppliers = new ArrayList<>();
        String mockCode = "PN00001";

        // 2. Giả lập Model
        when(mockModel.getAllProducts()).thenReturn(mockProducts);
        when(mockModel.getAllSuppliers()).thenReturn(mockSuppliers);
        when(mockModel.generateReceiptCode()).thenReturn(mockCode);

        // 3. Gọi hàm cần test
        controller.loadInitialData();

        // 4. Kiểm tra
        verify(mockView, times(1)).refreshProductTable(mockProducts);
        verify(mockView, times(1)).refreshSupplierCombo(mockSuppliers);
        verify(mockView, times(1)).setReceiptCode(mockCode);
    }

    // ==================== TEST 2: THÊM VÀO GIỎ ====================
    @Test
    @DisplayName("TC_C22: addToCart() thêm sản phẩm vào giỏ khi chọn đúng")
    void testAddToCartSuccess() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        int productId = 1;
        Map<String, Object> mockProduct = new HashMap<>();
        mockProduct.put("id", 1);
        mockProduct.put("name", "Sản phẩm test");
        mockProduct.put("import_price", 10000.0);

        List<Map<String, Object>> mockProducts = new ArrayList<>();
        mockProducts.add(mockProduct);

        // 3. Giả lập View và Model
        when(mockView.getSelectedProductId()).thenReturn(productId);
        when(mockModel.getAllProducts()).thenReturn(mockProducts);
        when(mockView.getCartDetails()).thenReturn(new ArrayList<>());

        // 4. Bỏ qua JOptionPane
        doReturn("10").when(spyController).showInputDialog(anyString());
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.addToCart();

        // 6. Kiểm tra: View.addToCart() được gọi
        verify(mockView, times(1)).addToCart(anyMap(), eq(10), eq(10000.0));
    }

    @Test
    @DisplayName("TC_C23: addToCart() không thêm khi không chọn sản phẩm")
    void testAddToCartNoSelection() {
        // 1. Giả lập View trả về -1
        when(mockView.getSelectedProductId()).thenReturn(-1);

        // 2. Gọi hàm
        controller.addToCart();

        // 3. Kiểm tra: View.addToCart() KHÔNG được gọi
        verify(mockView, never()).addToCart(anyMap(), anyInt(), anyDouble());
    }

    @Test
    @DisplayName("TC_C24: addToCart() không thêm khi nhập số lượng không hợp lệ")
    void testAddToCartInvalidQuantity() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        int productId = 1;
        Map<String, Object> mockProduct = new HashMap<>();
        mockProduct.put("id", 1);
        mockProduct.put("name", "Sản phẩm test");
        mockProduct.put("import_price", 10000.0);

        List<Map<String, Object>> mockProducts = new ArrayList<>();
        mockProducts.add(mockProduct);

        // 3. Giả lập View và Model
        when(mockView.getSelectedProductId()).thenReturn(productId);
        when(mockModel.getAllProducts()).thenReturn(mockProducts);

        // 4. Bỏ qua JOptionPane - nhập chữ
        doReturn("abc").when(spyController).showInputDialog(anyString());
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.addToCart();

        // 6. Kiểm tra: View.addToCart() KHÔNG được gọi
        verify(mockView, never()).addToCart(anyMap(), anyInt(), anyDouble());
    }

    // ==================== TEST 3: XÓA KHỎI GIỎ ====================
    @Test
    @DisplayName("TC_C25: removeFromCart() xóa sản phẩm khỏi giỏ khi chọn YES")
    void testRemoveFromCartYes() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

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
    @DisplayName("TC_C26: removeFromCart() không xóa khi chọn NO")
    void testRemoveFromCartNo() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

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

    // ==================== TEST 4: TẠO PHIẾU NHẬP ====================
    @Test
    @DisplayName("TC_C27: createImportReceipt() tạo phiếu nhập thành công")
    void testCreateImportReceiptSuccess() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", 1);
        detail.put("quantity", 10);
        mockCartDetails.add(detail);

        String mockReceiptCode = "PN00001";

        // 3. Giả lập View
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);
        when(mockView.getSelectedSupplierId()).thenReturn(1);
        when(mockView.getReceiptCodeField()).thenReturn(mockReceiptCodeField);
        when(mockView.getReceiptCodeField().getText()).thenReturn(mockReceiptCode);
        when(mockView.getTotalAmount()).thenReturn(100000.0);

        // 4. Giả lập Model
        when(mockModel.createImportReceipt(anyMap(), anyList())).thenReturn(true);
        when(mockModel.generateReceiptCode()).thenReturn("PN00002");

        // 5. Bỏ qua JOptionPane và email
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());
        doNothing().when(spyController).sendEmailNotification(anyMap());

        // 6. Gọi hàm
        spyController.createImportReceipt();

        // 7. Kiểm tra
        verify(mockModel, times(1)).createImportReceipt(anyMap(), anyList());
        verify(mockView, times(1)).clearCart();
        verify(mockView, times(1)).setReceiptCode("PN00002");
        verify(mockView, times(1)).refreshProductTable(anyList());
    }

    @Test
    @DisplayName("TC_C28: createImportReceipt() thất bại khi giỏ hàng trống")
    void testCreateImportReceiptEmptyCart() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

        // 2. Giả lập View trả về giỏ trống
        when(mockView.getCartDetails()).thenReturn(new ArrayList<>());

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm
        spyController.createImportReceipt();

        // 5. Kiểm tra: Model KHÔNG được gọi
        verify(mockModel, never()).createImportReceipt(anyMap(), anyList());
    }

    @Test
    @DisplayName("TC_C29: createImportReceipt() thất bại khi không chọn NCC")
    void testCreateImportReceiptNoSupplier() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", 1);
        detail.put("quantity", 10);
        mockCartDetails.add(detail);

        // 3. Giả lập View
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);
        when(mockView.getSelectedSupplierId()).thenReturn(-1);

        // 4. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.createImportReceipt();

        // 6. Kiểm tra: Model KHÔNG được gọi
        verify(mockModel, never()).createImportReceipt(anyMap(), anyList());
    }

    @Test
    @DisplayName("TC_C30: createImportReceipt() thất bại khi Model trả về false")
    void testCreateImportReceiptModelFail() {
        // 1. Tạo spy controller
        ImportController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        List<Map<String, Object>> mockCartDetails = new ArrayList<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("product_id", 1);
        detail.put("quantity", 10);
        mockCartDetails.add(detail);

        // 3. Giả lập View
        when(mockView.getCartDetails()).thenReturn(mockCartDetails);
        when(mockView.getSelectedSupplierId()).thenReturn(1);
        when(mockView.getReceiptCodeField()).thenReturn(mockReceiptCodeField);
        when(mockView.getReceiptCodeField().getText()).thenReturn("PN00001");
        when(mockView.getTotalAmount()).thenReturn(100000.0);

        // 4. Giả lập Model trả về false
        when(mockModel.createImportReceipt(anyMap(), anyList())).thenReturn(false);

        // 5. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 6. Gọi hàm
        spyController.createImportReceipt();

        // 7. Kiểm tra: Model được gọi
        verify(mockModel, times(1)).createImportReceipt(anyMap(), anyList());
        // Kiểm tra: Không reset form
        verify(mockView, never()).clearCart();
    }
}