package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Condition;
import com.darwinsys.eselling.model.Item;

public class TestData {

    public static Item getItemOne() {
        Item item1 = new Item();
        item1.setName("Something for sale");
        item1.setDescription("""
                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""");
        item1.setAskingPrice(42d);
        item1.setCondition(Condition.USED);
        return item1;
    }

    public static Item getItemTwo() {
        Item item2 = new Item();
        item2.setName("Another Thing for sale");
        item2.setDescription("""
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""");
        item2.setAskingPrice(42d);
        item2.setCondition(Condition.USED);
        item2.getUrls().set(2, "Non-empty string for test");
        return item2;
    }
}
