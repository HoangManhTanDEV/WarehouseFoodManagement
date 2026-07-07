package com.warehouse.model;

import org.junit.jupiter.api.*;
import java.sql.Date;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatisticsModelTest {

    private static StatisticsModel statisticsModel;

    @BeforeAll
    static void setUp() {
        statisticsModel = new StatisticsModel();
        System.out.println("✅ Bắt đầu kiểm thử StatisticsModel");
    }

    @Test
    @Order(1)
    @DisplayName("TC01: Lấy danh sách các năm có dữ liệu")
    void testGetAvailableYears() {
        List<Integer> years = statisticsModel.getAvailableYears();
        assertNotNull(years, "Danh sách năm không được null");
        assertTrue(years.size() > 0, "Phải có ít nhất 1 năm");
        System.out.println("✅ Các năm có dữ liệu: " + years);
    }

    @Test
    @Order(2)
    @DisplayName("TC02: Thống kê nhập hàng 30 ngày gần nhất")
    void testGetImportStatistics() {
        Date endDate = new Date(System.currentTimeMillis());
        Date startDate = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);

        Map<String, Object> stats = statisticsModel.getImportStatistics(startDate, endDate);
        assertNotNull(stats, "Thống kê nhập hàng không được null");

        double totalValue = (double) stats.getOrDefault("total_value", 0.0);
        int totalReceipts = (int) stats.getOrDefault("total_receipts", 0);

        assertTrue(totalValue >= 0, "Tổng giá trị nhập không được âm");
        assertTrue(totalReceipts >= 0, "Số phiếu nhập không được âm");

        System.out.println("✅ Tổng nhập 30 ngày: " + String.format("%,.0f đ", totalValue));
        System.out.println("✅ Số phiếu nhập: " + totalReceipts);
    }

    @Test
    @Order(3)
    @DisplayName("TC03: Thống kê xuất hàng 30 ngày gần nhất")
    void testGetExportStatistics() {
        Date endDate = new Date(System.currentTimeMillis());
        Date startDate = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);

        Map<String, Object> stats = statisticsModel.getExportStatistics(startDate, endDate);
        assertNotNull(stats, "Thống kê xuất hàng không được null");

        double totalRevenue = (double) stats.getOrDefault("total_revenue", 0.0);
        int totalReceipts = (int) stats.getOrDefault("total_receipts", 0);

        assertTrue(totalRevenue >= 0, "Tổng doanh thu không được âm");
        assertTrue(totalReceipts >= 0, "Số phiếu xuất không được âm");

        System.out.println("✅ Tổng xuất 30 ngày: " + String.format("%,.0f đ", totalRevenue));
        System.out.println("✅ Số phiếu xuất: " + totalReceipts);
    }

    @Test
    @Order(4)
    @DisplayName("TC04: Thống kê lợi nhuận 30 ngày gần nhất")
    void testGetProfitStatistics() {
        Date endDate = new Date(System.currentTimeMillis());
        Date startDate = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);

        Map<String, Object> stats = statisticsModel.getProfitStatistics(startDate, endDate);
        assertNotNull(stats, "Thống kê lợi nhuận không được null");

        double totalCost = (double) stats.getOrDefault("total_cost", 0.0);
        double totalRevenue = (double) stats.getOrDefault("total_revenue", 0.0);
        double profit = (double) stats.getOrDefault("profit", 0.0);
        double profitMargin = (double) stats.getOrDefault("profit_margin", 0.0);

        assertTrue(totalCost >= 0, "Tổng chi phí không được âm");
        assertTrue(totalRevenue >= 0, "Tổng doanh thu không được âm");

        System.out.println("✅ Tổng chi phí: " + String.format("%,.0f đ", totalCost));
        System.out.println("✅ Tổng doanh thu: " + String.format("%,.0f đ", totalRevenue));
        System.out.println("✅ Lợi nhuận: " + String.format("%,.0f đ", profit));
        System.out.println("✅ Biên lợi nhuận: " + String.format("%.2f%%", profitMargin));
    }

    @Test
    @Order(5)
    @DisplayName("TC05: Top sản phẩm bán chạy")
    void testGetTopSellingProducts() {
        Date endDate = new Date(System.currentTimeMillis());
        Date startDate = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);

        List<Map<String, Object>> topProducts = statisticsModel.getTopSellingProducts(5, startDate, endDate);
        assertNotNull(topProducts, "Danh sách top sản phẩm không được null");

        System.out.println("✅ Top 5 sản phẩm bán chạy nhất:");
        if (topProducts.isEmpty()) {
            System.out.println("   (Chưa có dữ liệu bán hàng trong 30 ngày)");
        } else {
            int rank = 1;
            for (Map<String, Object> item : topProducts) {
                System.out.println("   " + rank + ". " + item.get("code") + " - " + item.get("name") +
                        ": " + item.get("total_sold") + " sản phẩm");
                rank++;
            }
        }
    }

    @Test
    @Order(6)
    @DisplayName("TC06: Thống kê theo tháng trong năm hiện tại")
    void testGetMonthlyStatistics() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Map<String, Object>> monthlyStats = statisticsModel.getMonthlyStatistics(currentYear);

        assertNotNull(monthlyStats, "Thống kê theo tháng không được null");
        assertEquals(12, monthlyStats.size(), "Phải có 12 tháng trong năm");

        System.out.println("✅ Thống kê theo tháng trong năm " + currentYear + ":");
        for (int i = 0; i < monthlyStats.size(); i++) {
            Map<String, Object> stat = monthlyStats.get(i);
            double importValue = (double) stat.get("import_value");
            double exportValue = (double) stat.get("export_value");
            double profit = (double) stat.get("profit");

            String monthName = "Tháng " + (i + 1);
            System.out.printf("   %-10s | Nhập: %,.0f | Xuất: %,.0f | LN: %,.0f%n",
                    monthName, importValue, exportValue, profit);
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("✅ Kết thúc kiểm thử StatisticsModel");
    }
}