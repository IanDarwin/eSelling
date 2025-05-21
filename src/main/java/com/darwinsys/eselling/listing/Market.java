package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Item;

import java.util.Set;


public interface Market<T> {
    ListResponse list(Set<Item> items);
}
