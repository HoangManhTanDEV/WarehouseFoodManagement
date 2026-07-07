package com.warehouse.view;

import com.warehouse.controller.*;
import com.warehouse.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MainView extends JFrame {
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;
    private Map<String, String> userInfo;

    public MainView(Map<String, String> userInfo) {
        this.userInfo = userInfo;
        initComponents();
        DashboardPanel dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("🏠 DASHBOARD", dashboardPanel);
        setTitle("QUẢN LÝ KHO THỰC PHẨM - Chào " + userInfo.get("fullname"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel trên cùng
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.setPreferredSize(new Dimension(0, 60));

        welcomeLabel = new JLabel("  Chào mừng: " + userInfo.get("fullname") + " | Vai trò: " + userInfo.get("role"));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("ĐĂNG XUẤT");
        logoutButton.setBackground(new Color(204, 0, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.addActionListener(e -> logout());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // TabbedPane chính
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // 1. SẢN PHẨM
        ProductPanel productPanel = new ProductPanel();
        ProductModel productModel = new ProductModel();
        new ProductController(productPanel, productModel);
        tabbedPane.addTab("📦 SẢN PHẨM", productPanel);

        // 2. NHÀ CUNG CẤP
        SupplierPanel supplierPanel = new SupplierPanel();
        SupplierModel supplierModel = new SupplierModel();
        new SupplierController(supplierPanel, supplierModel);
        tabbedPane.addTab("🏭 NHÀ CUNG CẤP", supplierPanel);

        // 3. NHẬP HÀNG
        ImportPanel importPanel = new ImportPanel();
        ImportModel importModel = new ImportModel();
        new ImportController(importPanel, importModel, userInfo);
        tabbedPane.addTab("📥 NHẬP HÀNG", importPanel);

        // 4. XUẤT HÀNG
        ExportPanel exportPanel = new ExportPanel();
        ExportModel exportModel = new ExportModel();
        new ExportController(exportPanel, exportModel, userInfo);
        tabbedPane.addTab("📤 XUẤT HÀNG", exportPanel);

        // 5. TỒN KHO
        InventoryPanel inventoryPanel = new InventoryPanel();
        InventoryModel inventoryModel = new InventoryModel();
        new InventoryController(inventoryPanel, inventoryModel);
        tabbedPane.addTab("📊 TỒN KHO", inventoryPanel);

        // 6. THỐNG KÊ
        StatisticsPanel statisticsPanel = new StatisticsPanel();
        StatisticsModel statisticsModel = new StatisticsModel();
        new StatisticsController(statisticsPanel, statisticsModel);
        tabbedPane.addTab("📈 THỐNG KÊ", statisticsPanel);

        // 7. TÀI KHOẢN (Chỉ hiển thị nếu là Admin)
        if (userInfo.get("role").equals("admin")) {
            UserPanel userPanel = new UserPanel(userInfo);
            UserModel userModel = new UserModel();
            new UserController(userPanel, userModel);
            tabbedPane.addTab("👤 TÀI KHOẢN", userPanel);
        }

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginView().setVisible(true);
            this.dispose();
        }
    }

    // ============ GETTERS DÀNH CHO TEST ============
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }

    public Map<String, String> getUserInfo() {
        return userInfo;
    }
}