package com.warehouse.utils;

public class TestEmail {
    public static void main(String[] args) {
        // Gửi email test đến chính email của bạn
        EmailSender.sendEmail(
                "manhtan120905@gmail.com",  // ← Thay bằng email nhận test
                "[TEST] Kiểm tra cấu hình email",
                """
                <html>
                <body>
                    <h2 style="color: green;">✅ CHÚC MỪNG!</h2>
                    <p>Email đã được cấu hình thành công!</p>
                    <p>Hệ thống quản lý kho của bạn đã sẵn sàng gửi thông báo.</p>
                    <hr/>
                    <p style="color: gray;">Đây là email test từ phần mềm Quản lý kho thực phẩm.</p>
                </body>
                </html>
                """
        );
    }
}