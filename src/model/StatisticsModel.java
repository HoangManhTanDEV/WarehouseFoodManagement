package com.warehouse.model;

import com.warehouse.utils.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class StatisticsModel {

    // Thống kê nhập hàng theo khoảng thời gian
    public Map<String, Object> getImportStatistics(java.sql.Date startDate, java.sql.Date endDate) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_receipts, " +
                "SUM(total_amount) as total_value, " +
                "SUM(CASE WHEN MONTH(import_date) = MONTH(CURDATE()) AND YEAR(import_date) = YEAR(CURDATE()) THEN total_amount ELSE 0 END) as this_month_value, " +
                "SUM(CASE WHEN MONTH(import_date) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) AND YEAR(import_date) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) THEN total_amount ELSE 0 END) as last_month_value " +
                "FROM import_receipts " +
                "WHERE import_date BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                stats.put("total_receipts", rs.getInt("total_receipts"));
                stats.put("total_value", rs.getDouble("total_value"));
                stats.put("this_month_value", rs.getDouble("this_month_value"));
                stats.put("last_month_value", rs.getDouble("last_month_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Thống kê xuất hàng theo khoảng thời gian
    public Map<String, Object> getExportStatistics(java.sql.Date startDate, java.sql.Date endDate) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_receipts, " +
                "SUM(total_amount) as total_revenue, " +
                "SUM(CASE WHEN MONTH(export_date) = MONTH(CURDATE()) AND YEAR(export_date) = YEAR(CURDATE()) THEN total_amount ELSE 0 END) as this_month_revenue, " +
                "SUM(CASE WHEN MONTH(export_date) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) AND YEAR(export_date) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) THEN total_amount ELSE 0 END) as last_month_revenue " +
                "FROM export_receipts " +
                "WHERE export_date BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                stats.put("total_receipts", rs.getInt("total_receipts"));
                stats.put("total_revenue", rs.getDouble("total_revenue"));
                stats.put("this_month_revenue", rs.getDouble("this_month_revenue"));
                stats.put("last_month_revenue", rs.getDouble("last_month_revenue"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Thống kê lợi nhuận
    public Map<String, Object> getProfitStatistics(java.sql.Date startDate, java.sql.Date endDate) {
        Map<String, Object> stats = new HashMap<>();

        // Tính tổng vốn nhập hàng
        String importSql = "SELECT SUM(ed.quantity * ed.unit_price) as total_import_cost " +
                "FROM import_details ed " +
                "JOIN import_receipts ir ON ed.receipt_id = ir.id " +
                "WHERE ir.import_date BETWEEN ? AND ?";

        // Tính tổng doanh thu xuất hàng
        String exportSql = "SELECT SUM(ed.quantity * ed.unit_price) as total_revenue " +
                "FROM export_details ed " +
                "JOIN export_receipts er ON ed.receipt_id = er.id " +
                "WHERE er.export_date BETWEEN ? AND ?";

        double totalCost = 0;
        double totalRevenue = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Tính tổng vốn
            try (PreparedStatement pstmt = conn.prepareStatement(importSql)) {
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    totalCost = rs.getDouble("total_import_cost");
                }
            }

            // Tính tổng doanh thu
            try (PreparedStatement pstmt = conn.prepareStatement(exportSql)) {
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    totalRevenue = rs.getDouble("total_revenue");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        double profit = totalRevenue - totalCost;
        double profitMargin = totalRevenue > 0 ? (profit / totalRevenue) * 100 : 0;

        stats.put("total_cost", totalCost);
        stats.put("total_revenue", totalRevenue);
        stats.put("profit", profit);
        stats.put("profit_margin", profitMargin);

        return stats;
    }
/*
    // Thống kê top sản phẩm bán chạy
    public List<Map<String, Object>> getTopSellingProducts(int limit, java.sql.Date startDate, java.sql.Date endDate) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        String sql = "SELECT p.id, p.code, p.name, SUM(ed.quantity) as total_sold, " +
                "SUM(ed.quantity * ed.unit_price) as total_revenue " +
                "FROM export_details ed " +
                "JOIN export_receipts er ON ed.receipt_id = er.id " +
                "JOIN products p ON ed.product_id = p.id " +
                "WHERE er.export_date BETWEEN ? AND ? " +
                "GROUP BY p.id, p.code, p.name " +
                "ORDER BY total_sold DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            pstmt.setInt(3, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("code", rs.getString("code"));
                product.put("name", rs.getString("name"));
                product.put("total_sold", rs.getInt("total_sold"));
                product.put("total_revenue", rs.getDouble("total_revenue"));
                topProducts.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topProducts;
    }
*/
    // Thống kê nhập xuất theo tháng
    public List<Map<String, Object>> getMonthlyStatistics(int year) {
        List<Map<String, Object>> monthlyStats = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("month", month);
            stat.put("import_value", 0.0);
            stat.put("export_value", 0.0);
            stat.put("profit", 0.0);
            monthlyStats.add(stat);
        }

        // Thống kê nhập theo tháng
        String importSql = "SELECT MONTH(import_date) as month, SUM(total_amount) as total " +
                "FROM import_receipts " +
                "WHERE YEAR(import_date) = ? " +
                "GROUP BY MONTH(import_date)";

        // Thống kê xuất theo tháng
        String exportSql = "SELECT MONTH(export_date) as month, SUM(total_amount) as total " +
                "FROM export_receipts " +
                "WHERE YEAR(export_date) = ? " +
                "GROUP BY MONTH(export_date)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Lấy dữ liệu nhập
            try (PreparedStatement pstmt = conn.prepareStatement(importSql)) {
                pstmt.setInt(1, year);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int month = rs.getInt("month");
                    double total = rs.getDouble("total");
                    monthlyStats.get(month - 1).put("import_value", total);
                }
            }

            // Lấy dữ liệu xuất
            try (PreparedStatement pstmt = conn.prepareStatement(exportSql)) {
                pstmt.setInt(1, year);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int month = rs.getInt("month");
                    double total = rs.getDouble("total");
                    monthlyStats.get(month - 1).put("export_value", total);
                }
            }

            // Tính lợi nhuận
            for (Map<String, Object> stat : monthlyStats) {
                double exportValue = (double) stat.get("export_value");
                double importValue = (double) stat.get("import_value");
                stat.put("profit", exportValue - importValue);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyStats;
    }

    // Lấy danh sách các năm có dữ liệu
    public List<Integer> getAvailableYears() {
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(import_date) as year FROM import_receipts " +
                "UNION " +
                "SELECT DISTINCT YEAR(export_date) as year FROM export_receipts " +
                "ORDER BY year DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Nếu không có dữ liệu, thêm năm hiện tại
        if (years.isEmpty()) {
            years.add(Calendar.getInstance().get(Calendar.YEAR));
        }

        return years;
    }
}
