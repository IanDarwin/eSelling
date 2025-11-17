package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Item;

import java.util.Collection;
import java.util.Set;

public interface Market<T> {
    /// Before calling list()
    void startStream(String location);


    ListResponse list(Item item);
    default ListResponse list(Collection<Item> items) {
        startStream("Unknown");
        ListResponse listResponse = new ListResponse();
        for (Item item : items) {
            var resp = list(item);
            listResponse.setSuccessCount(resp.getSuccessCount() + listResponse.getSuccessCount());
            listResponse.getMessages().addAll(resp.getMessages());
        }
        closeStream();
        return listResponse;
    }

    ///  After last list() call
    ListResponse closeStream();

    // A message to the user.
    String getPostMessage();
}
