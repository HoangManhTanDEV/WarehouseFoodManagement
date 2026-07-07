package com.warehouse;

import com.warehouse.controller.LoginController;
import com.warehouse.model.UserModel;
import com.warehouse.view.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Chương trình khởi động...");

        SwingUtilities.invokeLater(() -> {
            System.out.println("📱 Đang tạo LoginView...");
            LoginView loginView = new LoginView();
            System.out.println("✅ LoginView đã được tạo");

            System.out.println("📦 Đang tạo UserModel...");
            UserModel userModel = new UserModel();
            System.out.println("✅ UserModel đã được tạo");

            System.out.println("🎮 Đang tạo LoginController...");
            new LoginController(loginView, userModel);
            System.out.println("✅ LoginController đã được tạo");

            // Đảm bảo hiển thị
            loginView.setVisible(true);
            System.out.println("👁️ LoginView đã hiển thị");
        });
    }
}