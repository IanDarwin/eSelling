package com.darwinsys.eselling.listing;

import java.io.*;

import com.darwinsys.eselling.model.Constants;
import com.darwinsys.eselling.model.Item;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class FBMarket {

    // Main driver method
    public static void main(String[] args) throws IOException {

		Item item = new Item();
		item.setName("Thing for sale");
		item.setDescription("""
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""");
		item.setAskingPrice(42d);
		item.setCondition(Constants.Condition.USED);

		String fname = FBMarket.list(item);
		System.out.println("FBMarket Upload is at " + fname);
	}

	public static String list(Item item) throws IOException {

        // Creating Workbook instances
        Workbook wb = new HSSFWorkbook();

		// XXX Should be parameterized, and last part sequenced/random
        var fileName = "/home/ian/eSelling/fbmarket.xlsx";

        OutputStream fileOut = new FileOutputStream(fileName);

        // Creating a Sheet from the workbench
        Sheet sheet = wb.createSheet("Listing");

		// A cell must be created from a specific row

		Cell cell;
		Row row = sheet.createRow(0);

		// FB wants titles in SCREAMING CAPS
		cell = row.createCell(0);
		cell.setCellValue("TITLE");

		cell = row.createCell(1);
		cell.setCellValue("PRICE");

		cell = row.createCell(2);
		cell.setCellValue("CONDITION");

		cell = row.createCell(3);
		cell.setCellValue("DESCRIPTION");

		cell = row.createCell(4);
		cell.setCellValue("CATEGORY");

		// Now a row from an Item
		// Field order (0-origin): Title, Price, Condition, Description, Category
		row = sheet.createRow(1);
		
		cell = row.createCell(0);
		cell.setCellValue(item.getName());

		cell = row.createCell(1);
		cell.setCellValue(item.getAskingPrice().intValue());

		cell = row.createCell(2);
		cell.setCellValue("Used - Good");

		cell = row.createCell(3);
		cell.setCellValue(item.getDescription());

		cell = row.createCell(4);
		cell.setCellValue("Category");

		wb.write(fileOut);

		return fileName;
	}
}
