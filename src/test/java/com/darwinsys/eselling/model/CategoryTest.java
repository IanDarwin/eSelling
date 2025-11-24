package com.darwinsys.eselling.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    @Test
	public void testGetCategory() {
		Category c = new Category("ZZZZ", "FB", 41, 42);

        assertEquals("FB", c.fbCategory());
        assertEquals(41, c.eBayCategory());
        assertEquals(42, c.kijijiCategory());
	}
}
