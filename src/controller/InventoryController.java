package com.warehouse.controller;

import com.warehouse.model.InventoryModel;
import com.warehouse.view.InventoryPanel;
import com.warehouse.utils.ExcelExporter;
import javax.swing.*;
import java.util.*;

public class InventoryController {
    private InventoryPanel inventoryPanel;
    private InventoryModel inventoryModel;

    // Constructor chính - dùng trong ứng dụng
    public InventoryController(InventoryPanel inventoryPanel, InventoryModel inventoryModel) {
        this.inventoryPanel = inventoryPanel;
        this.inventoryModel = inventoryModel;

        loadInventoryData();
        loadSummary();

        this.inventoryPanel.getSearchButton().addActionListener(e -> searchInventory());
        this.inventoryPanel.getRefreshButton().addActionListener(e -> refreshAll());
        this.inventoryPanel.getLowStockButton().addActionListener(e -> showLowStock());
        this.inventoryPanel.getExpiringButton().addActionListener(e -> showExpiring());
        this.inventoryPanel.getExportExcelButton().addActionListener(e -> exportToExcel());
    }

    // ============ CONSTRUCTOR DÀNH CHO TEST ============
    public InventoryController(InventoryPanel inventoryPanel, InventoryModel inventoryModel, boolean forTest) {
        this.inventoryPanel = inventoryPanel;
        this.inventoryModel = inventoryModel;
        // KHÔNG load dữ liệu và đăng ký sự kiện
    }

    // ============ CÁC METHOD CÔNG KHAI ============
    public void loadInventoryData() {
        inventoryPanel.refreshInventoryTable(inventoryModel.getAllInventory());
    }

    public void loadSummary() {
        inventoryPanel.updateSummary(inventoryModel.getInventorySummary());
    }

    public void searchInventory() {
        String keyword = inventoryPanel.getSearchKeyword();
        if (keyword.trim().isEmpty()) {
            loadInventoryData();
        } else {
            inventoryPanel.refreshInventoryTable(inventoryModel.searchInventory(keyword));
        }
    }

    public void refreshAll() {
        loadInventoryData();
        loadSummary();
        showMessage("Đã làm mới dữ liệu tồn kho!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showLowStock() {
        List<Map<String, Object>> lowStock = inventoryModel.getLowStockProducts(10);
        if (lowStock.isEmpty()) {
            showMessage("Không có sản phẩm nào tồn kho thấp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            inventoryPanel.showLowStockProducts(lowStock);
        }
    }

    public void showExpiring() {
        List<Map<String, Object>> expiring = inventoryModel.getExpiringProducts();
        if (expiring.isEmpty()) {
            showMessage("Không có sản phẩm nào sắp hết hạn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            inventoryPanel.showExpiringProducts(expiring);
        }
    }

    public void exportToExcel() {
        List<Map<String, Object>> inventory = inventoryModel.getAllInventory();
        if (inventory.isEmpty()) {
            showMessage("Không có dữ liệu tồn kho để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ExcelExporter.exportInventoryToExcel(inventory);
        showMessage("Xuất Excel thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============ CÁC METHOD HỖ TRỢ (CÓ THỂ OVERRIDE KHI TEST) ============
    protected void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }
}