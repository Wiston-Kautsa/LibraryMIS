package com.mycompany.librarymis;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BulkService {

    /* ================= IMPORT MEMBERS ================= */

    public void importMembers(Window window) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Members Excel File");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = chooser.showOpenDialog(window);

        if (file == null) {
            return;
        }

        DatabaseHandler handler = DatabaseHandler.getInstance();

        String sql = "INSERT INTO MEMBER(id,name,mobile,email) VALUES(?,?,?,?)";

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis);
             Connection conn = handler.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue; // skip header
                }

                String name = getValue(row.getCell(0));
                String mobile = getValue(row.getCell(1));
                String email = getValue(row.getCell(2));

                if (name.isBlank()) {
                    continue;
                }

                String id = handler.generateMemberID();

                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, mobile);
                ps.setString(4, email);

                ps.executeUpdate();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    /* ================= IMPORT BOOKS ================= */

    public void importBooks(Window window) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Books Excel File");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = chooser.showOpenDialog(window);

        if (file == null) {
            return;
        }

        DatabaseHandler handler = DatabaseHandler.getInstance();

        String sql =
                "INSERT INTO BOOK(id,title,author,publisher,isAvail) VALUES(?,?,?,?,TRUE)";

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis);
             Connection conn = handler.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue;
                }

                String title = getValue(row.getCell(0));
                String author = getValue(row.getCell(1));
                String publisher = getValue(row.getCell(2));

                if (title.isBlank()) {
                    continue;
                }

                String id = handler.generateBookID();

                ps.setString(1, id);
                ps.setString(2, title);
                ps.setString(3, author);
                ps.setString(4, publisher);

                ps.executeUpdate();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    /* ================= SAFE CELL VALUE ================= */

    private String getValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {

            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            default:
                return "";
        }
    }
}