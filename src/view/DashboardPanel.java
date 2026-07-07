package com.warehouse.view;

import com.warehouse.model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private JLabel totalProductsLabel, totalSuppliersLabel, totalUsersLabel;
    private JLabel lowStockLabel, expiringLabel, totalValueLabel;
    private JTextArea recentActivityArea;
    private JTextArea lowStockArea;

    public DashboardPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel trên cùng - Tiêu đề
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("🏪 HỆ THỐNG QUẢN LÝ KHO THỰC PHẨM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Panel thống kê nhanh (dạng card - Grid 2x3)
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                "📊 THỐNG KÊ NHANH",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(0, 102, 204)
        ));

        totalProductsLabel = createStatCard(statsPanel, "📦 Tổng sản phẩm", "0", new Color(52, 152, 219));
        totalSuppliersLabel = createStatCard(statsPanel, "🏭 Nhà cung cấp", "0", new Color(46, 204, 113));
        totalUsersLabel = createStatCard(statsPanel, "👤 Người dùng", "0", new Color(155, 89, 182));
        totalValueLabel = createStatCard(statsPanel, "💰 Giá trị tồn kho", "0 đ", new Color(241, 196, 15));
        lowStockLabel = createStatCard(statsPanel, "⚠️ Hàng sắp hết (≤10)", "0", new Color(230, 126, 34));
        expiringLabel = createStatCard(statsPanel, "📅 Hàng sắp hết hạn", "0", new Color(231, 76, 60));

        // Panel bên dưới - Chia 2 cột
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        // Panel cảnh báo hàng tồn thấp
        JPanel lowStockPanel = new JPanel(new BorderLayout());
        lowStockPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 2),
                "⚠️ DANH SÁCH HÀNG SẮP HẾT (TỒN ≤ 10)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(230, 126, 34)
        ));

        lowStockArea = new JTextArea();
        lowStockArea.setEditable(false);
        lowStockArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lowStockArea.setBackground(new Color(255, 245, 235));
        JScrollPane lowStockScroll = new JScrollPane(lowStockArea);
        lowStockScroll.setPreferredSize(new Dimension(0, 200));
        lowStockPanel.add(lowStockScroll, BorderLayout.CENTER);

        // Panel hoạt động gần đây
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "📋 CẢNH BÁO & THÔNG BÁO",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(52, 152, 219)
        ));

        recentActivityArea = new JTextArea();
        recentActivityArea.setEditable(false);
        recentActivityArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        recentActivityArea.setBackground(new Color(235, 245, 255));
        JScrollPane activityScroll = new JScrollPane(recentActivityArea);
        activityScroll.setPreferredSize(new Dimension(0, 200));
        activityPanel.add(activityScroll, BorderLayout.CENTER);

        bottomPanel.add(lowStockPanel);
        bottomPanel.add(activityPanel);

        // Thêm các panel vào main
        add(titlePanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Set kích thước
        statsPanel.setPreferredSize(new Dimension(0, 200));
        bottomPanel.setPreferredSize(new Dimension(0, 250));
    }

    private JLabel createStatCard(JPanel panel, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        panel.add(card);

        return valueLabel;
    }

    private void loadData() {
        // Load dữ liệu từ các Model
        ProductModel productModel = new ProductModel();
        SupplierModel supplierModel = new SupplierModel();
        UserModel userModel = new UserModel();
        InventoryModel inventoryModel = new InventoryModel();

        // Thống kê số lượng
        int productCount = productModel.getAllProducts().size();
        int supplierCount = supplierModel.getAllSuppliers().size();
        int userCount = userModel.getAllUsers().size();

        // Thống kê tồn kho
        List<Map<String, Object>> lowStockList = inventoryModel.getLowStockProducts(10);
        List<Map<String, Object>> expiringList = inventoryModel.getExpiringProducts();
        Map<String, Double> summary = inventoryModel.getInventorySummary();

        double totalValue = summary.getOrDefault("total_value", 0.0);

        // Cập nhật label
        totalProductsLabel.setText(String.valueOf(productCount));
        totalSuppliersLabel.setText(String.valueOf(supplierCount));
        totalUsersLabel.setText(String.valueOf(userCount));
        totalValueLabel.setText(String.format("%,.0f đ", totalValue));
        lowStockLabel.setText(String.valueOf(lowStockList.size()));
        expiringLabel.setText(String.valueOf(expiringList.size()));

        // Hiển thị danh sách hàng tồn thấp
        StringBuilder lowStockText = new StringBuilder();
        if (lowStockList.isEmpty()) {
            lowStockText.append("   ✅ Không có sản phẩm nào tồn kho thấp!\n");
        } else {
            lowStockText.append(String.format("   %-15s %-30s %-10s\n", "Mã SP", "Tên sản phẩm", "Tồn kho"));
            lowStockText.append("   " + "=".repeat(60) + "\n");
            for (Map<String, Object> item : lowStockList) {
                lowStockText.append(String.format("   %-15s %-30s %,10d\n",
                        item.get("code"),
                        item.get("name"),
                        item.get("quantity")
                ));
            }
        }
        lowStockArea.setText(lowStockText.toString());

        // Hiển thị cảnh báo và thông báo
        StringBuilder alerts = new StringBuilder();

        alerts.append("📊 THÔNG TIN HỆ THỐNG:\n");
        alerts.append("   • Tổng số sản phẩm: ").append(productCount).append("\n");
        alerts.append("   • Tổng giá trị tồn kho: ").append(String.format("%,.0f", totalValue)).append(" đ\n");
        alerts.append("\n");

        if (!lowStockList.isEmpty()) {
            alerts.append("⚠️ CẢNH BÁO TỒN KHO THẤP:\n");
            alerts.append("   • Có ").append(lowStockList.size()).append(" sản phẩm tồn kho ≤ 10!\n");
            alerts.append("   • Cần nhập hàng ngay để tránh gián đoạn kinh doanh.\n");
            alerts.append("\n");
        }

        if (!expiringList.isEmpty()) {
            alerts.append("📅 CẢNH BÁO HẾT HẠN:\n");
            alerts.append("   • Có ").append(expiringList.size()).append(" sản phẩm sắp hết hạn (trong 30 ngày)!\n");
            alerts.append("   • Kiểm tra và xử lý hàng sắp hết hạn.\n");
            alerts.append("\n");
        }

        if (lowStockList.isEmpty() && expiringList.isEmpty()) {
            alerts.append("✅ HỆ THỐNG HOẠT ĐỘNG TỐT:\n");
            alerts.append("   • Không có cảnh báo tồn kho thấp.\n");
            alerts.append("   • Không có sản phẩm sắp hết hạn.\n");
        }

        alerts.append("\n");
        alerts.append("🕐 Cập nhật lúc: ").append(new java.util.Date());

        recentActivityArea.setText(alerts.toString());
    }

    // Refresh dữ liệu
    public void refresh() {
        loadData();
    }

    // ============ GETTERS DÀNH CHO TEST ============
    public JLabel getTotalProductsLabel() {
        return totalProductsLabel;
    }

    public JLabel getTotalSuppliersLabel() {
        return totalSuppliersLabel;
    }

    public JLabel getTotalUsersLabel() {
        return totalUsersLabel;
    }

    public JLabel getLowStockLabel() {
        return lowStockLabel;
    }

    public JLabel getExpiringLabel() {
        return expiringLabel;
    }

    public JLabel getTotalValueLabel() {
        return totalValueLabel;
    }

    public JTextArea getRecentActivityArea() {
        return recentActivityArea;
    }

    public JTextArea getLowStockArea() {
        return lowStockArea;
    }
}