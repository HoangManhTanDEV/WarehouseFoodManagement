package com.warehouse.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    // ============ EMAIL CỦA BẠN ============
    private static final String FROM_EMAIL = "manhtan120905@gmail.com";
    private static final String FROM_PASSWORD = "ycct unfm pdgi uyys";

    public static void sendEmail(String toEmail, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ [EMAIL] Đã gửi email thành công đến: " + toEmail);

        } catch (MessagingException e) {
            System.out.println("❌ [EMAIL] Gửi email thất bại: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendImportNotification(String toEmail, String receiptCode, double totalAmount, String userName) {
        String subject = "📥 NHẬP KHO - Mã phiếu: " + receiptCode;
        String content = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #0066cc;">THÔNG BÁO NHẬP HÀNG</h2>
                <hr/>
                <p><b>Mã phiếu:</b> %s</p>
                <p><b>Ngày nhập:</b> %s</p>
                <p><b>Tổng tiền:</b> %,.0f đ</p>
                <p><b>Người nhập:</b> %s</p>
                <hr/>
                <p style="color: green;">✅ Đã cập nhật tồn kho thành công!</p>
            </body>
            </html>
            """, receiptCode, new java.util.Date(), totalAmount, userName);

        sendEmail(toEmail, subject, content);
    }

    // ============ THÊM METHOD GỬI THÔNG BÁO XUẤT HÀNG ============
    public static void sendExportNotification(String toEmail, String receiptCode, double totalAmount, String customerName) {
        String subject = "📤 XUẤT KHO - Mã phiếu: " + receiptCode;
        String content = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #0066cc;">THÔNG BÁO XUẤT HÀNG</h2>
                <hr/>
                <p><b>Mã phiếu:</b> %s</p>
                <p><b>Ngày xuất:</b> %s</p>
                <p><b>Khách hàng:</b> %s</p>
                <p><b>Tổng tiền:</b> %,.0f đ</p>
                <hr/>
                <p style="color: green;">✅ Đã xuất hàng thành công!</p>
                <p style="color: gray; font-size: 12px;">Hệ thống quản lý kho thực phẩm</p>
            </body>
            </html>
            """, receiptCode, new java.util.Date(), customerName, totalAmount);

        sendEmail(toEmail, subject, content);
    }
}