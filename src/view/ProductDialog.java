package com.warehouse.view;

import com.warehouse.model.SupplierModel;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ProductDialog extends JDialog {
    private JTextField codeField, nameField, categoryField, unitField, quantityField, importPriceField, sellPriceField;
    private JComboBox<String> supplierCombo;
    private JButton saveButton, cancelButton;
    private boolean isEdit = false;
    private int productId = -1;
    private Map<Integer, Integer> supplierIdMap; // Lưu mapping giữa index và supplier_id

    public ProductDialog(JFrame parent, String title, boolean isEdit) {
        super(parent, title, true);
        this.isEdit = isEdit;
        initComponents();
        loadSuppliers();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormField(formPanel, gbc, row++, "Mã sản phẩm:", codeField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Tên sản phẩm:", nameField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Loại sản phẩm:", categoryField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Đơn vị tính:", unitField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Số lượng tồn:", quantityField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Giá nhập:", importPriceField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Giá bán:", sellPriceField = new JTextField(15));

        // Nhà cung cấp - ComboBox
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 1;
        supplierCombo = new JComboBox<>();
        supplierCombo.setPreferredSize(new Dimension(200, 25));
        formPanel.add(supplierCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveButton = new JButton(isEdit ? "Cập nhật" : "Thêm mới");
        cancelButton = new JButton("Hủy");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setSize(450, 500);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadSuppliers() {
        try {
            SupplierModel supplierModel = new SupplierModel();
            List<Map<String, Object>> suppliers = supplierModel.getAllSuppliers();
            supplierIdMap = new java.util.HashMap<>();

            supplierCombo.removeAllItems();
            for (Map<String, Object> supplier : suppliers) {
                String display = supplier.get("code") + " - " + supplier.get("name");
                supplierCombo.addItem(display);
                supplierIdMap.put(supplierCombo.getItemCount() - 1, (int) supplier.get("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProductData(Map<String, Object> product) {
        codeField.setText((String) product.get("code"));
        nameField.setText((String) product.get("name"));
        categoryField.setText((String) product.get("category"));
        unitField.setText((String) product.get("unit"));
        quantityField.setText(String.valueOf(product.get("quantity")));
        importPriceField.setText(String.valueOf(product.get("import_price")));
        sellPriceField.setText(String.valueOf(product.get("sell_price")));
        productId = (int) product.get("id");

        // Chọn đúng nhà cung cấp trong combobox
        int supplierId = (int) product.get("supplier_id");
        for (int i = 0; i < supplierCombo.getItemCount(); i++) {
            if (supplierIdMap.get(i) == supplierId) {
                supplierCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    public Map<String, Object> getProductData() {
        Map<String, Object> product = new java.util.HashMap<>();
        product.put("id", productId);
        product.put("code", codeField.getText().trim());
        product.put("name", nameField.getText().trim());
        product.put("category", categoryField.getText().trim());
        product.put("unit", unitField.getText().trim());

        // Lấy supplier_id từ combobox
        int selectedIndex = supplierCombo.getSelectedIndex();
        int supplierId = supplierIdMap.getOrDefault(selectedIndex, 16); // Mặc định ID 16
        product.put("supplier_id", supplierId);

        try {
            product.put("quantity", Integer.parseInt(quantityField.getText().trim()));
            product.put("import_price", Double.parseDouble(importPriceField.getText().trim()));
            product.put("sell_price", Double.parseDouble(sellPriceField.getText().trim()));
        } catch (NumberFormatException e) {
            product.put("quantity", 0);
            product.put("import_price", 0.0);
            product.put("sell_price", 0.0);
        }

        product.put("expiry_date", null);
        return product;
    }

    public boolean validateInput() {
        if (codeField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập mã sản phẩm!");
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập tên sản phẩm!");
            return false;
        }
        if (supplierCombo.getSelectedIndex() == -1) {
            showMessage("Vui lòng chọn nhà cung cấp!");
            return false;
        }
        try {
            Integer.parseInt(quantityField.getText().trim());
            Double.parseDouble(importPriceField.getText().trim());
            Double.parseDouble(sellPriceField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Số lượng và giá phải là số hợp lệ!");
            return false;
        }
        return true;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public JButton getSaveButton() { return saveButton; }
    public JButton getCancelButton() { return cancelButton; }
}