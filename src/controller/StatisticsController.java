package com.warehouse.controller;

import com.warehouse.model.StatisticsModel;
import com.warehouse.view.StatisticsPanel;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class StatisticsController {
    private StatisticsPanel statisticsPanel;
    private StatisticsModel statisticsModel;

    // Constructor chính - dùng trong ứng dụng
    public StatisticsController(StatisticsPanel statisticsPanel, StatisticsModel statisticsModel) {
        this.statisticsPanel = statisticsPanel;
        this.statisticsModel = statisticsModel;

        loadYears();
        loadStatistics();

        this.statisticsPanel.getRefreshButton().addActionListener(e -> loadStatistics());
        addPeriodComboListener();
    }

    // ============ CONSTRUCTOR DÀNH CHO TEST ============
    public StatisticsController(StatisticsPanel statisticsPanel, StatisticsModel statisticsModel, boolean forTest) {
        this.statisticsPanel = statisticsPanel;
        this.statisticsModel = statisticsModel;
        // KHÔNG load dữ liệu và đăng ký sự kiện
    }

    // ============ CÁC METHOD CÔNG KHAI ============
    public void loadYears() {
        statisticsPanel.setYears(statisticsModel.getAvailableYears());
    }

    public void loadStatistics() {
        String period = statisticsPanel.getSelectedPeriod();
        int year = statisticsPanel.getSelectedYear();

        java.sql.Date startDate;
        java.sql.Date endDate = new java.sql.Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();

        switch (period) {
            case "Tháng này":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = new java.sql.Date(cal.getTimeInMillis());
                break;
            case "Tháng trước":
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = new java.sql.Date(cal.getTimeInMillis());
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate = new java.sql.Date(cal.getTimeInMillis());
                break;
            case "Năm nay":
                cal.set(Calendar.DAY_OF_YEAR, 1);
                startDate = new java.sql.Date(cal.getTimeInMillis());
                break;
            default:
                cal.set(Calendar.DAY_OF_YEAR, 1);
                startDate = new java.sql.Date(cal.getTimeInMillis());
        }

        var importStats = statisticsModel.getImportStatistics(startDate, endDate);
        var exportStats = statisticsModel.getExportStatistics(startDate, endDate);
        var profitStats = statisticsModel.getProfitStatistics(startDate, endDate);

        double importTotal = (double) importStats.getOrDefault("total_value", 0.0);
        double exportTotal = (double) exportStats.getOrDefault("total_revenue", 0.0);
        double profit = (double) profitStats.getOrDefault("profit", 0.0);
        double profitMargin = (double) profitStats.getOrDefault("profit_margin", 0.0);

        statisticsPanel.updateSummary(importTotal, exportTotal, profit, profitMargin);

        var topProducts = statisticsModel.getTopSellingProducts(10, startDate, endDate);
        statisticsPanel.updateTopProducts(topProducts);

        var monthlyStats = statisticsModel.getMonthlyStatistics(year);
        statisticsPanel.updateMonthlyStatistics(monthlyStats);
    }

    // ============ CÁC METHOD HỖ TRỢ (CÓ THỂ OVERRIDE KHI TEST) ============
    private void addPeriodComboListener() {
        ActionListener periodListener = e -> loadStatistics();
        JComboBox<String> periodCombo = statisticsPanel.getPeriodCombo();
        if (periodCombo != null) {
            periodCombo.addActionListener(periodListener);
        }
    }

    protected void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }
}