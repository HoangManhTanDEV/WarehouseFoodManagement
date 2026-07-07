package com.warehouse.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class UserDialog extends JDialog {
    private JTextField usernameField, fullnameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton saveButton, cancelButton;
    private boolean isEdit = false;
    private int userId = -1;

    public UserDialog(JFrame parent, String title, boolean isEdit) {
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
        addFormField(formPanel, gbc, row++, "Tên đăng nhập:", usernameField = new JTextField(15));
        if (!isEdit) {
            addFormField(formPanel, gbc, row++, "Mật khẩu:", passwordField = new JPasswordField(15));
        }
        addFormField(formPanel, gbc, row++, "Họ tên:", fullnameField = new JTextField(15));
        addFormField(formPanel, gbc, row++, "Email:", emailField = new JTextField(15));

        // Vai trò
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"staff", "admin"});
        formPanel.add(roleCombo, gbc);

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
        setSize(400, isEdit ? 350 : 400);

        // Nếu là sửa, disable username field
        if (isEdit) {
            usernameField.setEditable(false);
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    public void setUserData(Map<String, Object> user) {
        usernameField.setText((String) user.get("username"));
        fullnameField.setText((String) user.get("fullname"));
        emailField.setText((String) user.get("email"));
        String role = (String) user.get("role");
        roleCombo.setSelectedItem(role);
        userId = (int) user.get("id");
    }

    public Map<String, Object> getUserData() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("username", usernameField.getText());
        user.put("fullname", fullnameField.getText());
        user.put("email", emailField.getText());
        user.put("role", (String) roleCombo.getSelectedItem());
        if (passwordField != null) {
            user.put("password", new String(passwordField.getPassword()));
        }
        return user;
    }

    public boolean validateInput() {
        if (usernameField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập tên đăng nhập!");
            return false;
        }
        if (!isEdit && (passwordField == null || new String(passwordField.getPassword()).trim().isEmpty())) {
            showMessage("Vui lòng nhập mật khẩu!");
            return false;
        }
        if (fullnameField.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập họ tên!");
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