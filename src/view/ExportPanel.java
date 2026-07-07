package com.warehouse.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class ExportPanel extends JPanel {
    private JTextField receiptCodeField, customerNameField;
    private JTable productTable, cartTable;
    private DefaultTableModel productTableModel, cartTableModel;
    private JButton createButton, addProductButton, removeProductButton, exportExcelButton;
    private Map<Integer, Map<String, Object>> cartItems = new HashMap<>();

    public ExportPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel thông tin phiếu xuất
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu xuất"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã phiếu
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Mã phiếu:"), gbc);
        gbc.gridx = 1;
        receiptCodeField = new JTextField(15);
        receiptCodeField.setEditable(false);
        infoPanel.add(receiptCodeField, gbc);

        // Tên khách hàng
        gbc.gridx = 2;
        infoPanel.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 3;
        customerNameField = new JTextField(20);
        infoPanel.add(customerNameField, gbc);

        // Nút tạo phiếu
        gbc.gridx = 4;
        createButton = new JButton("📋 Tạo phiếu xuất");
        createButton.setBackground(new Color(0, 102, 204));
        createButton.setForeground(Color.WHITE);
        infoPanel.add(createButton, gbc);

        // Panel danh sách sản phẩm
        String[] productColumns = {"ID", "Mã SP", "Tên sản phẩm", "Tồn kho", "Giá bán"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        JScrollPane productScroll = new JScrollPane(productTable);
        productScroll.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

        // Panel giỏ hàng
        String[] cartColumns = {"ID", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Giỏ hàng xuất"));

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addProductButton = new JButton("➕ Thêm vào giỏ");
        removeProductButton = new JButton("❌ Xóa khỏi giỏ");
        exportExcelButton = new JButton("📊 Xuất Excel");

        addProductButton.setBackground(new Color(0, 153, 0));
        addProductButton.setForeground(Color.WHITE);
        removeProductButton.setBackground(new Color(204, 0, 0));
        removeProductButton.setForeground(Color.WHITE);
        exportExcelButton.setBackground(new Color(0, 102, 204));
        exportExcelButton.setForeground(Color.WHITE);

        buttonPanel.add(addProductButton);
        buttonPanel.add(removeProductButton);
        buttonPanel.add(exportExcelButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, productScroll, cartScroll);
        splitPane.setResizeWeight(0.5);

        add(infoPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshProductTable(java.util.List<Map<String, Object>> products) {
        productTableModel.setRowCount(0);
        for (Map<String, Object> product : products) {
            productTableModel.addRow(new Object[]{
                    product.get("id"),
                    product.get("code"),
                    product.get("name"),
                    product.get("quantity"),
                    String.format("%,.0f đ", product.get("sell_price"))
            });
        }
    }

    public void setReceiptCode(String code) {
        receiptCodeField.setText(code);
    }

    public String getCustomerName() {
        return customerNameField.getText();
    }

    public int getSelectedProductId() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (int) productTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    public void addToCart(Map<String, Object> product, int quantity) {
        int productId = (int) product.get("id");
        int availableStock = (int) product.get("quantity");

        if (quantity > availableStock) {
            JOptionPane.showMessageDialog(this, "Số lượng xuất vượt quá tồn kho! (Tồn: " + availableStock + ")", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cartItems.containsKey(productId)) {
            Map<String, Object> existing = cartItems.get(productId);
            int newQuantity = (int) existing.get("quantity") + quantity;
            if (newQuantity > availableStock) {
                JOptionPane.showMessageDialog(this, "Tổng số lượng xuất vượt quá tồn kho!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            existing.put("quantity", newQuantity);
            existing.put("total", newQuantity * (double) existing.get("sell_price"));
        } else {
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("product_id", productId);
            cartItem.put("product_name", product.get("name"));
            cartItem.put("quantity", quantity);
            cartItem.put("sell_price", product.get("sell_price"));
            cartItem.put("total", quantity * (double) product.get("sell_price"));
            cartItems.put(productId, cartItem);
        }
        refreshCartTable();
    }

    public void removeFromCart(int productId) {
        cartItems.remove(productId);
        refreshCartTable();
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (Map<String, Object> item : cartItems.values()) {
            cartTableModel.addRow(new Object[]{
                    item.get("product_id"),
                    item.get("product_name"),
                    item.get("quantity"),
                    String.format("%,.0f đ", item.get("sell_price")),
                    String.format("%,.0f đ", item.get("total"))
            });
            total += (double) item.get("total");
        }
        cartTableModel.addRow(new Object[]{"", "", "", "TỔNG:", String.format("%,.0f đ", total)});
    }

    public double getTotalAmount() {
        double total = 0;
        for (Map<String, Object> item : cartItems.values()) {
            total += (double) item.get("total");
        }
        return total;
    }

    public java.util.List<Map<String, Object>> getCartDetails() {
        return new ArrayList<>(cartItems.values());
    }

    public void clearCart() {
        cartItems.clear();
        refreshCartTable();
        customerNameField.setText("");
    }

    public JButton getCreateButton() { return createButton; }
    public JButton getAddProductButton() { return addProductButton; }
    public JButton getRemoveProductButton() { return removeProductButton; }
    public JButton getExportExcelButton() { return exportExcelButton; }
    public JTable getCartTable() { return cartTable; }
    public JTextField getReceiptCodeField() { return receiptCodeField; }
}