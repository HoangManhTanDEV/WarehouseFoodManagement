package com.warehouse.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class ImportPanel extends JPanel {
    private JComboBox<String> supplierCombo;
    private JTextField receiptCodeField;
    private JButton createButton, addProductButton, removeProductButton;
    private JTable productTable, cartTable;
    private DefaultTableModel productTableModel, cartTableModel;
    private Map<Integer, Map<String, Object>> cartItems = new HashMap<>();

    public ImportPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel thông tin phiếu nhập
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập"));
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

        // Nhà cung cấp
        gbc.gridx = 2;
        infoPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 3;
        supplierCombo = new JComboBox<>();
        supplierCombo.setPreferredSize(new Dimension(200, 25));
        infoPanel.add(supplierCombo, gbc);

        // Nút tạo phiếu
        gbc.gridx = 4;
        createButton = new JButton("📋 Tạo phiếu nhập mới");
        createButton.setBackground(new Color(0, 102, 204));
        createButton.setForeground(Color.WHITE);
        infoPanel.add(createButton, gbc);

        // Panel danh sách sản phẩm
        String[] productColumns = {"ID", "Mã SP", "Tên sản phẩm", "Giá nhập"};
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
                return column == 2; // Chỉ cho sửa cột số lượng
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Giỏ hàng nhập"));

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addProductButton = new JButton("➕ Thêm vào giỏ");
        removeProductButton = new JButton("❌ Xóa khỏi giỏ");
        addProductButton.setBackground(new Color(0, 153, 0));
        addProductButton.setForeground(Color.WHITE);
        removeProductButton.setBackground(new Color(204, 0, 0));
        removeProductButton.setForeground(Color.WHITE);
        buttonPanel.add(addProductButton);
        buttonPanel.add(removeProductButton);

        // Split pane
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
                    String.format("%,.0f đ", product.get("import_price"))
            });
        }
    }

    public void refreshSupplierCombo(java.util.List<Map<String, Object>> suppliers) {
        supplierCombo.removeAllItems();
        for (Map<String, Object> supplier : suppliers) {
            supplierCombo.addItem(supplier.get("id") + " - " + supplier.get("name"));
        }
    }

    public void setReceiptCode(String code) {
        receiptCodeField.setText(code);
    }

    public int getSelectedProductId() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (int) productTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    public Map<String, Object> getCartItemAt(int row) {
        if (row >= 0 && row < cartTableModel.getRowCount()) {
            Map<String, Object> item = new HashMap<>();
            item.put("product_id", cartTableModel.getValueAt(row, 0));
            item.put("product_name", cartTableModel.getValueAt(row, 1));
            item.put("quantity", cartTableModel.getValueAt(row, 2));
            item.put("unit_price", cartTableModel.getValueAt(row, 3));
            return item;
        }
        return null;
    }

    public void addToCart(Map<String, Object> product, int quantity, double unitPrice) {
        int productId = (int) product.get("id");
        if (cartItems.containsKey(productId)) {
            // Cập nhật số lượng
            Map<String, Object> existing = cartItems.get(productId);
            int newQuantity = (int) existing.get("quantity") + quantity;
            existing.put("quantity", newQuantity);
            existing.put("total", newQuantity * (double) existing.get("unit_price"));
        } else {
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("product_id", productId);
            cartItem.put("product_name", product.get("name"));
            cartItem.put("quantity", quantity);
            cartItem.put("unit_price", unitPrice);
            cartItem.put("total", quantity * unitPrice);
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
                    String.format("%,.0f đ", item.get("unit_price")),
                    String.format("%,.0f đ", item.get("total"))
            });
            total += (double) item.get("total");
        }
        // Thêm dòng tổng tiền
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

    public int getSelectedSupplierId() {
        String selected = (String) supplierCombo.getSelectedItem();
        if (selected != null) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }

    public void clearCart() {
        cartItems.clear();
        refreshCartTable();
    }

    public JButton getCreateButton() { return createButton; }
    public JButton getAddProductButton() { return addProductButton; }
    public JButton getRemoveProductButton() { return removeProductButton; }
    // Thêm vào cuối class ImportPanel, trước dấu } cuối cùng:

    public JTable getCartTable() {
        return cartTable;
    }

    public JTextField getReceiptCodeField() {
        return receiptCodeField;
    }

    public DefaultTableModel getCartTableModel() {
        return cartTableModel;
    }
}