package com.warehouse.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class InventoryPanel extends JPanel {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton, refreshButton, lowStockButton, expiringButton, exportExcelButton;
    private JLabel summaryLabel;
    private JTabbedPane tabbedPane;

    public InventoryPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo tabbed pane cho các chức năng
        tabbedPane = new JTabbedPane();

        // Tab 1: Tất cả tồn kho
        tabbedPane.addTab("📦 Tất cả sản phẩm", createInventoryTab());

        // Tab 2: Hàng tồn thấp
        tabbedPane.addTab("⚠️ Hàng sắp hết", createLowStockTab());

        // Tab 3: Hàng sắp hết hạn
        tabbedPane.addTab("📅 Hàng sắp hết hạn", createExpiringTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createInventoryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        searchButton = new JButton("🔍 Tìm");
        refreshButton = new JButton("🔄 Làm mới");

        // ============ THÊM NÚT XUẤT EXCEL ============
        exportExcelButton = new JButton("📊 Xuất Excel");
        exportExcelButton.setFont(new Font("Arial", Font.BOLD, 13));
        exportExcelButton.setBackground(new Color(0, 102, 204));
        exportExcelButton.setForeground(Color.WHITE);
        // ============================================

        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        searchPanel.add(exportExcelButton);

        // Panel thống kê nhanh
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryLabel = new JLabel("Đang tải dữ liệu...");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryLabel.setForeground(new Color(0, 102, 204));
        statsPanel.add(summaryLabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);

        // Bảng hiển thị
        String[] columns = {"ID", "Mã SP", "Tên sản phẩm", "Loại", "Đơn vị",
                "SL tồn", "Giá nhập", "Giá bán", "HSD", "Nhà cung cấp", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        inventoryTable.setRowHeight(25);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Đặt màu cho các dòng theo trạng thái
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) tableModel.getValueAt(row, 10);
                    if (status != null) {
                        if (status.contains("Hết hạn")) {
                            c.setBackground(new Color(255, 200, 200));
                            c.setForeground(Color.RED);
                        } else if (status.contains("CẢNH BÁO") || status.contains("Sắp hết")) {
                            c.setBackground(new Color(255, 255, 200));
                            c.setForeground(new Color(255, 140, 0));
                        } else if (status.contains("Sắp hết hạn")) {
                            c.setBackground(new Color(255, 220, 180));
                            c.setForeground(new Color(255, 100, 0));
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách tồn kho"));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLowStockTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lowStockButton = new JButton("🔍 Xem hàng tồn thấp");
        lowStockButton.setBackground(new Color(255, 140, 0));
        lowStockButton.setForeground(Color.WHITE);
        buttonPanel.add(lowStockButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable()), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExpiringTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expiringButton = new JButton("📅 Xem hàng sắp hết hạn");
        expiringButton.setBackground(new Color(0, 102, 204));
        expiringButton.setForeground(Color.WHITE);
        buttonPanel.add(expiringButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable()), BorderLayout.CENTER);

        return panel;
    }

    public void refreshInventoryTable(java.util.List<Map<String, Object>> inventory) {
        tableModel.setRowCount(0);
        for (Map<String, Object> item : inventory) {
            tableModel.addRow(new Object[]{
                    item.get("id"),
                    item.get("code"),
                    item.get("name"),
                    item.get("category"),
                    item.get("unit"),
                    item.get("quantity"),
                    String.format("%,.0f đ", item.get("import_price")),
                    String.format("%,.0f đ", item.get("sell_price")),
                    item.get("expiry_date") != null ? item.get("expiry_date") : "Chưa có",
                    item.get("supplier_name") != null ? item.get("supplier_name") : "Chưa có",
                    item.get("status")
            });
        }
    }

    public void updateSummary(Map<String, Double> summary) {
        double totalValue = summary.getOrDefault("total_value", 0.0);
        double totalQuantity = summary.getOrDefault("total_quantity", 0.0);
        double totalProducts = summary.getOrDefault("total_products", 0.0);

        summaryLabel.setText(String.format(
                "📊 Tổng số SP: %.0f | Tổng SL: %.0f | Tổng giá trị: %,.0f đ",
                totalProducts, totalQuantity, totalValue
        ));
    }

    public void showLowStockProducts(java.util.List<Map<String, Object>> lowStock) {
        // Tạo dialog hiển thị hàng tồn thấp
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Hàng tồn kho thấp", true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"Mã SP", "Tên sản phẩm", "SL tồn", "Giá bán", "Nhà cung cấp"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        for (Map<String, Object> item : lowStock) {
            model.addRow(new Object[]{
                    item.get("code"),
                    item.get("name"),
                    item.get("quantity"),
                    String.format("%,.0f đ", item.get("sell_price")),
                    item.get("supplier_name")
            });
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    public void showExpiringProducts(java.util.List<Map<String, Object>> expiring) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Hàng sắp hết hạn", true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"Mã SP", "Tên sản phẩm", "SL tồn", "Hạn sử dụng", "Còn lại (ngày)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        for (Map<String, Object> item : expiring) {
            model.addRow(new Object[]{
                    item.get("code"),
                    item.get("name"),
                    item.get("quantity"),
                    item.get("expiry_date"),
                    item.get("days_left")
            });
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    public String getSearchKeyword() {
        return searchField.getText();
    }

    public JButton getSearchButton() { return searchButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getLowStockButton() { return lowStockButton; }
    public JButton getExpiringButton() { return expiringButton; }

    // ============ GETTER CHO NÚT XUẤT EXCEL ============
    public JButton getExportExcelButton() { return exportExcelButton; }
}