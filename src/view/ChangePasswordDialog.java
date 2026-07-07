package com.warehouse.view;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField newPasswordField, confirmPasswordField;
    private JButton saveButton, cancelButton;
    private String newPassword;

    public ChangePasswordDialog(JFrame parent, String title) {
        super(parent, title, true);
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

        // Mật khẩu mới
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField(15);
        formPanel.add(newPasswordField, gbc);

        // Xác nhận mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        formPanel.add(confirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("Đổi mật khẩu");
        cancelButton = new JButton("Hủy");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setSize(350, 180);
    }

    public boolean validateInput() {
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (newPass.trim().isEmpty()) {
            showMessage("Vui lòng nhập mật khẩu mới!");
            return false;
        }
        if (!newPass.equals(confirmPass)) {
            showMessage("Mật khẩu xác nhận không khớp!");
            return false;
        }
        if (newPass.length() < 3) {
            showMessage("Mật khẩu phải có ít nhất 3 ký tự!");
            return false;
        }

        newPassword = newPass;
        return true;
    }

    public String getNewPassword() {
        return newPassword;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public JButton getSaveButton() { return saveButton; }
    public JButton getCancelButton() { return cancelButton; }
}