package com.warehouse.view;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class SupplierPanel extends JPanel {
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, searchButton;

    public SupplierPanel() {
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
        addButton = new JButton("➕ Thêm nhà cung cấp");
        editButton = new JButton("✏️ Sửa");
        deleteButton = new JButton("🗑️ Xóa");

        addButton.setBackground(new Color(0, 153, 0));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(0, 102, 204));
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(searchPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanel, BorderLayout.CENTER);

        // Bảng hiển thị
        String[] columns = {"ID", "Mã NCC", "Tên nhà cung cấp", "SĐT", "Email", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        supplierTable = new JTable(tableModel);
        supplierTable.setFont(new Font("Arial", Font.PLAIN, 13));
        supplierTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách nhà cung cấp"));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTable(java.util.List<Map<String, Object>> suppliers) {
        tableModel.setRowCount(0);
        for (Map<String, Object> supplier : suppliers) {
            tableModel.addRow(new Object[]{
                    supplier.get("id"),
                    supplier.get("code"),
                    supplier.get("name"),
                    supplier.get("phone"),
                    supplier.get("email"),
                    supplier.get("address")
            });
        }
    }

    public int getSelectedSupplierId() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (int) tableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    public Map<String, Object> getSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow >= 0) {
            Map<String, Object> supplier = new HashMap<>();
            supplier.put("id", tableModel.getValueAt(selectedRow, 0));
            supplier.put("code", tableModel.getValueAt(selectedRow, 1));
            supplier.put("name", tableModel.getValueAt(selectedRow, 2));
            supplier.put("phone", tableModel.getValueAt(selectedRow, 3));
            supplier.put("email", tableModel.getValueAt(selectedRow, 4));
            supplier.put("address", tableModel.getValueAt(selectedRow, 5));
            return supplier;
        }
        return null;
    }

    public String getSearchKeyword() {
        return searchField.getText();
    }

    public JButton getAddButton() { return addButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getSearchButton() { return searchButton; }
}