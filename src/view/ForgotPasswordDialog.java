package com.warehouse.view;

import com.warehouse.model.UserModel;
import com.warehouse.utils.EmailSender;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ForgotPasswordDialog extends JDialog {
    private JTextField usernameField;
    private JButton sendButton, cancelButton;
    private UserModel userModel;

    public ForgotPasswordDialog(JFrame parent) {
        super(parent, "QUÊN MẬT KHẨU", true);
        this.userModel = new UserModel();
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel titleLabel = new JLabel("KHÔI PHỤC MẬT KHẨU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Hướng dẫn
        JLabel guideLabel = new JLabel("Nhập tên đăng nhập, mật khẩu mới sẽ được gửi qua email:");
        guideLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        guideLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(guideLabel, gbc);

        // Tên đăng nhập
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Tên đăng nhập:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));

        sendButton = new JButton("GỬI MẬT KHẨU MỚI");
        sendButton.setBackground(new Color(0, 102, 204));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        sendButton.setFocusPainted(false);

        cancelButton = new JButton("HỦY");
        cancelButton.setBackground(new Color(204, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setFocusPainted(false);

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(450, 280);
        setResizable(false);

        // Sự kiện
        sendButton.addActionListener(e -> sendNewPassword());
        cancelButton.addActionListener(e -> dispose());
    }

    private void sendNewPassword() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra username có tồn tại không
        Map<String, Object> user = userModel.getUserByUsername(username);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo mật khẩu mới ngẫu nhiên
        String newPassword = generateRandomPassword();

        // Cập nhật mật khẩu mới vào database
        boolean updated = userModel.changePassword((int) user.get("id"), newPassword);

        if (updated) {
            // Gửi email chứa mật khẩu mới
            String subject = "🔑 KHÔI PHỤC MẬT KHẨU - Hệ thống QL Kho";
            String content = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #0066cc;">KHÔI PHỤC MẬT KHẨU THÀNH CÔNG</h2>
                    <hr/>
                    <p>Xin chào <b>%s</b>,</p>
                    <p>Chúng tôi đã nhận được yêu cầu khôi phục mật khẩu của bạn.</p>
                    <p>Mật khẩu mới của bạn là:</p>
                    <h2 style="color: red; background-color: #f0f0f0; padding: 10px; text-align: center;">%s</h2>
                    <p>Vui lòng <b>đăng nhập lại</b> và đổi mật khẩu ngay sau khi đăng nhập để bảo mật!</p>
                    <hr/>
                    <p style="color: gray; font-size: 12px;">Nếu bạn không yêu cầu khôi phục mật khẩu, vui lòng bỏ qua email này.</p>
                    <p style="color: gray; font-size: 12px;">Hệ thống quản lý kho thực phẩm</p>
                </body>
                </html>
                """, user.get("fullname"), newPassword);

            EmailSender.sendEmail((String) user.get("email"), subject, content);

            JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới đã được gửi đến email: " + user.get("email") + "\nVui lòng kiểm tra hộp thư (cả Spam)!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Khôi phục mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}