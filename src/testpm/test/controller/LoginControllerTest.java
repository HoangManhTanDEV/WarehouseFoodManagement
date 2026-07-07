package com.warehouse.controller;

import com.warehouse.model.UserModel;
import com.warehouse.view.LoginView;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JOptionPane;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @Mock
    private UserModel mockModel;

    @Mock
    private LoginView mockView;

    private LoginController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new LoginController(mockView, mockModel, true);
    }

    // ==================== TEST 1: ĐĂNG NHẬP THÀNH CÔNG ====================
    @Test
    @DisplayName("TC_C08: doLogin() đăng nhập thành công với admin")
    void testLoginSuccessAdmin() {
        // 1. Tạo spy controller
        LoginController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        String username = "admin";
        String password = "123";
        Map<String, String> mockUserInfo = new HashMap<>();
        mockUserInfo.put("username", "admin");
        mockUserInfo.put("role", "admin");
        mockUserInfo.put("fullname", "Quản trị viên");

        // 3. Giả lập Model
        when(mockModel.login(username, password)).thenReturn(mockUserInfo);

        // 4. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());
        doNothing().when(spyController).openMainView(anyMap());
        doNothing().when(spyController).closeLoginView();

        // 5. Gọi hàm cần test
        spyController.doLogin(username, password);

        // 6. Kiểm tra
        verify(mockModel, times(1)).login(username, password);
        verify(spyController, times(1)).openMainView(mockUserInfo);
        verify(spyController, times(1)).closeLoginView();
        verify(spyController, never()).clearFields();
    }

    @Test
    @DisplayName("TC_C09: doLogin() đăng nhập thành công với staff")
    void testLoginSuccessStaff() {
        // 1. Tạo spy controller
        LoginController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        String username = "staff1";
        String password = "123";
        Map<String, String> mockUserInfo = new HashMap<>();
        mockUserInfo.put("username", "staff1");
        mockUserInfo.put("role", "staff");
        mockUserInfo.put("fullname", "Nhân viên");

        // 3. Giả lập Model
        when(mockModel.login(username, password)).thenReturn(mockUserInfo);

        // 4. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());
        doNothing().when(spyController).openMainView(anyMap());
        doNothing().when(spyController).closeLoginView();

        // 5. Gọi hàm cần test
        spyController.doLogin(username, password);

        // 6. Kiểm tra
        verify(mockModel, times(1)).login(username, password);
        verify(spyController, times(1)).openMainView(mockUserInfo);
        verify(spyController, times(1)).closeLoginView();
    }

    // ==================== TEST 2: ĐĂNG NHẬP THẤT BẠI ====================
    @Test
    @DisplayName("TC_C10: doLogin() đăng nhập sai mật khẩu")
    void testLoginFailWrongPassword() {
        // 1. Tạo spy controller
        LoginController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        String username = "admin";
        String password = "wrong";

        // 3. Giả lập Model trả về null (đăng nhập thất bại)
        when(mockModel.login(username, password)).thenReturn(null);

        // 4. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());
        doNothing().when(spyController).clearFields();

        // 5. Gọi hàm cần test
        spyController.doLogin(username, password);

        // 6. Kiểm tra
        verify(mockModel, times(1)).login(username, password);
        verify(spyController, times(1)).showMessage(
                contains("Sai"),
                contains("Lỗi"),
                eq(JOptionPane.ERROR_MESSAGE)
        );
        verify(spyController, times(1)).clearFields();
        verify(spyController, never()).openMainView(anyMap());
        verify(spyController, never()).closeLoginView();
    }

    @Test
    @DisplayName("TC_C11: doLogin() đăng nhập với tài khoản không tồn tại")
    void testLoginFailUserNotFound() {
        // 1. Tạo spy controller
        LoginController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        String username = "nonexist";
        String password = "123";

        // 3. Giả lập Model trả về null (không tìm thấy user)
        when(mockModel.login(username, password)).thenReturn(null);

        // 4. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());
        doNothing().when(spyController).clearFields();

        // 5. Gọi hàm cần test
        spyController.doLogin(username, password);

        // 6. Kiểm tra
        verify(mockModel, times(1)).login(username, password);
        verify(spyController, times(1)).showMessage(
                contains("Sai"),
                contains("Lỗi"),
                eq(JOptionPane.ERROR_MESSAGE)
        );
        verify(spyController, times(1)).clearFields();
        verify(spyController, never()).openMainView(anyMap());
        verify(spyController, never()).closeLoginView();
    }

    // ==================== TEST 3: KIỂM TRA DỮ LIỆU ĐẦU VÀO ====================
    @Test
    @DisplayName("TC_C12: doLogin() từ chối đăng nhập khi username trống")
    void testLoginEmptyUsername() {
        // 1. Tạo spy controller
        LoginController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        String username = "";
        String password = "123";

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm cần test
        spyController.doLogin(username, password);

        // 5. Kiểm tra: Model KHÔNG được gọi
        verify(mockModel, never()).login(anyString(), anyString());
        verify(spyController, times(1)).showMessage(
                contains("nhập đầy đủ"),
                contains("Lỗi"),
                eq(JOptionPane.ERROR_MESSAGE)
        );
        verify(spyController, never()).openMainView(anyMap());
        verify(spyController, never()).closeLoginView();
    }

    @Test
    @DisplayName("TC_C13: doLogin() từ chối đăng nhập khi password trống")
    void testLoginEmptyPassword() {
        // 1. Tạo spy controller
        LoginController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu
        String username = "admin";
        String password = "";

        // 3. Bỏ qua JOptionPane
        doNothing().when(spyController).showMessage(anyString(), anyString(), anyInt());

        // 4. Gọi hàm cần test
        spyController.doLogin(username, password);

        // 5. Kiểm tra: Model KHÔNG được gọi
        verify(mockModel, never()).login(anyString(), anyString());
        verify(spyController, times(1)).showMessage(
                contains("nhập đầy đủ"),
                contains("Lỗi"),
                eq(JOptionPane.ERROR_MESSAGE)
        );
        verify(spyController, never()).openMainView(anyMap());
        verify(spyController, never()).closeLoginView();
    }
}