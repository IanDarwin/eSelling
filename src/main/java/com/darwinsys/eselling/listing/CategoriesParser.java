package com.darwinsys.eselling.listing;

import org.checkerframework.checker.units.qual.C;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CategoriesParser {

    public static void main(String[] args) {
        System.out.println("CategoriesParser DEMO");
        var ret = new CategoriesParser().parse("categories.csv");
        for (Cat c : ret) {
            System.out.println("c = " + c);
        }
    }

    record Cat(String name, String fbCat, int eBayCat) {
        //
    }

    List<Cat> parse(String fileName) {
        BufferedReader bs = null;
        try {
            bs = Files.newBufferedReader(Path.of(fileName));
            return parse(bs);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read " + fileName, ex);
        }
    }

    private List<Cat> parse(BufferedReader br) throws IOException {
        List<Cat> results = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
           var raw = line.split(",");
           switch(raw.length) {
               case 2:
                   results.add(new Cat(raw[0], raw[1], -1));
                   break;
               case 3:
                   results.add(new Cat(raw[0], raw[1], Integer.parseInt(raw[2])));
                   break;
               default:
                   throw new IllegalArgumentException("Line " + line + " invalid, ncols " + raw.length);
           }
        }

        return results;
    }
}
