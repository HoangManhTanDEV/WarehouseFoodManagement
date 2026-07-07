package com.warehouse.controller;

import com.warehouse.model.SupplierModel;
import com.warehouse.view.SupplierPanel;
import com.warehouse.view.SupplierDialog;
import javax.swing.*;
import java.util.Map;

public class SupplierController {
    private SupplierPanel supplierPanel;
    private SupplierModel supplierModel;

    // Constructor chính - dùng trong ứng dụng
    public SupplierController(SupplierPanel supplierPanel, SupplierModel supplierModel) {
        this.supplierPanel = supplierPanel;
        this.supplierModel = supplierModel;

        loadSupplierData();

        this.supplierPanel.getAddButton().addActionListener(e -> showAddDialog());
        this.supplierPanel.getEditButton().addActionListener(e -> showEditDialog());
        this.supplierPanel.getDeleteButton().addActionListener(e -> deleteSupplier());
        this.supplierPanel.getRefreshButton().addActionListener(e -> loadSupplierData());
        this.supplierPanel.getSearchButton().addActionListener(e -> searchSupplier());
    }

    // ============ CONSTRUCTOR DÀNH CHO TEST ============
    public SupplierController(SupplierPanel supplierPanel, SupplierModel supplierModel, boolean forTest) {
        this.supplierPanel = supplierPanel;
        this.supplierModel = supplierModel;
        // KHÔNG load dữ liệu và đăng ký sự kiện
    }

    // ============ CÁC METHOD CÔNG KHAI ============
    public void loadSupplierData() {
        supplierPanel.refreshTable(supplierModel.getAllSuppliers());
    }

    public void searchSupplier() {
        String keyword = supplierPanel.getSearchKeyword();
        if (keyword.trim().isEmpty()) {
            loadSupplierData();
        } else {
            supplierPanel.refreshTable(supplierModel.searchSuppliers(keyword));
        }
    }

    public void deleteSupplier() {
        Map<String, Object> selected = supplierPanel.getSelectedSupplier();
        if (selected == null) {
            showMessage("Vui lòng chọn nhà cung cấp cần xóa!");
            return;
        }

        int confirm = showConfirmDialog("Xóa nhà cung cấp " + selected.get("name") + "?\nLưu ý: Sẽ không xóa được nếu đã có sản phẩm liên kết!");
        if (confirm == JOptionPane.YES_OPTION) {
            if (supplierModel.deleteSupplier((int) selected.get("id"))) {
                showMessage("Xóa thành công!");
                loadSupplierData();
            } else {
                showMessage("Xóa thất bại! Nhà cung cấp này đã có sản phẩm liên kết, không thể xóa.");
            }
        }
    }

    // ============ CÁC METHOD HỖ TRỢ (CÓ THỂ OVERRIDE KHI TEST) ============
    protected void showMessage(String message) {
        JOptionPane.showMessageDialog(supplierPanel, message);
    }

    protected int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(
                supplierPanel,
                message,
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
    }

    // ============ CÁC METHOD PRIVATE ============
    private void showAddDialog() {
        try {
            SupplierDialog dialog = new SupplierDialog(null, "Thêm nhà cung cấp", false);

            String newCode = supplierModel.generateSupplierCode();
            dialog.setGeneratedCode(newCode);

            dialog.getSaveButton().addActionListener(e -> {
                if (dialog.validateInput()) {
                    Map<String, Object> supplier = dialog.getSupplierData();

                    if (supplierModel.isCodeExists((String) supplier.get("code"))) {
                        showMessage("Mã nhà cung cấp '" + supplier.get("code") + "' đã tồn tại!\nVui lòng sử dụng mã khác.");
                        return;
                    }

                    if (supplierModel.addSupplier(supplier)) {
                        showMessage("Thêm nhà cung cấp thành công!");
                        dialog.dispose();
                        loadSupplierData();
                    } else {
                        showMessage("Thêm nhà cung cấp thất bại!\nVui lòng kiểm tra lại thông tin.");
                    }
                }
            });
            dialog.getCancelButton().addActionListener(e -> dialog.dispose());
            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi: " + ex.getMessage());
        }
    }

    private void showEditDialog() {
        Map<String, Object> selected = supplierPanel.getSelectedSupplier();
        if (selected == null) {
            showMessage("Vui lòng chọn nhà cung cấp cần sửa!");
            return;
        }

        SupplierDialog dialog = new SupplierDialog(null, "Sửa nhà cung cấp", true);
        dialog.setSupplierData(selected);
        dialog.getSaveButton().addActionListener(e -> {
            if (dialog.validateInput()) {
                Map<String, Object> supplier = dialog.getSupplierData();
                if (supplierModel.updateSupplier(supplier)) {
                    showMessage("Cập nhật thành công!");
                    dialog.dispose();
                    loadSupplierData();
                } else {
                    showMessage("Cập nhật thất bại!");
                }
            }
        });
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
}