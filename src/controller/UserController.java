package com.warehouse.controller;

import com.warehouse.model.UserModel;
import com.warehouse.view.UserPanel;
import com.warehouse.view.UserDialog;
import com.warehouse.view.ChangePasswordDialog;
import javax.swing.*;
import java.util.Map;

public class UserController {
    private UserPanel userPanel;
    private UserModel userModel;

    // Constructor chính - dùng trong ứng dụng
    public UserController(UserPanel userPanel, UserModel userModel) {
        this.userPanel = userPanel;
        this.userModel = userModel;

        loadUserData();

        this.userPanel.getAddButton().addActionListener(e -> showAddDialog());
        this.userPanel.getEditButton().addActionListener(e -> showEditDialog());
        this.userPanel.getDeleteButton().addActionListener(e -> deleteUser());
        this.userPanel.getRefreshButton().addActionListener(e -> loadUserData());
        this.userPanel.getSearchButton().addActionListener(e -> searchUser());
        this.userPanel.getChangePasswordButton().addActionListener(e -> changePassword());
    }

    // ============ CONSTRUCTOR DÀNH CHO TEST ============
    public UserController(UserPanel userPanel, UserModel userModel, boolean forTest) {
        this.userPanel = userPanel;
        this.userModel = userModel;
        // KHÔNG load dữ liệu và đăng ký sự kiện
    }

    // ============ CÁC METHOD CÔNG KHAI ============
    public void loadUserData() {
        userPanel.refreshTable(userModel.getAllUsers());
    }

    public void searchUser() {
        String keyword = userPanel.getSearchKeyword();
        if (keyword.trim().isEmpty()) {
            loadUserData();
        } else {
            userPanel.refreshTable(userModel.searchUsers(keyword));
        }
    }

    public void deleteUser() {
        Map<String, Object> selected = userPanel.getSelectedUser();
        if (selected == null) {
            showMessage("Vui lòng chọn tài khoản cần xóa!");
            return;
        }

        // Không cho xóa chính mình
        if (selected.get("username").equals(userPanel.getCurrentUser().get("username"))) {
            showMessage("Không thể xóa tài khoản đang đăng nhập!");
            return;
        }

        int confirm = showConfirmDialog("Xóa tài khoản " + selected.get("username") + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (userModel.deleteUser((int) selected.get("id"))) {
                showMessage("Xóa thành công!");
                loadUserData();
            } else {
                showMessage("Xóa thất bại! Không thể xóa tài khoản Admin.");
            }
        }
    }

    public void changePassword() {
        Map<String, Object> selected = userPanel.getSelectedUser();
        if (selected == null) {
            showMessage("Vui lòng chọn tài khoản cần đổi mật khẩu!");
            return;
        }

        ChangePasswordDialog dialog = new ChangePasswordDialog(null, "Đổi mật khẩu cho " + selected.get("username"));
        dialog.getSaveButton().addActionListener(e -> {
            if (dialog.validateInput()) {
                if (userModel.changePassword((int) selected.get("id"), dialog.getNewPassword())) {
                    showMessage("Đổi mật khẩu thành công!");
                    dialog.dispose();
                } else {
                    showMessage("Đổi mật khẩu thất bại!");
                }
            }
        });
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ============ CÁC METHOD HỖ TRỢ (CÓ THỂ OVERRIDE KHI TEST) ============
    protected void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    protected int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(null, message, "Xác nhận", JOptionPane.YES_NO_OPTION);
    }

    // ============ CÁC METHOD PRIVATE ============
    private void showAddDialog() {
        UserDialog dialog = new UserDialog(null, "Thêm tài khoản", false);
        dialog.getSaveButton().addActionListener(e -> {
            if (dialog.validateInput()) {
                Map<String, Object> user = dialog.getUserData();

                if (userModel.isUsernameExists((String) user.get("username"), -1)) {
                    showMessage("Tên đăng nhập đã tồn tại!");
                    return;
                }

                if (userModel.addUser(user)) {
                    showMessage("Thêm tài khoản thành công!");
                    dialog.dispose();
                    loadUserData();
                } else {
                    showMessage("Thêm thất bại!");
                }
            }
        });
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        Map<String, Object> selected = userPanel.getSelectedUser();
        if (selected == null) {
            showMessage("Vui lòng chọn tài khoản cần sửa!");
            return;
        }

        UserDialog dialog = new UserDialog(null, "Sửa tài khoản", true);
        dialog.setUserData(selected);
        dialog.getSaveButton().addActionListener(e -> {
            if (dialog.validateInput()) {
                Map<String, Object> user = dialog.getUserData();

                if (userModel.updateUser(user)) {
                    showMessage("Cập nhật thành công!");
                    dialog.dispose();
                    loadUserData();
                } else {
                    showMessage("Cập nhật thất bại!");
                }
            }
        });
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
}