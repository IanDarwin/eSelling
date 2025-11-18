package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CategoriesParser {

    public static final String DEFAULT_CATEGORIES_FILE = "categories.csv";
    private static CategoriesParser INSTANCE;
	public List<Category> categories;
	private static Object locker = new Object();

    static {
        if (INSTANCE == null) {
            INSTANCE = new CategoriesParser();
            INSTANCE.parse(DEFAULT_CATEGORIES_FILE);
        }
    }

	public static CategoriesParser getInstance() {
		return INSTANCE;
	}

    List<Category> parse(String fileName) {
        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(Path.of(fileName));
            return parse(br);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read " + fileName, ex);
        }
    }

    List<Category> parse(BufferedReader br) throws IOException {
        categories = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
           var raw = line.split(",");
           switch(raw.length) {
               case 2:
                   categories.add(new Category(raw[0], raw[1], -1));
                   break;
               case 3:
                   categories.add(new Category(raw[0], raw[1], Integer.parseInt(raw[2])));
                   break;
               default:
                   throw new IllegalArgumentException("Line " + line + " invalid, ncols " + raw.length);
           }
        }
        return categories;
    }

    /// Trivial demo
    public static void main(String[] args) {
        System.out.println("CategoriesParser DEMO");
        var ret = INSTANCE.parse(DEFAULT_CATEGORIES_FILE);

        for (Category c : ret) {
            System.out.println("c = " + c);
        }
    }

}
