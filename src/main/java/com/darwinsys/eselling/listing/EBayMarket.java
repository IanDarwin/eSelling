package com.darwinsys.eselling.listing;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.darwinsys.eselling.io.CategoriesParser;
import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Item;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EBayMarket implements Market<Item> {

	// XXX Should be parameterized, and last part sequenced/randomized?
	public static final String location = "/home/ian/eSelling/eBayMarket.csv";

    Category category;

	// Fields: id,ebayCat,title,price,description
	public static final String PATTERN = """
#INFO,Version=0.0.2,Template= eBay-draft-listings-template_ENCA,,,,,,,,
Action(SiteID=Canada|Country=CA|Currency=CAD|Version=1193|CC=UTF-8),Custom label (SKU),Category ID,Title,UPC,Price,Quantity,Item photo URL,Condition ID,Description,Format
Draft,%d,%d,%s,,%g,1,,USED,"%s",FixedPrice
""";

	static final String POST_MESSAGE = """
Now upload this draft to the Seller Hub Reports tab
and complete the draft to make it active at https://www.ebay.ca/sh/reports/uploads
""";

	PrintWriter os;

    @Override
    public void startStream(String location) {
		try {
			os = new PrintWriter(Files.newOutputStream(Path.of(location)));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.toString(), ioe);
		}
	}

    @Override
    public ListResponse list(Item item) {
        if (category == null) {
			category = item.getCategory();
		} else if (!category.equals(item.getCategory())) {
            var r = new ListResponse();
            r.setSuccessCount(0);
            r.setMessages(List.of("EBayMarket Export: only do one category at a time, sorry"));
        }
		String output = String.format(PATTERN, item.getId(),
                ebayCategory(item.getCategory()), item.getName(),
                item.getAskingPrice(), item.getDescription());
		System.out.println("DEBUG: " + output);
		os.println(output);
		return new ListResponse(location, 1, List.of());
	}

    @Override
    public ListResponse closeStream() {
		os.close();
        var r = new ListResponse(location, 1, List.of());
        return r;
	}

    @Override
    public String getPostMessage() {
        return POST_MESSAGE;
    }

	private int ebayCategory(Category category) {
		for (Category c : CategoriesParser.getInstance().categories) {
			if (c == category) {
				return c.eBayCategory();
			}
		}
		return -1;	// Expected to cause the upload to fail.
	}
}
