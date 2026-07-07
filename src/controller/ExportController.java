package com.warehouse.controller;

import com.warehouse.model.ExportModel;
import com.warehouse.view.ExportPanel;
import com.warehouse.utils.ExcelExporter;
import javax.swing.*;
import java.util.*;

public class ExportController {
    private ExportPanel exportPanel;
    private ExportModel exportModel;
    private Map<String, String> userInfo;

    // Constructor chính - dùng trong ứng dụng
    public ExportController(ExportPanel exportPanel, ExportModel exportModel, Map<String, String> userInfo) {
        this.exportPanel = exportPanel;
        this.exportModel = exportModel;
        this.userInfo = userInfo;

        loadInitialData();

        this.exportPanel.getCreateButton().addActionListener(e -> createExportReceipt());
        this.exportPanel.getAddProductButton().addActionListener(e -> addToCart());
        this.exportPanel.getRemoveProductButton().addActionListener(e -> removeFromCart());
        this.exportPanel.getExportExcelButton().addActionListener(e -> exportToExcel());
    }

    // ============ CONSTRUCTOR DÀNH CHO TEST ============
    public ExportController(ExportPanel exportPanel, ExportModel exportModel, Map<String, String> userInfo, boolean forTest) {
        this.exportPanel = exportPanel;
        this.exportModel = exportModel;
        this.userInfo = userInfo;
        // KHÔNG load dữ liệu và đăng ký sự kiện
    }

    // ============ CÁC METHOD CÔNG KHAI ============
    public void loadInitialData() {
        exportPanel.refreshProductTable(exportModel.getAvailableProducts());
        exportPanel.setReceiptCode(exportModel.generateReceiptCode());
    }

    public void addToCart() {
        int productId = exportPanel.getSelectedProductId();
        if (productId == -1) {
            showMessage("Vui lòng chọn sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Object> selectedProduct = null;
        for (Map<String, Object> product : exportModel.getAvailableProducts()) {
            if ((int) product.get("id") == productId) {
                selectedProduct = product;
                break;
            }
        }

        if (selectedProduct != null) {
            String quantityStr = showInputDialog("Nhập số lượng xuất (Tồn: " + selectedProduct.get("quantity") + "):");
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    exportPanel.addToCart(selectedProduct, quantity);
                } catch (NumberFormatException e) {
                    showMessage("Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void removeFromCart() {
        int selectedRow = exportPanel.getCartTable().getSelectedRow();
        if (selectedRow >= 0 && selectedRow < exportPanel.getCartDetails().size()) {
            Map<String, Object> item = exportPanel.getCartDetails().get(selectedRow);
            int confirm = showConfirmDialog("Xóa " + item.get("product_name") + " khỏi giỏ?");
            if (confirm == JOptionPane.YES_OPTION) {
                exportPanel.removeFromCart((int) item.get("product_id"));
            }
        } else {
            showMessage("Vui lòng chọn sản phẩm cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createExportReceipt() {
        if (exportPanel.getCartDetails().isEmpty()) {
            showMessage("Giỏ hàng trống! Vui lòng thêm sản phẩm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String customerName = exportPanel.getCustomerName();
        if (customerName.trim().isEmpty()) {
            showMessage("Vui lòng nhập tên khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", exportPanel.getReceiptCodeField().getText());
        receipt.put("customer_name", customerName);
        receipt.put("total_amount", exportPanel.getTotalAmount());
        receipt.put("user_id", Integer.parseInt(userInfo.get("id")));
        receipt.put("note", "Xuất kho");

        boolean success = exportModel.createExportReceipt(receipt, exportPanel.getCartDetails());

        if (success) {
            showMessage("Xuất hàng thành công!\nĐã cập nhật tồn kho.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            exportPanel.clearCart();
            exportPanel.refreshProductTable(exportModel.getAvailableProducts());
            exportPanel.setReceiptCode(exportModel.generateReceiptCode());
        } else {
            showMessage("Xuất hàng thất bại!\nKiểm tra lại tồn kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Xuất Excel
    public void exportToExcel() {
        if (exportPanel.getCartDetails().isEmpty()) {
            showMessage("Không có dữ liệu để xuất Excel!\nVui lòng thêm sản phẩm vào giỏ hàng.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ExcelExporter.exportExportReceiptToExcel(buildReceiptData(), exportPanel.getCartDetails());
            showMessage("Xuất Excel thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showMessage("Xuất Excel thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============ CÁC METHOD HỖ TRỢ (CÓ THỂ OVERRIDE KHI TEST) ============
    protected void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }

    protected String showInputDialog(String message) {
        return JOptionPane.showInputDialog(message);
    }

    protected int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(null, message, "Xác nhận", JOptionPane.YES_NO_OPTION);
    }

    protected Map<String, Object> buildReceiptData() {
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", exportPanel.getReceiptCodeField().getText());
        receipt.put("export_date", new java.util.Date());
        receipt.put("customer_name", exportPanel.getCustomerName().isEmpty() ? "Khách lẻ" : exportPanel.getCustomerName());
        receipt.put("user_name", userInfo.get("fullname"));

        // Thêm mã sản phẩm cho chi tiết
        for (Map<String, Object> detail : exportPanel.getCartDetails()) {
            for (Map<String, Object> product : exportModel.getAvailableProducts()) {
                if ((int) product.get("id") == (int) detail.get("product_id")) {
                    detail.put("code", product.get("code"));
                    break;
                }
            }
        }
        return receipt;
    }
}