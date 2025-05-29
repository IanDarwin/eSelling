package com.darwinsys.eselling.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ESalesViewTest {

    @Test
    public void testUrlValidatorNotEntered() {
        assertTrue(ESalesView.urlFieldValidator.test(""));
    }

    @Test
    public void testUrlValidatorGood() {
        assertTrue(ESalesView.urlFieldValidator.test("http://listingco.com/item/12345"));
    }

    @Test
    public void testUrlValidatorBad() {
        assertFalse(ESalesView.urlFieldValidator.test("meep meep"));
    }
}