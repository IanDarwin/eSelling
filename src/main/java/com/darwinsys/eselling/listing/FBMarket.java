package com.darwinsys.eselling.listing;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
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

	List<String> warnings;

	Workbook wb = new HSSFWorkbook();
	Sheet sheet;
	int rowNum;
	int numItems;

	@Override
	public void startStream(String location) {
        warnings = new ArrayList<>();
		numItems = rowNum = 0;

		// Creating a Sheet from the workbench
		sheet = wb.createSheet("Listing");

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

	}

	public ListResponse list(Item item) {

		// Now a row from each Item:
		int numMessages = 0;

		String fbURL = item.getUrl(MarketName.FBMarket);

		if (fbURL != null && !fbURL.isEmpty()) {
			warnings.add(String.format("`%s' is already listed as %s", item.getName(), fbURL));
			++numMessages;
		}

		// Field order (0-origin): Title, Price, Condition, Description, Category
		Row row = sheet.createRow(++rowNum);

		Cell cell = row.createCell(0);
		cell.setCellValue(item.getName());

		cell = row.createCell(1);
		cell.setCellValue(item.getAskingPrice().intValue());

		cell = row.createCell(2);
		if (item.getCondition() == null) {
			cell.setCellValue("Used - Good"); // Best guess
		} else {
			switch (item.getCondition()) {
				case NEW:
					cell.setCellValue("New");
					break;
				case LIKE_NEW:
					cell.setCellValue("New");
					break;
				case USED:
					cell.setCellValue("Used - Good");
					break;
				case FOR_PARTS:
					cell.setCellValue("Broken/Not Working");
					break;
				default:
					break;
			}
		}

		cell = row.createCell(3);
		cell.setCellValue(item.getDescription());

		cell = row.createCell(4);
		cell.setCellValue(item.getCategory().fbCategory());
		return new ListResponse(location, 1, warnings);
	}

	@Override
	public ListResponse list(Collection<Item> items) {
		var r = Market.super.list(items);
		r.setLocation(location);
		return r;
	}

	@Override
	public ListResponse closeStream() {

		try (OutputStream fileOut = new FileOutputStream(location)) {
			wb.write(fileOut);
			final ListResponse listResponse = new ListResponse(location, rowNum, warnings);
			if (chatterly) {
				System.out.printf("Wrote %d items into %s with %d messages\n",
						numItems, location, warnings.size());
				System.out.println("listResponse = " + listResponse);
			}
			return listResponse;
		} catch (IOException ex) {
			throw new RuntimeException("IO Error: " + ex, ex);
		}
	}

    @Override
    public String getPostMessage() {
        return "Now send " + location + " to Facebook Market";
    }
}
