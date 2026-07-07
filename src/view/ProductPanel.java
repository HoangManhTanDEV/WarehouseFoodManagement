package com.warehouse.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, searchButton;

    public ProductPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel trên cùng - Tìm kiếm
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        searchButton = new JButton("🔍 Tìm");
        refreshButton = new JButton("🔄 Làm mới");
        topPanel.add(searchButton);
        topPanel.add(refreshButton);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("➕ Thêm sản phẩm");
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

        // Panel chứa các nút
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanel, BorderLayout.CENTER);

        // Bảng hiển thị sản phẩm
        String[] columns = {"ID", "Mã SP", "Tên sản phẩm", "Loại", "Đơn vị", "SL tồn", "Giá bán", "Nhà cung cấp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 13));
        productTable.setRowHeight(25);
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTable(List<Map<String, Object>> products) {
        tableModel.setRowCount(0);
        for (Map<String, Object> product : products) {
            tableModel.addRow(new Object[]{
                    product.get("id"),
                    product.get("code"),
                    product.get("name"),
                    product.get("category"),
                    product.get("unit"),
                    product.get("quantity"),
                    String.format("%,.0f đ", product.get("sell_price")),
                    product.get("supplier_name")
            });
        }
    }

    public int getSelectedProductId() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (int) tableModel.getValueAt(selectedRow, 0);
        }
        return -1;
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