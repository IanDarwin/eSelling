package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {
    Item item1, item2;

    @BeforeEach
    public void setup() {
        item1 = TestData.getItemOne();
        item2 = TestData.getItemTwo();
    }

    class MockMarket implements Market {
        String location;
        @Override
        public void startStream(String location) {
            this.location = location;
        }

        @Override
        public ListResponse closeStream() {
            return null;
        }

        @Override
        public ListResponse list(Item item) {
            return null;
        }
    }

    @Test
    public void testNothing() {
        // empty for now
    }
}