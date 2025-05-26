package com.darwinsys.eselling.listing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.darwinsys.eselling.model.Item;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import static com.darwinsys.eselling.model.Condition.*;


@ApplicationScoped
public class FBMarket implements Market<Item> {

	private final boolean chatterly = false;

	// XXX Should be parameterized, and last part sequenced/randomized?
	public static final String location = "/home/ian/eSelling/fbmarket.xlsx";

	public ListResponse list(Set<Item> items) {

		// Creating Workbook instances
		Workbook wb = new HSSFWorkbook();

		List<String> warnings = new ArrayList<>();

		try (OutputStream fileOut = new FileOutputStream(location)) {

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
				String fbURL = item.getUrl(MarketName.FBMarket);

				if (fbURL != null && !fbURL.isEmpty()) {
					warnings.add(String.format("`%s' is already listed as %s, skipping", item.getName(), fbURL));
					continue;
				}
				// Field order (0-origin): Title, Price, Condition, Description, Category
				row = sheet.createRow(++rowNum);

				cell = row.createCell(0);
				cell.setCellValue(item.getName());

				cell = row.createCell(1);
				cell.setCellValue(item.getAskingPrice().intValue());

				cell = row.createCell(2);
				if (item.getCondition() == null) {
					cell.setCellValue("Used - Good"); // Best guess
				} else {
					switch(item.getCondition()) {
						case NEW:
							cell.setCellValue("New"); break;
						case LIKE_NEW:
							cell.setCellValue("New"); break;
						case USED:
							cell.setCellValue("Used - Good"); break;
						case FOR_PARTS:
							cell.setCellValue("Broken/Not Working"); break;
						default:
							break;
					}
				}

				cell = row.createCell(3);
				cell.setCellValue(item.getDescription());

				cell = row.createCell(4);
				cell.setCellValue("Category");
			}

			wb.write(fileOut);
			final ListResponse listResponse = new ListResponse(location, rowNum, warnings);
			if (chatterly) {
				System.out.printf("Wrote %d items into %s with %d warnings\n",
						items.size(), location, warnings.size());
				System.out.println("listResponse = " + listResponse);
			}
			return listResponse;
		} catch (IOException ex) {
			throw new RuntimeException("IO Error: " + ex, ex);
		}
	}
}
