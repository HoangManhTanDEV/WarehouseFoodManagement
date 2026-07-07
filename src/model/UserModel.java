package com.warehouse.model;

import com.warehouse.utils.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class UserModel {

    // Đăng nhập
    public Map<String, String> login(String username, String password) {
        Map<String, String> userInfo = null;
        String sql = "SELECT * FROM users WHERE username = ? AND password = MD5(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userInfo = new HashMap<>();
                userInfo.put("id", String.valueOf(rs.getInt("id")));
                userInfo.put("username", rs.getString("username"));
                userInfo.put("fullname", rs.getString("fullname"));
                userInfo.put("email", rs.getString("email"));
                userInfo.put("role", rs.getString("role"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInfo;
    }

    // Lấy thông tin user theo username
    public Map<String, Object> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("username", rs.getString("username"));
                user.put("fullname", rs.getString("fullname"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả người dùng
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("username", rs.getString("username"));
                user.put("fullname", rs.getString("fullname"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                user.put("created_at", rs.getTimestamp("created_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Thêm người dùng mới
    public boolean addUser(Map<String, Object> user) {
        String sql = "INSERT INTO users(username, password, fullname, email, role) VALUES(?, MD5(?), ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.get("username"));
            pstmt.setString(2, (String) user.get("password"));
            pstmt.setString(3, (String) user.get("fullname"));
            pstmt.setString(4, (String) user.get("email"));
            pstmt.setString(5, (String) user.get("role"));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật người dùng
    public boolean updateUser(Map<String, Object> user) {
        String sql = "UPDATE users SET fullname=?, email=?, role=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.get("fullname"));
            pstmt.setString(2, (String) user.get("email"));
            pstmt.setString(3, (String) user.get("role"));
            pstmt.setInt(4, (int) user.get("id"));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đổi mật khẩu
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = MD5(?) WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa người dùng (không cho xóa admin)
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=? AND role != 'admin'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm người dùng
    public List<Map<String, Object>> searchUsers(String keyword) {
        List<Map<String, Object>> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR fullname LIKE ? OR email LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("username", rs.getString("username"));
                user.put("fullname", rs.getString("fullname"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                user.put("created_at", rs.getTimestamp("created_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Kiểm tra username đã tồn tại chưa
    public boolean isUsernameExists(String username, int excludeId) {
        String sql = "SELECT * FROM users WHERE username = ? AND id != ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy thông tin user theo ID
    public Map<String, Object> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("username", rs.getString("username"));
                user.put("fullname", rs.getString("fullname"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}