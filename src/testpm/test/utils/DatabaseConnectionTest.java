package com.warehouse.utils;

import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    @DisplayName("TC_U_01: Kiểm tra kết nối database thành công")
    void testGetConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull(conn, "Connection không được null");
            assertFalse(conn.isClosed(), "Connection phải đang mở");
            System.out.println("✅ Kết nối database thành công!");
        } catch (SQLException e) {
            fail("Kết nối database thất bại: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("TC_U_02: Kiểm tra truy vấn đơn giản")
    void testSimpleQuery() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {

            assertTrue(rs.next(), "ResultSet phải có dữ liệu");
            assertEquals(1, rs.getInt(1), "Kết quả phải là 1");
            System.out.println("✅ Truy vấn đơn giản thành công!");
        } catch (SQLException e) {
            fail("Truy vấn thất bại: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("TC_U_03: Kiểm tra closeConnection()")
    void testCloseConnection() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            assertNotNull(conn);
            DatabaseConnection.closeConnection(conn);
            // Kiểm tra connection đã đóng
            assertTrue(conn.isClosed(), "Connection phải được đóng");
            System.out.println("✅ Đóng connection thành công!");
        } catch (SQLException e) {
            fail("Lỗi khi đóng connection: " + e.getMessage());
        }
    }
}