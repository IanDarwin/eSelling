package com.darwinsys.eselling.listing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.darwinsys.eselling.model.Constants;
import com.darwinsys.eselling.model.Item;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

@ApplicationScoped
public class FBMarket implements Market<Item> {

	// Main driver method
	public static void main(String[] args) throws IOException {

		Item item = new Item();
		item.setName("Thing for sale");
		item.setDescription("""
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""");
		item.setAskingPrice(42d);
		item.setCondition(Constants.Condition.USED);

		var ret = new FBMarket().list(Set.of(item));
		System.out.println("FBMarket Upload is at " + ret.location());
		System.out.printf("ret contains = %d warnings\n", ret.warnings().size());
	}

	public ListResponse list(Set<Item> items) {

		// XXX Should be parameterized, and last part sequenced/randomized
		var fileName = "/home/ian/eSelling/fbmarket.xlsx";

		// Creating Workbook instances
		Workbook wb = new HSSFWorkbook();

		List<String> warnings = new ArrayList<>();

		try (OutputStream fileOut = new FileOutputStream(fileName)) {

			// Creating a Sheet from the workbench
			Sheet sheet = wb.createSheet("Listing");

			Row row = sheet.createRow(0);

			// A cell must be created from a specific row
			Cell cell;

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

			// Now a row from each Item:
			int rowNum = 0;

			for (Item item : items) {
				String fbURL = item.getUrls().get(2);

				if (fbURL != null && !fbURL.isEmpty()) {
					warnings.add(String.format("%s is already listed as %s, skipping", item.getName(), fbURL));
					continue;
				}
				// Field order (0-origin): Title, Price, Condition, Description, Category
				row = sheet.createRow(++rowNum);

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
			}

			wb.write(fileOut);
			System.out.printf("Wrote %d items into %s\n", items.size(), fileName);
			return new ListResponse(fileName, rowNum, warnings);
		} catch (IOException ex) {
			throw new RuntimeException("IO Error: " + ex, ex);
		}
	}
}
