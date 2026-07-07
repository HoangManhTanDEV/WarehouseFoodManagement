package com.warehouse.controller;

import com.warehouse.model.ProductModel;
import com.warehouse.view.ProductPanel;
import com.warehouse.view.ProductDialog;
import javax.swing.*;
import java.util.Map;

public class ProductController {
    private ProductPanel productPanel;
    private ProductModel productModel;

    // Constructor chính - dùng trong ứng dụng
    public ProductController(ProductPanel productPanel, ProductModel productModel) {
        this.productPanel = productPanel;
        this.productModel = productModel;

        loadProductData();

        this.productPanel.getAddButton().addActionListener(e -> showAddDialog());
        this.productPanel.getEditButton().addActionListener(e -> showEditDialog());
        this.productPanel.getDeleteButton().addActionListener(e -> deleteProduct());
        this.productPanel.getRefreshButton().addActionListener(e -> loadProductData());
        this.productPanel.getSearchButton().addActionListener(e -> searchProduct());
    }

    // Constructor dành cho test
    public ProductController(ProductPanel productPanel, ProductModel productModel, boolean forTest) {
        this.productPanel = productPanel;
        this.productModel = productModel;
    }

    // ============ CÁC METHOD CÔNG KHAI ============
    public void loadProductData() {
        productPanel.refreshTable(productModel.getAllProducts());
    }

    public void searchProduct() {
        String keyword = productPanel.getSearchKeyword();
        if (keyword.trim().isEmpty()) {
            loadProductData();
        } else {
            productPanel.refreshTable(productModel.searchProducts(keyword));
        }
    }

    public void deleteProduct() {
        int productId = productPanel.getSelectedProductId();
        if (productId == -1) {
            showMessage("Vui lòng chọn sản phẩm cần xóa!");
            return;
        }

        int confirm = showConfirmDialog("Bạn có chắc muốn xóa sản phẩm này?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (productModel.deleteProduct(productId)) {
                showMessage("Xóa thành công!");
                loadProductData();
            } else {
                showMessage("Xóa thất bại! Sản phẩm có thể đã được sử dụng trong phiếu nhập/xuất.");
            }
        }
    }

    // ============ CÁC METHOD HỖ TRỢ (CÓ THỂ OVERRIDE KHI TEST) ============
    protected void showMessage(String message) {
        JOptionPane.showMessageDialog(productPanel, message);
    }

    protected int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(
                productPanel,
                message,
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
    }

    // ============ CÁC METHOD PRIVATE ============
    private void showAddDialog() {
        ProductDialog dialog = new ProductDialog(null, "Thêm sản phẩm", false);
        dialog.getSaveButton().addActionListener(e -> {
            if (dialog.validateInput()) {
                Map<String, Object> product = dialog.getProductData();
                if (productModel.addProduct(product)) {
                    showMessage("Thêm sản phẩm thành công!");
                    dialog.dispose();
                    loadProductData();
                } else {
                    showMessage("Thêm sản phẩm thất bại! Kiểm tra lại thông tin.");
                }
            }
        });
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int productId = productPanel.getSelectedProductId();
        if (productId == -1) {
            showMessage("Vui lòng chọn sản phẩm cần sửa!");
            return;
        }

        Map<String, Object> selected = productModel.getProductById(productId);
        if (selected == null) {
            showMessage("Không tìm thấy sản phẩm!");
            return;
        }

        ProductDialog dialog = new ProductDialog(null, "Sửa sản phẩm", true);
        dialog.setProductData(selected);
        dialog.getSaveButton().addActionListener(e -> {
            if (dialog.validateInput()) {
                Map<String, Object> product = dialog.getProductData();
                if (productModel.updateProduct(product)) {
                    showMessage("Cập nhật thành công!");
                    dialog.dispose();
                    loadProductData();
                } else {
                    showMessage("Cập nhật thất bại! Kiểm tra lại thông tin.");
                }
            }
        });
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
}