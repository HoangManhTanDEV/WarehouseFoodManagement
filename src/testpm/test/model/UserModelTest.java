package com.warehouse.model;

import org.junit.jupiter.api.*;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserModelTest {

    private static UserModel userModel;

    @BeforeAll
    static void setUp() {
        userModel = new UserModel();
        System.out.println("✅ Bắt đầu kiểm thử UserModel");
    }

    @Test
    @Order(1)
    @DisplayName("TC01: Đăng nhập thành công với admin")
    void testLoginAdminSuccess() {
        Map<String, String> user = userModel.login("admin", "123");
        assertNotNull(user, "Đăng nhập admin phải thành công");
        assertEquals("admin", user.get("username"));
        assertEquals("admin", user.get("role"));
        System.out.println("✅ Đăng nhập admin thành công");
    }

    @Test
    @Order(2)
    @DisplayName("TC02: Đăng nhập thành công với staff")
    void testLoginStaffSuccess() {
        Map<String, String> user = userModel.login("staff1", "123");
        assertNotNull(user, "Đăng nhập staff phải thành công");
        assertEquals("staff1", user.get("username"));
        assertEquals("staff", user.get("role"));
        System.out.println("✅ Đăng nhập staff thành công");
    }

    @Test
    @Order(3)
    @DisplayName("TC03: Đăng nhập sai mật khẩu")
    void testLoginWrongPassword() {
        Map<String, String> user = userModel.login("admin", "wrong_password");
        assertNull(user, "Đăng nhập sai mật khẩu phải thất bại");
        System.out.println("✅ Hệ thống từ chối sai mật khẩu");
    }

    @Test
    @Order(4)
    @DisplayName("TC04: Đăng nhập sai username")
    void testLoginWrongUsername() {
        Map<String, String> user = userModel.login("non_exist_user", "123");
        assertNull(user, "Đăng nhập sai username phải thất bại");
        System.out.println("✅ Hệ thống từ chối sai username");
    }

    @Test
    @Order(5)
    @DisplayName("TC05: Lấy danh sách người dùng")
    void testGetAllUsers() {
        var users = userModel.getAllUsers();
        assertNotNull(users, "Danh sách người dùng không được null");
        assertTrue(users.size() >= 2, "Phải có ít nhất 2 người dùng (admin, staff)");
        System.out.println("✅ Có " + users.size() + " người dùng");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("✅ Kết thúc kiểm thử UserModel");
    }
}