package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Constants;
import com.darwinsys.eselling.model.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
            item1 = new Item();
            item1.setName("Thing for sale");
            item1.setDescription("""
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""");
            item1.setAskingPrice(42d);
            item1.setCondition(Constants.Condition.USED);

            item2 = new Item();
            item2.setName("Another Thing for sale");
            item2.setDescription("""
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""");
            item2.setAskingPrice(42d);
            item2.setCondition(Constants.Condition.USED);
            item2.getUrls().set(2, "Non-empty string for test");
    }

    @Test
    public void testOne() {
        assertEquals(42d, item1.getAskingPrice());
    }

    @Test
    public void testWarnings() {
        var ret = new FBMarket().list(Set.of(item1, item2));
        System.out.println("testWarnings: ret = " + ret);
        assertEquals(FBMarket.location, ret.location());
        assertEquals(1, ret.warnings().size());
    }
}
