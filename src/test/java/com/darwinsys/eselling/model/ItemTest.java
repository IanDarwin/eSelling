package com.darwinsys.eselling.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ItemTest {

    @Test
    public void testTags() {
        Item item = new Item();
        item.setTags("tom,  dick, \tharriet");
        String[] expect = List.of("tom", "dick", "harriet").toArray(new String[3]);
        var actual = item.getTagsAsArray();
        assertArrayEquals(expect, actual);
    }
}
