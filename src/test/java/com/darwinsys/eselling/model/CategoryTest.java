package com.darwinsys.eselling.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryTest {

    @Test
    public void testSimpleNames() {
        assertEquals("Antiques", Category.Antiques.toString());
    }

    @Test
    public void testCamelCaseNames() {
        assertEquals("Musical Instruments", Category.MusicalInstruments.toString());
    }
}
