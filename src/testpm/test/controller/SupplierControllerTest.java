package com.warehouse.controller;

import com.warehouse.model.SupplierModel;
import com.warehouse.view.SupplierPanel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JOptionPane;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SupplierControllerTest {

    @Mock
    private SupplierModel mockModel;

    @Mock
    private SupplierPanel mockView;

    private SupplierController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SupplierController(mockView, mockModel, true);
    }

    // ==================== TEST 1: LOAD DỮ LIỆU ====================
    @Test
    @DisplayName("TC_C14: loadSupplierData() gọi getAllSuppliers() và refreshTable()")
    void testLoadSupplierData() {
        // 1. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockSuppliers = new ArrayList<>();
        Map<String, Object> supplier = new HashMap<>();
        supplier.put("id", 1);
        supplier.put("code", "NCC001");
        supplier.put("name", "Nhà cung cấp test");
        mockSuppliers.add(supplier);

        // 2. Giả lập Model
        when(mockModel.getAllSuppliers()).thenReturn(mockSuppliers);

        // 3. Gọi hàm cần test
        controller.loadSupplierData();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getAllSuppliers();
        verify(mockView, times(1)).refreshTable(mockSuppliers);
    }

    // ==================== TEST 2: TÌM KIẾM ====================
    @Test
    @DisplayName("TC_C15: searchSupplier() gọi searchSuppliers() khi có từ khóa")
    void testSearchSupplierWithKeyword() {
        // 1. Chuẩn bị dữ liệu
        String keyword = "test";
        List<Map<String, Object>> mockResults = new ArrayList<>();

        // 2. Giả lập
        when(mockView.getSearchKeyword()).thenReturn(keyword);
        when(mockModel.searchSuppliers(keyword)).thenReturn(mockResults);

        // 3. Gọi hàm
        controller.searchSupplier();

        // 4. Kiểm tra
        verify(mockModel, times(1)).searchSuppliers(keyword);
        verify(mockView, times(1)).refreshTable(mockResults);
    }

    @Test
    @DisplayName("TC_C16: searchSupplier() gọi getAllSuppliers() khi từ khóa rỗng")
    void testSearchSupplierWithEmptyKeyword() {
        // 1. Chuẩn bị dữ liệu
        List<Map<String, Object>> mockSuppliers = new ArrayList<>();

        // 2. Giả lập
        when(mockView.getSearchKeyword()).thenReturn("");
        when(mockModel.getAllSuppliers()).thenReturn(mockSuppliers);

        // 3. Gọi hàm
        controller.searchSupplier();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getAllSuppliers();
        verify(mockModel, never()).searchSuppliers(anyString());
        verify(mockView, times(1)).refreshTable(mockSuppliers);
    }

    // ==================== TEST 3: XÓA NHÀ CUNG CẤP ====================
    @Test
    @DisplayName("TC_C17: deleteSupplier() xóa NCC khi có ID hợp lệ và xác nhận YES")
    void testDeleteSupplierSuccess() {
        // 1. Tạo spy controller
        SupplierController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        Map<String, Object> mockSupplier = new HashMap<>();
        mockSupplier.put("id", 1);
        mockSupplier.put("name", "NCC test");

        // 3. Giả lập View
        when(mockView.getSelectedSupplier()).thenReturn(mockSupplier);

        // 4. Giả lập Model.deleteSupplier() trả về true
        when(mockModel.deleteSupplier(1)).thenReturn(true);

        // 5. Bỏ qua JOptionPane
        doReturn(JOptionPane.YES_OPTION).when(spyController).showConfirmDialog(anyString());
        doNothing().when(spyController).showMessage(anyString());

        // 6. Gọi hàm
        spyController.deleteSupplier();

        // 7. Kiểm tra
        verify(mockModel, times(1)).deleteSupplier(1);
        verify(mockView, times(1)).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C18: deleteSupplier() không xóa khi không chọn NCC")
    void testDeleteSupplierNoSelection() {
        // 1. Tạo spy controller
        SupplierController spyController = spy(controller);

        // 2. Giả lập View trả về null
        when(mockView.getSelectedSupplier()).thenReturn(null);

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString());

        // 4. Gọi hàm
        spyController.deleteSupplier();

        // 5. Kiểm tra
        verify(mockModel, never()).deleteSupplier(anyInt());
        verify(mockView, never()).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C19: deleteSupplier() không xóa khi chọn NO")
    void testDeleteSupplierCancel() {
        // 1. Tạo spy controller
        SupplierController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        Map<String, Object> mockSupplier = new HashMap<>();
        mockSupplier.put("id", 1);
        mockSupplier.put("name", "NCC test");

        // 3. Giả lập View
        when(mockView.getSelectedSupplier()).thenReturn(mockSupplier);

        // 4. Bỏ qua JOptionPane - chọn NO
        doReturn(JOptionPane.NO_OPTION).when(spyController).showConfirmDialog(anyString());

        // 5. Gọi hàm
        spyController.deleteSupplier();

        // 6. Kiểm tra
        verify(mockModel, never()).deleteSupplier(anyInt());
        verify(mockView, never()).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C20: deleteSupplier() hiển thị thông báo lỗi khi xóa thất bại")
    void testDeleteSupplierFail() {
        // 1. Tạo spy controller
        SupplierController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        Map<String, Object> mockSupplier = new HashMap<>();
        mockSupplier.put("id", 1);
        mockSupplier.put("name", "NCC test");

        // 3. Giả lập View
        when(mockView.getSelectedSupplier()).thenReturn(mockSupplier);

        // 4. Giả lập Model.deleteSupplier() trả về false
        when(mockModel.deleteSupplier(1)).thenReturn(false);

        // 5. Bỏ qua JOptionPane
        doReturn(JOptionPane.YES_OPTION).when(spyController).showConfirmDialog(anyString());
        doNothing().when(spyController).showMessage(anyString());

        // 6. Gọi hàm
        spyController.deleteSupplier();

        // 7. Kiểm tra
        verify(mockModel, times(1)).deleteSupplier(1);
        verify(spyController, times(1)).showMessage(contains("thất bại"));
    }
}