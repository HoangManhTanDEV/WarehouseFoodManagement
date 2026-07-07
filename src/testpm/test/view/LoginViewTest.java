package com.warehouse.view;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static org.junit.jupiter.api.Assertions.*;

public class LoginViewTest {

    private LoginView loginView;

    @BeforeEach
    void setUp() {
        loginView = new LoginView();
        loginView.setVisible(false);
    }

    @AfterEach
    void tearDown() {
        loginView.dispose();
    }

    @Test
    @DisplayName("TC_V_01: Kiểm tra các thành phần LoginView tồn tại")
    void testComponentsExist() {
        assertNotNull(loginView.getUsernameField(), "Username field không được null");
        assertNotNull(loginView.getPasswordField(), "Password field không được null");
        assertNotNull(loginView.getLoginButton(), "Login button không được null");
        assertNotNull(loginView.getExitButton(), "Exit button không được null");
    }

    @Test
    @DisplayName("TC_V_02: Kiểm tra getUsername() hoạt động")
    void testGetUsername() {
        JTextField usernameField = loginView.getUsernameField();
        usernameField.setText("admin");
        assertEquals("admin", loginView.getUsername(), "getUsername() phải trả về 'admin'");
    }

    @Test
    @DisplayName("TC_V_03: Kiểm tra getPassword() hoạt động")
    void testGetPassword() {
        JPasswordField passwordField = loginView.getPasswordField();
        passwordField.setText("123");
        assertEquals("123", loginView.getPassword(), "getPassword() phải trả về '123'");
    }

    @Test
    @DisplayName("TC_V_04: Kiểm tra clearFields() xóa dữ liệu")
    void testClearFields() {
        JTextField usernameField = loginView.getUsernameField();
        JPasswordField passwordField = loginView.getPasswordField();

        usernameField.setText("admin");
        passwordField.setText("123");

        loginView.clearFields();

        assertEquals("", usernameField.getText(), "Sau khi clear, username field phải rỗng");
        assertEquals("", new String(passwordField.getPassword()), "Sau khi clear, password field phải rỗng");
    }

    @Test
    @DisplayName("TC_V_05: Kiểm tra có thể thêm ActionListener vào Login button")
    void testLoginButtonCanAddListener() {
        JButton loginButton = loginView.getLoginButton();
        ActionListener listener = e -> {};
        loginButton.addActionListener(listener);

        // Kiểm tra listener đã được thêm
        assertTrue(loginButton.getActionListeners().length > 0, "Login button phải có ActionListener sau khi thêm");
    }

    @Test
    @DisplayName("TC_V_06: Kiểm tra có thể thêm ActionListener vào Exit button")
    void testExitButtonCanAddListener() {
        JButton exitButton = loginView.getExitButton();
        ActionListener listener = e -> {};
        exitButton.addActionListener(listener);

        // Kiểm tra listener đã được thêm
        assertTrue(exitButton.getActionListeners().length > 0, "Exit button phải có ActionListener sau khi thêm");
    }

    @Test
    @DisplayName("TC_V_07: Kiểm tra addLoginListener() hoạt động")
    void testAddLoginListener() {
        final boolean[] called = {false};

        loginView.addLoginListener(e -> {
            called[0] = true;
        });

        // Mô phỏng click button
        loginView.getLoginButton().doClick();

        assertTrue(called[0], "Listener phải được gọi khi click Login button");
    }

    @Test
    @DisplayName("TC_V_08: Kiểm tra addExitListener() hoạt động")
    void testAddExitListener() {
        final boolean[] called = {false};

        loginView.addExitListener(e -> {
            called[0] = true;
        });

        // Mô phỏng click button
        loginView.getExitButton().doClick();

        assertTrue(called[0], "Listener phải được gọi khi click Exit button");
    }

    @Test
    @DisplayName("TC_V_09: Kiểm tra showMessage() hiển thị thông báo")
    void testShowMessage() {
        // showMessage không trả về giá trị, chỉ cần không bị lỗi
        assertDoesNotThrow(() -> {
            loginView.showMessage("Test message", "Test title", JOptionPane.INFORMATION_MESSAGE);
        }, "showMessage() không được ném ngoại lệ");
    }
}