package com.warehouse.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class StatisticsPanel extends JPanel {
    private JComboBox<String> periodCombo;
    private JComboBox<Integer> yearCombo;
    private JLabel importTotalLabel, exportTotalLabel, profitLabel, profitMarginLabel;
    private JTable topProductsTable, monthlyTable;
    private DefaultTableModel topProductsModel, monthlyModel;
    private JButton refreshButton;

    public StatisticsPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel điều khiển
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Thống kê theo"));

        periodCombo = new JComboBox<>(new String[]{"Tháng này", "Tháng trước", "Năm nay", "Tùy chỉnh"});
        yearCombo = new JComboBox<>();
        refreshButton = new JButton("📊 Xem thống kê");
        refreshButton.setBackground(new Color(0, 102, 204));
        refreshButton.setForeground(Color.WHITE);

        controlPanel.add(new JLabel("Kỳ báo cáo:"));
        controlPanel.add(periodCombo);
        controlPanel.add(new JLabel("Năm:"));
        controlPanel.add(yearCombo);
        controlPanel.add(refreshButton);

        // Panel thống kê tổng quan
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Tổng quan"));
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        importTotalLabel = new JLabel("0 đ");
        exportTotalLabel = new JLabel("0 đ");
        profitLabel = new JLabel("0 đ");
        profitMarginLabel = new JLabel("0%");

        importTotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        exportTotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        profitLabel.setFont(new Font("Arial", Font.BOLD, 18));
        profitMarginLabel.setFont(new Font("Arial", Font.BOLD, 18));

        summaryPanel.add(createCard("💰 Tổng nhập", importTotalLabel));
        summaryPanel.add(createCard("💵 Tổng xuất", exportTotalLabel));
        summaryPanel.add(createCard("📈 Lợi nhuận", profitLabel));
        summaryPanel.add(createCard("📊 Biên lợi nhuận", profitMarginLabel));

        // Panel top sản phẩm
        String[] topColumns = {"Mã SP", "Tên sản phẩm", "Số lượng bán", "Doanh thu"};
        topProductsModel = new DefaultTableModel(topColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        topProductsTable = new JTable(topProductsModel);
        JScrollPane topScroll = new JScrollPane(topProductsTable);
        topScroll.setBorder(BorderFactory.createTitledBorder("🏆 Top sản phẩm bán chạy"));

        // Panel thống kê theo tháng
        String[] monthlyColumns = {"Tháng", "Nhập hàng", "Xuất hàng", "Lợi nhuận"};
        monthlyModel = new DefaultTableModel(monthlyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        monthlyTable = new JTable(monthlyModel);
        JScrollPane monthlyScroll = new JScrollPane(monthlyTable);
        monthlyScroll.setBorder(BorderFactory.createTitledBorder("Thống kê theo tháng"));

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScroll, monthlyScroll);
        splitPane.setResizeWeight(0.5);

        add(controlPanel, BorderLayout.NORTH);
        add(summaryPanel, BorderLayout.CENTER);
        add(splitPane, BorderLayout.SOUTH);

        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        splitPane.setPreferredSize(new Dimension(0, 400));
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valueLabel.setForeground(new Color(0, 102, 204));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);

        return panel;
    }

    // ============ GETTER METHODS ============

    public JComboBox<String> getPeriodCombo() {
        return periodCombo;
    }

    public JComboBox<Integer> getYearCombo() {
        return yearCombo;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public String getSelectedPeriod() {
        return (String) periodCombo.getSelectedItem();
    }

    public int getSelectedYear() {
        Integer year = (Integer) yearCombo.getSelectedItem();
        return year != null ? year : java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    }

    // ============ UPDATE METHODS ============

    public void setYears(java.util.List<Integer> years) {
        yearCombo.removeAllItems();
        for (Integer year : years) {
            yearCombo.addItem(year);
        }
    }

    public void updateSummary(double importTotal, double exportTotal, double profit, double profitMargin) {
        importTotalLabel.setText(String.format("%,.0f đ", importTotal));
        exportTotalLabel.setText(String.format("%,.0f đ", exportTotal));

        if (profit >= 0) {
            profitLabel.setText(String.format("+,.0f đ", profit));
            profitLabel.setForeground(new Color(0, 153, 0));
        } else {
            profitLabel.setText(String.format("%,.0f đ", profit));
            profitLabel.setForeground(Color.RED);
        }

        profitMarginLabel.setText(String.format("%.2f%%", profitMargin));
        if (profitMargin >= 0) {
            profitMarginLabel.setForeground(new Color(0, 153, 0));
        } else {
            profitMarginLabel.setForeground(Color.RED);
        }
    }

    public void updateTopProducts(java.util.List<Map<String, Object>> products) {
        topProductsModel.setRowCount(0);
        for (Map<String, Object> product : products) {
            topProductsModel.addRow(new Object[]{
                    product.get("code"),
                    product.get("name"),
                    product.get("total_sold"),
                    String.format("%,.0f đ", product.get("total_revenue"))
            });
        }
    }

    public void updateMonthlyStatistics(java.util.List<Map<String, Object>> monthlyStats) {
        monthlyModel.setRowCount(0);
        String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};

        for (int i = 0; i < monthlyStats.size(); i++) {
            Map<String, Object> stat = monthlyStats.get(i);
            double importValue = (double) stat.get("import_value");
            double exportValue = (double) stat.get("export_value");
            double profit = (double) stat.get("profit");

            monthlyModel.addRow(new Object[]{
                    monthNames[i],
                    String.format("%,.0f đ", importValue),
                    String.format("%,.0f đ", exportValue),
                    profit >= 0 ? String.format("+,.0f đ", profit) : String.format("%,.0f đ", profit)
            });
        }
    }
}