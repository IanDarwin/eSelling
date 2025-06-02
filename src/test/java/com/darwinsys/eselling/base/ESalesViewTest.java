package com.darwinsys.eselling.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ESalesViewTest {

    @Test
    public void testUrlValidatorNotEntered() {
        assertTrue(AdminView.urlFieldValidator.test(""));
    }

    @Test
    public void testUrlValidatorGood() {
        assertTrue(AdminView.urlFieldValidator.test("http://listingco.com/item/12345"));
    }

    @Test
    public void testUrlValidatorBad() {
        assertFalse(AdminView.urlFieldValidator.test("meep meep"));
    }
}