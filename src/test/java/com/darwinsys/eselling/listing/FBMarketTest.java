package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Condition;
import com.darwinsys.eselling.model.Item;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FBMarketTest {
    static FBMarket target;
    Item item1, item2;

    @BeforeAll
    public static void createTarget() {
        target = new FBMarket();
    }

    @BeforeEach
    public void setup() {
        item1 = TestData.getItemOne();
        item2 = TestData.getItemTwo();
    }

    @Test
    public void testOne() {
        assertEquals(42d, item1.getAskingPrice());
    }

    @Test
    public void testWarnings() {
        var ret = new FBMarket().list(Set.of(item1, item2));
        System.out.println("testWarnings: ret = " + ret);
        assertEquals(FBMarket.location, ret.getLocation());
		for (var m : ret.getMessages()) {
			System.out.println("testWarnings: got warning: " + m);
		}
        assertEquals(1, ret.getMessages().size());
    }
}
