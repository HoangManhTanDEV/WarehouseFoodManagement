package com.warehouse.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    // ============ XUẤT PHIẾU XUẤT KHO ============
    public static void exportExportReceiptToExcel(Map<String, Object> receipt, List<Map<String, Object>> details) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Phieu_Xuat_" + receipt.get("receipt_code"));

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("PHIẾU XUẤT KHO");
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            CellStyle infoStyle = workbook.createCellStyle();
            infoStyle.setBorderBottom(BorderStyle.THIN);
            infoStyle.setBorderTop(BorderStyle.THIN);
            infoStyle.setBorderLeft(BorderStyle.THIN);
            infoStyle.setBorderRight(BorderStyle.THIN);

            int rowNum = 2;

            Row infoRow1 = sheet.createRow(rowNum++);
            infoRow1.createCell(0).setCellValue("Mã phiếu:");
            infoRow1.createCell(1).setCellValue((String) receipt.get("receipt_code"));
            infoRow1.createCell(3).setCellValue("Ngày xuất:");
            infoRow1.createCell(4).setCellValue(receipt.get("export_date").toString());

            Row infoRow2 = sheet.createRow(rowNum++);
            infoRow2.createCell(0).setCellValue("Khách hàng:");
            infoRow2.createCell(1).setCellValue((String) receipt.get("customer_name"));
            infoRow2.createCell(3).setCellValue("Người lập:");
            infoRow2.createCell(4).setCellValue((String) receipt.get("user_name"));

            rowNum++;

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            tableHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
            tableHeaderStyle.setBorderTop(BorderStyle.THIN);
            tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
            tableHeaderStyle.setBorderRight(BorderStyle.THIN);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            tableHeaderStyle.setFont(boldFont);

            Row headerRow = sheet.createRow(rowNum++);
            String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(tableHeaderStyle);
                sheet.setColumnWidth(i, 4000);
            }

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            int stt = 1;
            double total = 0;
            for (Map<String, Object> detail : details) {
                Row row = sheet.createRow(rowNum++);
                double amount = (int) detail.get("quantity") * (double) detail.get("sell_price");
                total += amount;

                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue((String) detail.get("code"));
                row.createCell(2).setCellValue((String) detail.get("product_name"));
                row.createCell(3).setCellValue((int) detail.get("quantity"));
                row.createCell(4).setCellValue((double) detail.get("sell_price"));
                row.createCell(5).setCellValue(amount);

                for (int i = 0; i < columns.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            Row totalRow = sheet.createRow(rowNum++);
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalStyle.setBorderBottom(BorderStyle.THIN);
            totalStyle.setBorderLeft(BorderStyle.THIN);
            totalStyle.setBorderRight(BorderStyle.THIN);

            Cell tongLabelCell = totalRow.createCell(4);
            tongLabelCell.setCellValue("TỔNG CỘNG:");
            tongLabelCell.setCellStyle(totalStyle);

            Cell tongValueCell = totalRow.createCell(5);
            tongValueCell.setCellValue(total);
            tongValueCell.setCellStyle(totalStyle);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu phiếu xuất Excel");
            fileChooser.setSelectedFile(new java.io.File("PhieuXuat_" + receipt.get("receipt_code") + ".xlsx"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                    JOptionPane.showMessageDialog(null, "Xuất phiếu xuất thành công!\nLưu tại: " + filePath,
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============ XUẤT BÁO CÁO TỒN KHO ============
    public static void exportInventoryToExcel(List<Map<String, Object>> inventory) {
        Workbook workbook = null;
        FileOutputStream fileOut = null;

        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Bao_cao_ton_kho");

            // --- Tạo Font và Style cho tiêu đề ---
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // --- Tạo tiêu đề chính ---
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(25);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO TỒN KHO THỰC PHẨM");
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 10));

            // --- Dòng thời gian ---
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Ngày lập báo cáo: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 10));

            // --- Header bảng ---
            Row headerRow = sheet.createRow(3);
            String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Loại", "Đơn vị",
                    "SL tồn", "Giá nhập", "Giá bán", "HSD", "Nhà cung cấp", "Trạng thái"};

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            tableHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
            tableHeaderStyle.setBorderTop(BorderStyle.THIN);
            tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
            tableHeaderStyle.setBorderRight(BorderStyle.THIN);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            tableHeaderStyle.setFont(boldFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(tableHeaderStyle);
                sheet.setColumnWidth(i, i == 2 ? 6000 : 4000);
            }

            // --- Đổ dữ liệu ---
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // Màu cho trạng thái
            CellStyle redStyle = workbook.createCellStyle();
            redStyle.cloneStyleFrom(dataStyle);
            redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle orangeStyle = workbook.createCellStyle();
            orangeStyle.cloneStyleFrom(dataStyle);
            orangeStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            orangeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 4;
            int stt = 1;
            for (Map<String, Object> item : inventory) {
                Row row = sheet.createRow(rowNum++);
                String status = item.get("status") != null ? item.get("status").toString() : "";

                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(item.get("code") != null ? item.get("code").toString() : "");
                row.createCell(2).setCellValue(item.get("name") != null ? item.get("name").toString() : "");
                row.createCell(3).setCellValue(item.get("category") != null ? item.get("category").toString() : "");
                row.createCell(4).setCellValue(item.get("unit") != null ? item.get("unit").toString() : "");
                row.createCell(5).setCellValue(item.get("quantity") != null ? Integer.parseInt(item.get("quantity").toString()) : 0);
                row.createCell(6).setCellValue(item.get("import_price") != null ? Double.parseDouble(item.get("import_price").toString()) : 0);
                row.createCell(7).setCellValue(item.get("sell_price") != null ? Double.parseDouble(item.get("sell_price").toString()) : 0);
                row.createCell(8).setCellValue(item.get("expiry_date") != null ? item.get("expiry_date").toString() : "");
                row.createCell(9).setCellValue(item.get("supplier_name") != null ? item.get("supplier_name").toString() : "");

                // Tô màu cho cột trạng thái
                Cell statusCell = row.createCell(10);
                statusCell.setCellValue(status);
                if (status.contains("Hết hạn")) {
                    statusCell.setCellStyle(redStyle);
                } else if (status.contains("CẢNH BÁO") || status.contains("Sắp hết")) {
                    statusCell.setCellStyle(orangeStyle);
                } else {
                    statusCell.setCellStyle(dataStyle);
                }

                // Apply style cho các cell khác
                for (int i = 0; i < 10; i++) {
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }

            // --- Dòng tổng kết ---
            Row summaryRow = sheet.createRow(rowNum + 1);
            CellStyle summaryStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            summaryStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellValue("TỔNG SỐ SẢN PHẨM: " + inventory.size());
            summaryCell.setCellStyle(summaryStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum + 1, rowNum + 1, 0, 10));

            // --- Lưu file ---
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu báo cáo tồn kho Excel");
            fileChooser.setSelectedFile(new java.io.File("Bao_cao_ton_kho.xlsx"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                fileOut = new FileOutputStream(filePath);
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null, "Xuất báo cáo tồn kho thành công!\nLưu tại: " + filePath,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (fileOut != null) fileOut.close();
                if (workbook != null) workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}