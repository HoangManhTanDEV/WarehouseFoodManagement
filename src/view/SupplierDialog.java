package com.warehouse.view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SupplierDialog extends JDialog {
    private JTextField codeField, nameField, phoneField, emailField, addressField;
    private JButton saveButton, cancelButton;
    private boolean isEdit = false;
    private int supplierId = -1;

    public SupplierDialog(JFrame parent, String title, boolean isEdit) {
        super(parent, title, true);
        this.isEdit = isEdit;
        initComponents();
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
        addFormField(formPanel, gbc, row++, "Mã NCC:", codeField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Tên NCC:", nameField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Số điện thoại:", phoneField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Email:", emailField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Địa chỉ:", addressField = new JTextField(15));

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
        setSize(450, 380);

        // Nếu là sửa, khóa mã không cho sửa
        if (isEdit) {
            codeField.setEditable(false);
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    public void setGeneratedCode(String code) {
        codeField.setText(code);
    }

    public void setSupplierData(Map<String, Object> supplier) {
        codeField.setText((String) supplier.get("code"));
        nameField.setText((String) supplier.get("name"));
        phoneField.setText((String) supplier.get("phone"));
        emailField.setText((String) supplier.get("email"));
        addressField.setText((String) supplier.get("address"));
        supplierId = (int) supplier.get("id");
    }

    public Map<String, Object> getSupplierData() {
        Map<String, Object> supplier = new java.util.HashMap<>();
        supplier.put("id", supplierId);
        supplier.put("code", codeField.getText().trim());
        supplier.put("name", nameField.getText().trim());
        supplier.put("phone", phoneField.getText().trim());
        supplier.put("email", emailField.getText().trim());
        supplier.put("address", addressField.getText().trim());
        return supplier;
    }

    public boolean validateInput() {
        if (codeField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập mã nhà cung cấp!");
            codeField.requestFocus();
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập tên nhà cung cấp!");
            nameField.requestFocus();
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập số điện thoại!");
            phoneField.requestFocus();
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập email!");
            emailField.requestFocus();
            return false;
        }
        return true;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }
}