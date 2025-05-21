package com.darwinsys.eselling.listing;

import java.util.ArrayList;
import java.util.List;

public record ListResponse(String location, int successCount, List<String> warnings) {
    /// Provide constructor only to ensure that warnings isn't null
    public ListResponse(String location, int successCount, List<String> warnings) {
        this.location = location;
        this.successCount = successCount;
        this.warnings = new ArrayList<String>();
    }
}
