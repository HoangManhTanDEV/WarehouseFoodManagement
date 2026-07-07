package com.warehouse.controller;

import com.warehouse.model.StatisticsModel;
import com.warehouse.view.StatisticsPanel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JComboBox;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatisticsControllerTest {

    @Mock
    private StatisticsModel mockModel;

    @Mock
    private StatisticsPanel mockView;

    @Mock
    private JComboBox<String> mockPeriodCombo;

    @Mock
    private JComboBox<Integer> mockYearCombo;

    private StatisticsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new StatisticsController(mockView, mockModel, true);
    }

    // ==================== TEST 1: LOAD NĂM ====================
    @Test
    @DisplayName("TC_C54: loadYears() gọi setYears()")
    void testLoadYears() {
        // 1. Chuẩn bị dữ liệu giả
        List<Integer> mockYears = Arrays.asList(2025, 2026);

        // 2. Giả lập Model
        when(mockModel.getAvailableYears()).thenReturn(mockYears);

        // 3. Gọi hàm cần test
        controller.loadYears();

        // 4. Kiểm tra
        verify(mockModel, times(1)).getAvailableYears();
        verify(mockView, times(1)).setYears(mockYears);
    }

    // ==================== TEST 2: LOAD THỐNG KÊ ====================
    @Test
    @DisplayName("TC_C55: loadStatistics() gọi các method thống kê")
    void testLoadStatistics() {
        // 1. Tạo spy controller
        StatisticsController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        String period = "Tháng này";
        int year = 2026;

        // 3. Giả lập View
        when(mockView.getSelectedPeriod()).thenReturn(period);
        when(mockView.getSelectedYear()).thenReturn(year);

        // 4. Giả lập Model trả về dữ liệu
        Map<String, Object> mockImportStats = new HashMap<>();
        mockImportStats.put("total_value", 1000000.0);
        when(mockModel.getImportStatistics(any(Date.class), any(Date.class))).thenReturn(mockImportStats);

        Map<String, Object> mockExportStats = new HashMap<>();
        mockExportStats.put("total_revenue", 800000.0);
        when(mockModel.getExportStatistics(any(Date.class), any(Date.class))).thenReturn(mockExportStats);

        Map<String, Object> mockProfitStats = new HashMap<>();
        mockProfitStats.put("profit", 200000.0);
        mockProfitStats.put("profit_margin", 25.0);
        when(mockModel.getProfitStatistics(any(Date.class), any(Date.class))).thenReturn(mockProfitStats);

        List<Map<String, Object>> mockTopProducts = new ArrayList<>();
        when(mockModel.getTopSellingProducts(anyInt(), any(Date.class), any(Date.class))).thenReturn(mockTopProducts);

        List<Map<String, Object>> mockMonthlyStats = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("import_value", 100000.0);
            stat.put("export_value", 80000.0);
            stat.put("profit", 20000.0);
            mockMonthlyStats.add(stat);
        }
        when(mockModel.getMonthlyStatistics(year)).thenReturn(mockMonthlyStats);

        // 5. Gọi hàm
        spyController.loadStatistics();

        // 6. Kiểm tra các method được gọi
        verify(mockModel, times(1)).getImportStatistics(any(Date.class), any(Date.class));
        verify(mockModel, times(1)).getExportStatistics(any(Date.class), any(Date.class));
        verify(mockModel, times(1)).getProfitStatistics(any(Date.class), any(Date.class));
        verify(mockModel, times(1)).getTopSellingProducts(eq(10), any(Date.class), any(Date.class));
        verify(mockModel, times(1)).getMonthlyStatistics(year);

        // 7. Kiểm tra View được cập nhật
        verify(mockView, times(1)).updateSummary(1000000.0, 800000.0, 200000.0, 25.0);
        verify(mockView, times(1)).updateTopProducts(mockTopProducts);
        verify(mockView, times(1)).updateMonthlyStatistics(mockMonthlyStats);
    }

    @Test
    @DisplayName("TC_C56: loadStatistics() với kỳ 'Tháng trước'")
    void testLoadStatisticsPreviousMonth() {
        // 1. Tạo spy controller
        StatisticsController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        String period = "Tháng trước";
        int year = 2026;

        // 3. Giả lập View
        when(mockView.getSelectedPeriod()).thenReturn(period);
        when(mockView.getSelectedYear()).thenReturn(year);

        // 4. Giả lập Model
        when(mockModel.getImportStatistics(any(Date.class), any(Date.class))).thenReturn(new HashMap<>());
        when(mockModel.getExportStatistics(any(Date.class), any(Date.class))).thenReturn(new HashMap<>());
        when(mockModel.getProfitStatistics(any(Date.class), any(Date.class))).thenReturn(new HashMap<>());
        when(mockModel.getTopSellingProducts(anyInt(), any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());
        when(mockModel.getMonthlyStatistics(year)).thenReturn(new ArrayList<>());

        // 5. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 6. Gọi hàm
        spyController.loadStatistics();

        // 7. Kiểm tra: loadStatistics được gọi
        verify(mockView, atLeast(1)).getSelectedPeriod();
        verify(mockView, atLeast(1)).getSelectedYear();
    }

    @Test
    @DisplayName("TC_C57: loadStatistics() với kỳ 'Năm nay'")
    void testLoadStatisticsThisYear() {
        // 1. Tạo spy controller
        StatisticsController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        String period = "Năm nay";
        int year = 2026;

        // 3. Giả lập View
        when(mockView.getSelectedPeriod()).thenReturn(period);
        when(mockView.getSelectedYear()).thenReturn(year);

        // 4. Giả lập Model
        when(mockModel.getImportStatistics(any(Date.class), any(Date.class))).thenReturn(new HashMap<>());
        when(mockModel.getExportStatistics(any(Date.class), any(Date.class))).thenReturn(new HashMap<>());
        when(mockModel.getProfitStatistics(any(Date.class), any(Date.class))).thenReturn(new HashMap<>());
        when(mockModel.getTopSellingProducts(anyInt(), any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());
        when(mockModel.getMonthlyStatistics(year)).thenReturn(new ArrayList<>());

        // 5. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 6. Gọi hàm
        spyController.loadStatistics();

        // 7. Kiểm tra: loadStatistics được gọi
        verify(mockView, atLeast(1)).getSelectedPeriod();
        verify(mockView, atLeast(1)).getSelectedYear();
    }

    @Test
    @DisplayName("TC_C58: loadStatistics() xử lý khi không có dữ liệu")
    void testLoadStatisticsEmptyData() {
        // 1. Tạo spy controller
        StatisticsController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        String period = "Tháng này";
        int year = 2026;

        // 3. Giả lập View
        when(mockView.getSelectedPeriod()).thenReturn(period);
        when(mockView.getSelectedYear()).thenReturn(year);

        // 4. Giả lập Model trả về dữ liệu rỗng
        Map<String, Object> emptyStats = new HashMap<>();
        emptyStats.put("total_value", 0.0);
        emptyStats.put("total_revenue", 0.0);
        emptyStats.put("profit", 0.0);
        emptyStats.put("profit_margin", 0.0);

        when(mockModel.getImportStatistics(any(Date.class), any(Date.class))).thenReturn(emptyStats);
        when(mockModel.getExportStatistics(any(Date.class), any(Date.class))).thenReturn(emptyStats);
        when(mockModel.getProfitStatistics(any(Date.class), any(Date.class))).thenReturn(emptyStats);
        when(mockModel.getTopSellingProducts(anyInt(), any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());
        when(mockModel.getMonthlyStatistics(year)).thenReturn(new ArrayList<>());

        // 5. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 6. Gọi hàm
        spyController.loadStatistics();

        // 7. Kiểm tra: updateSummary được gọi với giá trị 0
        verify(mockView, times(1)).updateSummary(0.0, 0.0, 0.0, 0.0);
    }
}