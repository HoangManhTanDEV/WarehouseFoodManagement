package com.warehouse.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class UserPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, searchButton, changePasswordButton;
    private Map<String, String> currentUser; // Người dùng hiện tại

    public UserPanel(Map<String, String> currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        searchButton = new JButton("🔍 Tìm");
        refreshButton = new JButton("🔄 Làm mới");
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("➕ Thêm tài khoản");
        editButton = new JButton("✏️ Sửa");
        deleteButton = new JButton("🗑️ Xóa");
        changePasswordButton = new JButton("🔑 Đổi mật khẩu");

        addButton.setBackground(new Color(0, 153, 0));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(0, 102, 204));
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        changePasswordButton.setBackground(new Color(255, 140, 0));
        changePasswordButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(changePasswordButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(searchPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanel, BorderLayout.CENTER);

        // Bảng hiển thị
        String[] columns = {"ID", "Tên đăng nhập", "Họ tên", "Email", "Vai trò", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách tài khoản"));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTable(java.util.List<Map<String, Object>> users) {
        tableModel.setRowCount(0);
        for (Map<String, Object> user : users) {
            tableModel.addRow(new Object[]{
                    user.get("id"),
                    user.get("username"),
                    user.get("fullname"),
                    user.get("email"),
                    user.get("role").equals("admin") ? "Quản trị viên" : "Nhân viên",
                    user.get("created_at")
            });
        }
    }

    public int getSelectedUserId() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (int) tableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    public Map<String, Object> getSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", tableModel.getValueAt(selectedRow, 0));
            user.put("username", tableModel.getValueAt(selectedRow, 1));
            user.put("fullname", tableModel.getValueAt(selectedRow, 2));
            user.put("email", tableModel.getValueAt(selectedRow, 3));
            user.put("role", tableModel.getValueAt(selectedRow, 4).equals("Quản trị viên") ? "admin" : "staff");
            return user;
        }
        return null;
    }

    public String getSearchKeyword() {
        return searchField.getText();
    }

    public Map<String, String> getCurrentUser() {
        return currentUser;
    }

    public JButton getAddButton() { return addButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getSearchButton() { return searchButton; }
    public JButton getChangePasswordButton() { return changePasswordButton; }
}