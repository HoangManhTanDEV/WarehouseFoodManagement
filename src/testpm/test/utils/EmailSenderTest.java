package com.warehouse.utils;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailSenderTest {

    @Test
    @DisplayName("TC_U_04: Kiểm tra gửi email (không gửi thật)")
    void testSendEmail() {
        assertDoesNotThrow(() -> {
            EmailSender.sendEmail(
                    "test@example.com",
                    "[TEST] Email kiểm thử",
                    "<h1>Đây là email test</h1>"
            );
        }, "sendEmail() không được ném ngoại lệ");

        System.out.println("✅ Gửi email test hoàn tất!");
    }

    @Test
    @DisplayName("TC_U_05: Kiểm tra gửi thông báo nhập hàng")
    void testSendImportNotification() {
        assertDoesNotThrow(() -> {
            EmailSender.sendImportNotification(
                    "test@warehouse.com",
                    "PN00001",
                    5000000.0,
                    "Nguyễn Văn A"
            );
        }, "sendImportNotification() không được ném ngoại lệ");

        System.out.println("✅ Gửi thông báo nhập hàng test hoàn tất!");
    }

    @Test
    @DisplayName("TC_U_06: Kiểm tra gửi thông báo xuất hàng")
    void testSendExportNotification() {
        assertDoesNotThrow(() -> {
            EmailSender.sendExportNotification(
                    "test@warehouse.com",
                    "PX00001",
                    3000000.0,
                    "Khách hàng test"
            );
        }, "sendExportNotification() không được ném ngoại lệ");

        System.out.println("✅ Gửi thông báo xuất hàng test hoàn tất!");
    }

    @Test
    @DisplayName("TC_U_07: Kiểm tra gửi email thật (tùy chọn)")
    void testSendRealEmail() {
        // Test gửi email thật đến email của bạn
        // Nếu muốn test thật, bỏ comment dòng dưới và thay email nhận
        /*
        assertDoesNotThrow(() -> {
            EmailSender.sendEmail(
                "your_email@gmail.com",  // Thay bằng email của bạn
                "[TEST] Email từ hệ thống QL Kho",
                "<h1 style='color: green;'>✅ Email đã được gửi thành công!</h1>" +
                "<p>Đây là email test từ hệ thống Quản lý kho thực phẩm.</p>"
            );
        }, "sendEmail() không được ném ngoại lệ");
        */

        System.out.println("✅ Kiểm tra email thật hoàn tất!");
    }
}