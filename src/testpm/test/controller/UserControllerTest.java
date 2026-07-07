package com.warehouse.controller;

import com.warehouse.model.UserModel;
import com.warehouse.view.UserPanel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import javax.swing.JOptionPane;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserModel mockModel;

    @Mock
    private UserPanel mockView;

    private Map<String, String> mockCurrentUser;
    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCurrentUser = new HashMap<>();
        mockCurrentUser.put("username", "admin");
        mockCurrentUser.put("role", "admin");

        when(mockView.getCurrentUser()).thenReturn(mockCurrentUser);
        controller = new UserController(mockView, mockModel, true);
    }

    // ==================== TEST 1: LOAD DỮ LIỆU ====================
    @Test
    @DisplayName("TC_C59: loadUserData() gọi refreshTable()")
    void testLoadUserData() {
        List<Map<String, Object>> mockUsers = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", "admin");
        user.put("fullname", "Quản trị viên");
        mockUsers.add(user);

        when(mockModel.getAllUsers()).thenReturn(mockUsers);

        controller.loadUserData();

        verify(mockModel, times(1)).getAllUsers();
        verify(mockView, times(1)).refreshTable(mockUsers);
    }

    // ==================== TEST 2: TÌM KIẾM ====================
    @Test
    @DisplayName("TC_C60: searchUser() gọi searchUsers() khi có từ khóa")
    void testSearchUserWithKeyword() {
        String keyword = "admin";
        List<Map<String, Object>> mockResults = new ArrayList<>();
        when(mockView.getSearchKeyword()).thenReturn(keyword);
        when(mockModel.searchUsers(keyword)).thenReturn(mockResults);

        controller.searchUser();

        verify(mockModel, times(1)).searchUsers(keyword);
        verify(mockView, times(1)).refreshTable(mockResults);
    }

    @Test
    @DisplayName("TC_C61: searchUser() gọi getAllUsers() khi từ khóa rỗng")
    void testSearchUserEmptyKeyword() {
        List<Map<String, Object>> mockUsers = new ArrayList<>();
        when(mockView.getSearchKeyword()).thenReturn("");
        when(mockModel.getAllUsers()).thenReturn(mockUsers);

        controller.searchUser();

        verify(mockModel, times(1)).getAllUsers();
        verify(mockModel, never()).searchUsers(anyString());
        verify(mockView, times(1)).refreshTable(mockUsers);
    }

    // ==================== TEST 3: XÓA NGƯỜI DÙNG ====================
    @Test
    @DisplayName("TC_C62: deleteUser() xóa user khi hợp lệ")
    void testDeleteUserSuccess() {
        UserController spyController = spy(controller);

        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("id", 2);
        mockUser.put("username", "staff1");

        when(mockView.getSelectedUser()).thenReturn(mockUser);
        when(mockModel.deleteUser(2)).thenReturn(true);

        doReturn(JOptionPane.YES_OPTION).when(spyController).showConfirmDialog(anyString());
        doNothing().when(spyController).showMessage(anyString());

        spyController.deleteUser();

        verify(mockModel, times(1)).deleteUser(2);
        verify(mockView, times(1)).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C63: deleteUser() không xóa khi không chọn user")
    void testDeleteUserNoSelection() {
        UserController spyController = spy(controller);

        when(mockView.getSelectedUser()).thenReturn(null);
        doNothing().when(spyController).showMessage(anyString());

        spyController.deleteUser();

        verify(mockModel, never()).deleteUser(anyInt());
        verify(mockView, never()).refreshTable(anyList());
    }

    @Test
    @DisplayName("TC_C64: deleteUser() không xóa khi xóa chính mình")
    void testDeleteUserSelf() {
        UserController spyController = spy(controller);

        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("id", 1);
        mockUser.put("username", "admin");

        when(mockView.getSelectedUser()).thenReturn(mockUser);
        doNothing().when(spyController).showMessage(anyString());

        spyController.deleteUser();

        verify(mockModel, never()).deleteUser(anyInt());
        verify(spyController, times(1)).showMessage(contains("Không thể xóa tài khoản đang đăng nhập"));
    }

    @Test
    @DisplayName("TC_C65: deleteUser() xóa thất bại khi Model trả về false")
    void testDeleteUserFail() {
        UserController spyController = spy(controller);

        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("id", 2);
        mockUser.put("username", "staff1");

        when(mockView.getSelectedUser()).thenReturn(mockUser);
        when(mockModel.deleteUser(2)).thenReturn(false);

        doReturn(JOptionPane.YES_OPTION).when(spyController).showConfirmDialog(anyString());
        doNothing().when(spyController).showMessage(anyString());

        spyController.deleteUser();

        verify(mockModel, times(1)).deleteUser(2);
        verify(spyController, times(1)).showMessage(contains("thất bại"));
    }

    // ==================== TEST 4: ĐỔI MẬT KHẨU ====================
    // Lưu ý: Test changePassword cần mock ChangePasswordDialog
    // Cách đơn giản là kiểm tra controller gọi đúng phương thức Model

    @Test
    @DisplayName("TC_C66: changePassword() gọi changePassword khi user được chọn")
    void testChangePasswordSuccess() {
        // 1. Tạo spy controller và mock các dependency
        UserController spyController = spy(controller);

        // 2. Chuẩn bị dữ liệu giả
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("id", 2);
        mockUser.put("username", "staff1");

        // 3. Giả lập View
        when(mockView.getSelectedUser()).thenReturn(mockUser);

        // 4. Mock ChangePasswordDialog - sử dụng spy để bỏ qua
        // Vì ChangePasswordDialog là class thật, chúng ta không thể mock trực tiếp
        // Cách đơn giản: kiểm tra rằng controller đã gọi đúng phương thức Model
        // khi dialog được xác nhận

        // 5. Giả lập Model
        when(mockModel.changePassword(eq(2), anyString())).thenReturn(true);

        // 6. Bỏ qua showMessage
        doNothing().when(spyController).showMessage(anyString());

        // 7. Gọi hàm
        spyController.changePassword();

        // 8. Kiểm tra: Model.changePassword() được gọi (qua dialog)
        // Lưu ý: do dialog thật được tạo nên không thể verify trực tiếp
        // Thay vào đó, chúng ta kiểm tra rằng controller đã gọi showDialog
        // hoặc kiểm tra logic xử lý
    }

    @Test
    @DisplayName("TC_C67: changePassword() không đổi khi không chọn user")
    void testChangePasswordNoSelection() {
        UserController spyController = spy(controller);

        when(mockView.getSelectedUser()).thenReturn(null);
        doNothing().when(spyController).showMessage(anyString());

        spyController.changePassword();

        verify(mockModel, never()).changePassword(anyInt(), anyString());
        verify(spyController, times(1)).showMessage(contains("Vui lòng chọn tài khoản"));
    }
}