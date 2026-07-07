package com.warehouse.view;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

public class MainViewTest {

    private MainView mainView;

    @BeforeEach
    void setUp() {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "admin");
        userInfo.put("fullname", "Quản trị viên");
        userInfo.put("role", "admin");
        userInfo.put("email", "admin@warehouse.com");

        mainView = new MainView(userInfo);
        mainView.setVisible(false);
    }

    @AfterEach
    void tearDown() {
        mainView.dispose();
    }

    @Test
    @DisplayName("TC_V_10: Kiểm tra MainView khởi tạo thành công")
    void testMainViewCreated() {
        assertNotNull(mainView, "MainView không được null");
        assertTrue(mainView.getTitle().contains("QUẢN LÝ KHO THỰC PHẨM"),
                "Tiêu đề phải chứa 'QUẢN LÝ KHO THỰC PHẨM'");
        assertTrue(mainView.getTitle().contains("Quản trị viên"),
                "Tiêu đề phải chứa tên user");
    }

    @Test
    @DisplayName("TC_V_11: Kiểm tra MainView có các tab cần thiết (Admin)")
    void testTabsExistForAdmin() {
        JTabbedPane tabbedPane = mainView.getTabbedPane();
        assertNotNull(tabbedPane, "TabbedPane không được null");

        // Kiểm tra số lượng tab (Admin có 8 tab)
        int tabCount = tabbedPane.getTabCount();
        assertEquals(8, tabCount, "Admin phải có đúng 8 tab");

        // Kiểm tra các tab thực tế dựa trên output
        String[] actualTabs = {
                "📦 SẢN PHẨM",
                "🏭 NHÀ CUNG CẤP",
                "📥 NHẬP HÀNG",
                "📤 XUẤT HÀNG",
                "📊 TỒN KHO",
                "📈 THỐNG KÊ",
                "👤 TÀI KHOẢN",
                "🏠 DASHBOARD"
        };

        for (int i = 0; i < actualTabs.length; i++) {
            String expected = actualTabs[i];
            String actual = tabbedPane.getTitleAt(i);
            assertEquals(expected, actual, "Tab " + i + " phải là '" + expected + "', thực tế: '" + actual + "'");
        }
    }

    @Test
    @DisplayName("TC_V_12: Kiểm tra MainView cho Staff không có tab Tài khoản")
    void testTabsForStaff() {
        Map<String, String> staffInfo = new HashMap<>();
        staffInfo.put("username", "staff1");
        staffInfo.put("fullname", "Nhân viên");
        staffInfo.put("role", "staff");
        staffInfo.put("email", "staff@warehouse.com");

        MainView staffView = new MainView(staffInfo);
        staffView.setVisible(false);

        JTabbedPane tabbedPane = staffView.getTabbedPane();
        assertNotNull(tabbedPane);

        // Staff có 7 tab (không có TÀI KHOẢN)
        assertEquals(7, tabbedPane.getTabCount(), "Staff phải có đúng 7 tab");

        // Kiểm tra không có tab TÀI KHOẢN
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            assertFalse(title.contains("TÀI KHOẢN"), "Staff không được thấy tab TÀI KHOẢN");
        }

        // Kiểm tra staff có các tab cơ bản
        String[] staffTabs = {"SẢN PHẨM", "NHÀ CUNG CẤP", "NHẬP HÀNG", "XUẤT HÀNG", "TỒN KHO", "THỐNG KÊ", "DASHBOARD"};
        for (String expectedTab : staffTabs) {
            boolean found = false;
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getTitleAt(i).contains(expectedTab)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Staff phải có tab '" + expectedTab + "'");
        }

        staffView.dispose();
    }

    @Test
    @DisplayName("TC_V_13: Kiểm tra welcome label chứa tên user")
    void testWelcomeLabel() {
        JLabel welcomeLabel = mainView.getWelcomeLabel();
        assertNotNull(welcomeLabel, "Welcome label không được null");
        assertTrue(welcomeLabel.getText().contains("Quản trị viên"),
                "Welcome label phải chứa tên user");
        assertTrue(welcomeLabel.getText().contains("Quản trị viên"),
                "Welcome label phải chứa vai trò");
    }
}