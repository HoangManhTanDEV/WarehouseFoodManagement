package com.warehouse.controller;

import com.warehouse.model.UserModel;
import com.warehouse.view.LoginView;
import com.warehouse.view.MainView;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class LoginController {
    private LoginView loginView;
    private UserModel userModel;

    // Constructor chính - dùng trong ứng dụng
    public LoginController(LoginView loginView, UserModel userModel) {
        this.loginView = loginView;
        this.userModel = userModel;

        // Đăng ký sự kiện
        this.loginView.addLoginListener(new LoginListener());
        this.loginView.addExitListener(new ExitListener());
    }

    // ============ CONSTRUCTOR DÀNH CHO TEST ============
    public LoginController(LoginView loginView, UserModel userModel, boolean forTest) {
        this.loginView = loginView;
        this.userModel = userModel;
        // KHÔNG đăng ký sự kiện để test
    }

    // ============ METHOD CÔNG KHAI ĐỂ TEST ============
    public void doLogin(String username, String password) {
        // Kiểm tra không để trống
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            showMessage("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra đăng nhập
        Map<String, String> userInfo = userModel.login(username, password);

        if (userInfo != null) {
            showMessage("Chào mừng " + userInfo.get("fullname") + "!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            openMainView(userInfo);
            closeLoginView();
        } else {
            showMessage("Sai tên đăng nhập hoặc mật khẩu!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            clearFields();
        }
    }

    // ============ CÁC METHOD HỖ TRỢ (CÓ THỂ OVERRIDE KHI TEST) ============
    protected void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(loginView, message, title, type);
    }

    protected void openMainView(Map<String, String> userInfo) {
        MainView mainView = new MainView(userInfo);
        mainView.setVisible(true);
    }

    protected void closeLoginView() {
        loginView.dispose();
    }

    protected void clearFields() {
        loginView.clearFields();
    }

    // ============ INNER CLASS ============
    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();
            doLogin(username, password);
        }
    }

    class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int confirm = JOptionPane.showConfirmDialog(loginView,
                    "Bạn có chắc muốn thoát?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
}