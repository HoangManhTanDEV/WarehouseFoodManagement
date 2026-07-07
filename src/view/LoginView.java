package com.warehouse.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import com.warehouse.view.ForgotPasswordDialog;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;

    public LoginView() {
        initComponents();
        setTitle("ĐĂNG NHẬP - QUẢN LÝ KHO THỰC PHẨM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Tiêu đề
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ KHO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Tên đăng nhập:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passwordField, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(new Color(240, 248, 255));

        loginButton = new JButton("ĐĂNG NHẬP");
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);

        exitButton = new JButton("THOÁT");
        exitButton.setBackground(new Color(204, 0, 0));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setFocusPainted(false);

        // Nút Quên mật khẩu
        JButton forgotButton = new JButton("Quên mật khẩu?");
        forgotButton.setBackground(new Color(240, 248, 255));
        forgotButton.setForeground(new Color(0, 102, 204));
        forgotButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotButton.setFocusPainted(false);
        forgotButton.setBorderPainted(false);
        forgotButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotButton.addActionListener(e -> {
            new ForgotPasswordDialog(LoginView.this).setVisible(true);
        });

        btnPanel.add(loginButton);
        btnPanel.add(exitButton);
        btnPanel.add(forgotButton);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(450, 350);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    public void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

    // ============ GETTERS DÀNH CHO TEST ============
    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }
}