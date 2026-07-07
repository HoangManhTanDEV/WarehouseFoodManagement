package com.warehouse.controller;

import com.warehouse.model.ImportModel;
import com.warehouse.view.ImportPanel;
import com.warehouse.utils.EmailSender;
import javax.swing.*;
import java.util.*;

public class ImportController {
    private ImportPanel importPanel;
    private ImportModel importModel;
    private Map<String, String> userInfo;

    // Constructor chính - dùng trong ứng dụng
    public ImportController(ImportPanel importPanel, ImportModel importModel, Map<String, String> userInfo) {
        this.importPanel = importPanel;
        this.importModel = importModel;
        this.userInfo = userInfo;

        loadInitialData();

        this.importPanel.getCreateButton().addActionListener(e -> createImportReceipt());
        this.importPanel.getAddProductButton().addActionListener(e -> addToCart());
        this.importPanel.getRemoveProductButton().addActionListener(e -> removeFromCart());
    }

    // ============ CONSTRUCTOR DÀNH CHO TEST ============
    public ImportController(ImportPanel importPanel, ImportModel importModel, Map<String, String> userInfo, boolean forTest) {
        this.importPanel = importPanel;
        this.importModel = importModel;
        this.userInfo = userInfo;
        // KHÔNG load dữ liệu và đăng ký sự kiện
    }

    // ============ CÁC METHOD CÔNG KHAI ============
    public void loadInitialData() {
        importPanel.refreshProductTable(importModel.getAllProducts());
        importPanel.refreshSupplierCombo(importModel.getAllSuppliers());
        importPanel.setReceiptCode(importModel.generateReceiptCode());
    }

    public void addToCart() {
        int productId = importPanel.getSelectedProductId();
        if (productId == -1) {
            showMessage("Vui lòng chọn sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Object> selectedProduct = null;
        for (Map<String, Object> product : importModel.getAllProducts()) {
            if ((int) product.get("id") == productId) {
                selectedProduct = product;
                break;
            }
        }

        if (selectedProduct != null) {
            String quantityStr = showInputDialog("Nhập số lượng nhập:");
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    double unitPrice = (double) selectedProduct.get("import_price");
                    importPanel.addToCart(selectedProduct, quantity, unitPrice);
                } catch (NumberFormatException e) {
                    showMessage("Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void removeFromCart() {
        int selectedRow = importPanel.getCartTable().getSelectedRow();
        if (selectedRow >= 0 && selectedRow < importPanel.getCartDetails().size()) {
            Map<String, Object> item = importPanel.getCartDetails().get(selectedRow);
            int confirm = showConfirmDialog("Xóa " + item.get("product_name") + " khỏi giỏ?");
            if (confirm == JOptionPane.YES_OPTION) {
                importPanel.removeFromCart((int) item.get("product_id"));
            }
        } else {
            showMessage("Vui lòng chọn sản phẩm cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createImportReceipt() {
        if (importPanel.getCartDetails().isEmpty()) {
            showMessage("Giỏ hàng trống! Vui lòng thêm sản phẩm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int supplierId = importPanel.getSelectedSupplierId();
        if (supplierId == -1) {
            showMessage("Vui lòng chọn nhà cung cấp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Object> receipt = new HashMap<>();
        receipt.put("receipt_code", importPanel.getReceiptCodeField().getText());
        receipt.put("import_date", new java.sql.Date(System.currentTimeMillis()));
        receipt.put("supplier_id", supplierId);
        receipt.put("total_amount", importPanel.getTotalAmount());
        receipt.put("user_id", Integer.parseInt(userInfo.get("id")));
        receipt.put("note", "Nhập kho");

        boolean success = importModel.createImportReceipt(receipt, importPanel.getCartDetails());

        if (success) {
            sendEmailNotification(receipt);

            showMessage("Nhập hàng thành công!\nĐã gửi email thông báo đến " + userInfo.get("email"),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            importPanel.clearCart();
            importPanel.setReceiptCode(importModel.generateReceiptCode());
            importPanel.refreshProductTable(importModel.getAllProducts());
        } else {
            showMessage("Nhập hàng thất bại!\nVui lòng kiểm tra lại kết nối database.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    protected void sendEmailNotification(Map<String, Object> receipt) {
        try {
            EmailSender.sendImportNotification(
                    userInfo.get("email"),
                    (String) receipt.get("receipt_code"),
                    importPanel.getTotalAmount(),
                    userInfo.get("fullname")
            );
        } catch (Exception ex) {
            // Bỏ qua lỗi email trong test
            System.err.println("Gửi email thất bại: " + ex.getMessage());
        }
    }
}