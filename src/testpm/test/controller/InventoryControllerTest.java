package com.warehouse.controller;

import com.warehouse.model.InventoryModel;
import com.warehouse.view.InventoryPanel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JOptionPane;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryControllerTest {

    @Mock
    private InventoryModel mockModel;

    @Mock
    private InventoryPanel mockView;

    private InventoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new InventoryController(mockView, mockModel, true);
    }

    // ==================== TEST 1: LOAD DỮ LIỆU ====================
    @Test
    @DisplayName("TC_C43: loadInventoryData() gọi refreshInventoryTable()")
    void testLoadInventoryData() {
        // 1. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockInventory = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("code", "TEST001");
        item.put("name", "Sản phẩm test");
        mockInventory.add(item);

        // 2. Giả lập Model
        when(mockModel.getAllInventory()).thenReturn(mockInventory);

        // 3. Gọi hàm cần test
        controller.loadInventoryData();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getAllInventory();
        verify(mockView, times(1)).refreshInventoryTable(mockInventory);
    }

    @Test
    @DisplayName("TC_C44: loadSummary() gọi updateSummary()")
    void testLoadSummary() {
        // 1. Chuẩn bị dữ liệu giả
        Map<String, Double> mockSummary = new HashMap<>();
        mockSummary.put("total_value", 1000000.0);
        mockSummary.put("total_quantity", 100.0);
        mockSummary.put("total_products", 10.0);

        // 2. Giả lập Model
        when(mockModel.getInventorySummary()).thenReturn(mockSummary);

        // 3. Gọi hàm cần test
        controller.loadSummary();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getInventorySummary();
        verify(mockView, times(1)).updateSummary(mockSummary);
    }

    // ==================== TEST 2: TÌM KIẾM ====================
    @Test
    @DisplayName("TC_C45: searchInventory() gọi searchInventory() khi có từ khóa")
    void testSearchInventoryWithKeyword() {
        // 1. Chuẩn bị dữ liệu
        String keyword = "test";
        List<Map<String, Object>> mockResults = new ArrayList<>();
        when(mockView.getSearchKeyword()).thenReturn(keyword);
        when(mockModel.searchInventory(keyword)).thenReturn(mockResults);

        // 2. Gọi hàm
        controller.searchInventory();

        // 3. Kiểm tra
        verify(mockModel, times(1)).searchInventory(keyword);
        verify(mockView, times(1)).refreshInventoryTable(mockResults);
    }

    @Test
    @DisplayName("TC_C46: searchInventory() gọi getAllInventory() khi từ khóa rỗng")
    void testSearchInventoryEmptyKeyword() {
        // 1. Chuẩn bị dữ liệu
        List<Map<String, Object>> mockInventory = new ArrayList<>();
        when(mockView.getSearchKeyword()).thenReturn("");
        when(mockModel.getAllInventory()).thenReturn(mockInventory);

        // 2. Gọi hàm
        controller.searchInventory();

        // 3. Kiểm tra
        verify(mockModel, times(1)).getAllInventory();
        verify(mockModel, never()).searchInventory(anyString());
        verify(mockView, times(1)).refreshInventoryTable(mockInventory);
    }

    // ==================== TEST 3: LÀM MỚI ====================
    @Test
    @DisplayName("TC_C47: refreshAll() gọi loadInventoryData() và loadSummary()")
    void testRefreshAll() {
        // 1. Tạo spy controller
        InventoryController spyController = spy(controller);

        // 2. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 3. Gọi hàm
        spyController.refreshAll();

        // 4. Kiểm tra
        verify(spyController, times(1)).loadInventoryData();
        verify(spyController, times(1)).loadSummary();
    }

    // ==================== TEST 4: HIỂN THỊ HÀNG TỒN THẤP ====================
    @Test
    @DisplayName("TC_C48: showLowStock() hiển thị khi có hàng tồn thấp")
    void testShowLowStockHasData() {
        // 1. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockLowStock = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("code", "TEST001");
        item.put("name", "Sản phẩm test");
        item.put("quantity", 5);
        mockLowStock.add(item);

        // 2. Giả lập Model
        when(mockModel.getLowStockProducts(10)).thenReturn(mockLowStock);

        // 3. Gọi hàm
        controller.showLowStock();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getLowStockProducts(10);
        verify(mockView, times(1)).showLowStockProducts(mockLowStock);
    }

    @Test
    @DisplayName("TC_C49: showLowStock() hiển thị thông báo khi không có hàng tồn thấp")
    void testShowLowStockEmpty() {
        // 1. Tạo spy controller
        InventoryController spyController = spy(controller);

        // 2. Giả lập Model trả về danh sách rỗng
        when(mockModel.getLowStockProducts(10)).thenReturn(new ArrayList<>());

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm
        spyController.showLowStock();

        // 5. Kiểm tra
        verify(mockModel, times(1)).getLowStockProducts(10);
        verify(mockView, never()).showLowStockProducts(anyList());
        verify(spyController, times(1)).showMessage(contains("Không có"), anyString(), anyInt());
    }

    // ==================== TEST 5: HIỂN THỊ HÀNG HẾT HẠN ====================
    @Test
    @DisplayName("TC_C50: showExpiring() hiển thị khi có hàng sắp hết hạn")
    void testShowExpiringHasData() {
        // 1. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockExpiring = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("code", "TEST001");
        item.put("name", "Sản phẩm test");
        item.put("expiry_date", new java.sql.Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000L));
        item.put("days_left", 15);
        mockExpiring.add(item);

        // 2. Giả lập Model
        when(mockModel.getExpiringProducts()).thenReturn(mockExpiring);

        // 3. Gọi hàm
        controller.showExpiring();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getExpiringProducts();
        verify(mockView, times(1)).showExpiringProducts(mockExpiring);
    }

    @Test
    @DisplayName("TC_C51: showExpiring() hiển thị thông báo khi không có hàng hết hạn")
    void testShowExpiringEmpty() {
        // 1. Tạo spy controller
        InventoryController spyController = spy(controller);

        // 2. Giả lập Model trả về danh sách rỗng
        when(mockModel.getExpiringProducts()).thenReturn(new ArrayList<>());

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm
        spyController.showExpiring();

        // 5. Kiểm tra
        verify(mockModel, times(1)).getExpiringProducts();
        verify(mockView, never()).showExpiringProducts(anyList());
        verify(spyController, times(1)).showMessage(contains("Không có"), anyString(), anyInt());
    }

    // ==================== TEST 6: XUẤT EXCEL ====================
    @Test
    @DisplayName("TC_C52: exportToExcel() xuất Excel khi có dữ liệu")
    void testExportToExcelSuccess() {
        // 1. Tạo spy controller
        InventoryController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        List<Map<String, Object>> mockInventory = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("code", "TEST001");
        item.put("name", "Sản phẩm test");
        mockInventory.add(item);

        // 3. Giả lập Model
        when(mockModel.getAllInventory()).thenReturn(mockInventory);

        // 4. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 5. Gọi hàm
        spyController.exportToExcel();

        // 6. Kiểm tra
        verify(mockModel, times(1)).getAllInventory();
        verify(spyController, times(1)).showMessage(contains("Xuất Excel thành công"), anyString(), anyInt());
    }

    @Test
    @DisplayName("TC_C53: exportToExcel() thông báo khi không có dữ liệu")
    void testExportToExcelEmpty() {
        // 1. Tạo spy controller
        InventoryController spyController = spy(controller);

        // 2. Giả lập Model trả về danh sách rỗng
        when(mockModel.getAllInventory()).thenReturn(new ArrayList<>());

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm
        spyController.exportToExcel();

        // 5. Kiểm tra
        verify(mockModel, times(1)).getAllInventory();
        verify(spyController, times(1)).showMessage(contains("Không có"), anyString(), anyInt());
    }
}