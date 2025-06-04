package com.darwinsys.eselling.listing;

import com.darwinsys.eselling.model.Item;
import com.darwinsys.eselling.model.Condition;
import com.darwinsys.eselling.model.Category;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Class to list Item objects on eBay.
 * @author Initial version by Google Gemini
 */
public class EBayMarket implements Market {

    private final String EBAY_API_BASE_URL = "https://api.ebay.com/sell/inventory/v1"; // Or production URL
    private final RestTemplate restTemplate;
    private final String accessToken; //  eBay OAuth access token

    public EBayMarket(String accessToken) {
        this.restTemplate = new RestTemplate();
        this.accessToken = accessToken;
    }


    @Override
    public void startStream(String location) {

    }

    @Override
    public ListResponse closeStream() {
        return null;
    }

    /**
     * Creates a listing for an item on eBay using the Inventory API.
     * This example focuses on creating an "offer" and then "publishing" it.
     * The Inventory API requires you to first create a inventory item, then an offer,
     * and then publish the offer.
     *
     * @param item The Item object to list.
     * @return The ID of the created eBay listing (offer ID), or null if creation failed.
     */
    public ListResponse list(Item item) {
        try {
            String inventoryItemId;

            // 1. Create/Update Inventory Item (Product)
            if (item.getEbaySku() != null && !item.getEbaySku().isEmpty()) {
                // If an eBay SKU already exists, attempt to update the existing inventory item
                inventoryItemId = item.getEbaySku();
                if (!updateEbayInventoryItem(item, inventoryItemId)) {
                    System.err.println("Failed to update eBay inventory item for SKU: " + inventoryItemId);
                    return null;
                }
            } else {
                // Otherwise, create a new inventory item
                inventoryItemId = createEbayInventoryItem(item);
                if (inventoryItemId == null) {
                    System.err.println("Failed to create eBay inventory item for item: " + item.getName());
                    return null;
                }
                item.setEbaySku(inventoryItemId); // Store the newly created SKU
            }

            // 2. Create or Update Offer
            String offerId;
            if (item.getEbayOfferId() != null && !item.getEbayOfferId().isEmpty()) {
                // If an eBay Offer ID exists, attempt to update the existing offer
                offerId = item.getEbayOfferId();
                if (!updateEbayOffer(item, inventoryItemId, offerId)) {
                    System.err.println("Failed to update eBay offer for offer ID: " + offerId);
                    return null;
                }
            } else {
                // Otherwise, create a new offer
                offerId = createEbayOffer(item, inventoryItemId);
                if (offerId == null) {
                    System.err.println("Failed to create eBay offer for item: " + item.getName());
                    return null;
                }
                item.setEbayOfferId(offerId); // Store the newly created Offer ID
            }


            // 3. Publish Offer
            String listingId = publishEbayOffer(offerId);
            if (listingId != null) {
                item.setEbayListingId(listingId); // Store the final eBay Listing ID
                System.out.println("Successfully listed item '" + item.getName() + "' on eBay with listing ID: " + listingId);
                return new ListResponse(item.getEbayListingId(), 1, List.of()); // Return the updated item
            } else {
                System.err.println("Failed to publish eBay offer for item: " + item.getName());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error listing item on eBay: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ListResponse list(Collection collection) {
        return Market.super.list(collection);
    }

    /**
     * Creates an inventory item (product) in eBay's inventory system.
     *
     * @param item The Item object.
     * @return The SKU (inventory item ID) if successful, null otherwise.
     */
    private String createEbayInventoryItem(Item item) {
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode itemNode = mapper.createObjectNode();
        itemNode.put("productFamily", "STANDARD"); // Or "MULTIVARIATION"
        ObjectNode product = itemNode.putObject("product");
        product.put("title", item.getName());
        product.put("description", item.getDescription());
        product.put("aspects", mapper.createObjectNode().put("Brand", "Generic")); // Example aspect

        ArrayNode imageUrls = product.putArray("imageUrls");
        if (item.getPhotos() != null) {
            item.getPhotos().forEach(imageUrls::add);
        }

        String ebayCondition = mapConditionToEbay(item.getCondition());
        if (ebayCondition != null) {
            product.put("condition", ebayCondition);
        }
        if (item.getConditionQualification() != null && !item.getConditionQualification().isEmpty()) {
            product.put("conditionDescription", item.getConditionQualification());
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(itemNode.toString(), headers);
        // Generate a unique SKU based on your internal Item ID or other unique identifier
        // This SKU needs to be unique across your eBay inventory.
        String sku = "SKU_" + item.getId() + "_" + System.currentTimeMillis();

        try {
            String url = EBAY_API_BASE_URL + "/inventory_item/" + sku;
            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
            System.out.println("Successfully created eBay inventory item with SKU: " + sku);
            return sku;
        } catch (Exception e) {
            System.err.println("Error creating eBay inventory item: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates an existing inventory item (product) in eBay's inventory system.
     *
     * @param item The Item object with updated details.
     * @param sku The existing eBay SKU for the item.
     * @return True if update was successful, false otherwise.
     */
    private boolean updateEbayInventoryItem(Item item, String sku) {
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode itemNode = mapper.createObjectNode();
        itemNode.put("productFamily", "STANDARD");
        ObjectNode product = itemNode.putObject("product");
        product.put("title", item.getName());
        product.put("description", item.getDescription());
        product.put("aspects", mapper.createObjectNode().put("Brand", "Generic"));

        ArrayNode imageUrls = product.putArray("imageUrls");
        if (item.getPhotos() != null) {
            item.getPhotos().forEach(imageUrls::add);
        }

        String ebayCondition = mapConditionToEbay(item.getCondition());
        if (ebayCondition != null) {
            product.put("condition", ebayCondition);
        }
        if (item.getConditionQualification() != null && !item.getConditionQualification().isEmpty()) {
            product.put("conditionDescription", item.getConditionQualification());
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(itemNode.toString(), headers);

        try {
            String url = EBAY_API_BASE_URL + "/inventory_item/" + sku;
            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
            System.out.println("Successfully updated eBay inventory item with SKU: " + sku);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating eBay inventory item: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates an offer for an inventory item on eBay.
     *
     * @param item The Item object.
     * @param inventoryItemId The SKU of the inventory item to create an offer for.
     * @return The offer ID if successful, null otherwise.
     */
    private String createEbayOffer(Item item, String inventoryItemId) {
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode offerNode = mapper.createObjectNode();
        offerNode.put("sku", inventoryItemId);
        offerNode.put("marketplaceId", "EBAY_CA"); // Or EBAY_US, EBAY_DE, etc.
        offerNode.put("format", "FIXED_PRICE"); // Or "AUCTION"
        offerNode.put("availableQuantity", item.getQuantity());

        ObjectNode listingPolicies = offerNode.putObject("listingPolicies");
        listingPolicies.put("fulfillmentPolicyId", "YOUR_FULFILLMENT_POLICY_ID");
        listingPolicies.put("paymentPolicyId", "YOUR_PAYMENT_POLICY_ID");
        listingPolicies.put("returnPolicyId", "YOUR_RETURN_POLICY_ID");

        ObjectNode pricing = offerNode.putObject("pricingSummary");
        ObjectNode price = pricing.putObject("price");
        price.put("value", item.getAskingPrice());
        price.put("currency", "CAD");

        String ebayCategoryId = mapCategoryToEbayId(item.getCategory());
        if (ebayCategoryId != null) {
            offerNode.put("categoryId", ebayCategoryId);
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(offerNode.toString(), headers);

        try {
            String url = EBAY_API_BASE_URL + "/offer";
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode responseBody = response.getBody();
                if (responseBody != null && responseBody.has("offerId")) {
                    System.out.println("Successfully created eBay offer: " + responseBody.get("offerId").asText());
                    return responseBody.get("offerId").asText();
                }
            }
            System.err.println("Failed to create eBay offer. Status: " + response.getStatusCode() + ", Body: " + response.getBody());
            return null;
        } catch (Exception e) {
            System.err.println("Error creating eBay offer: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates an existing offer for an inventory item on eBay.
     *
     * @param item The Item object with updated details.
     * @param inventoryItemId The SKU of the inventory item associated with the offer.
     * @param offerId The existing eBay Offer ID to update.
     * @return True if update was successful, false otherwise.
     */
    private boolean updateEbayOffer(Item item, String inventoryItemId, String offerId) {
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode offerNode = mapper.createObjectNode();
        offerNode.put("sku", inventoryItemId);
        offerNode.put("marketplaceId", "EBAY_CA");
        offerNode.put("format", "FIXED_PRICE");
        offerNode.put("availableQuantity", item.getQuantity());

        ObjectNode listingPolicies = offerNode.putObject("listingPolicies");
        listingPolicies.put("fulfillmentPolicyId", "YOUR_FULFILLMENT_POLICY_ID");
        listingPolicies.put("paymentPolicyId", "YOUR_PAYMENT_POLICY_ID");
        listingPolicies.put("returnPolicyId", "YOUR_RETURN_POLICY_ID");

        ObjectNode pricing = offerNode.putObject("pricingSummary");
        ObjectNode price = pricing.putObject("price");
        price.put("value", item.getAskingPrice());
        price.put("currency", "CAD");

        String ebayCategoryId = mapCategoryToEbayId(item.getCategory());
        if (ebayCategoryId != null) {
            offerNode.put("categoryId", ebayCategoryId);
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(offerNode.toString(), headers);

        try {
            String url = EBAY_API_BASE_URL + "/offer/" + offerId;
            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class); // Use PUT for updating
            System.out.println("Successfully updated eBay offer with ID: " + offerId);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating eBay offer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Publishes an offer to make it live on eBay.
     *
     * @param offerId The ID of the offer to publish.
     * @return The final eBay Listing ID if successful, null otherwise.
     */
    private String publishEbayOffer(String offerId) {
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers); // No body needed for publish

        try {
            String url = EBAY_API_BASE_URL + "/offer/" + offerId + "/publish";
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseBody = response.getBody();
                if (responseBody != null && responseBody.has("listingId")) {
                    System.out.println("Successfully published eBay offer. Listing ID: " + responseBody.get("listingId").asText());
                    return responseBody.get("listingId").asText();
                }
            }
            System.err.println("Failed to publish eBay offer. Status: " + response.getStatusCode() + ", Body: " + response.getBody());
            return null;
        } catch (Exception e) {
            System.err.println("Error publishing eBay offer: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper to create HTTP headers with the Authorization token.
     *
     * @return HttpHeaders with Authorization.
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/json");
        headers.set("Content-Language", "en-CA"); // Set appropriate content language
        return headers;
    }

    /**
     * Maps the internal Condition enum to eBay's condition string.
     * XXX need a more comprehensive mapping based on eBay's documentation.
     */
    private String mapConditionToEbay(Condition condition) {
        Objects.requireNonNull(condition, "Condition may not be null");
        switch (condition) {
            case NEW: return "NEW_WITH_TAGS"; // Or "NEW" depending on exact eBay condition
            case USED: return "USED";
            // Add more mappings as per eBay's condition ID list
            default: return null;
        }
    }

    /**
     * Maps the internal Category enum to an eBay category ID.
     * This is a critical part of eBay listing and requires careful mapping.
     * You'll likely need to use eBay's Taxonomy API to find appropriate category IDs.
     */
    private String mapCategoryToEbayId(Category category) {
        Objects.requireNonNull(category, "Category may not be null");
        // This is a simplified mapping. In a real application, you'd have a more robust
        // way to get eBay category IDs (e.g., from a database, configuration, or Taxonomy API).
        switch (category) {
            case ComputersElectronics: return "15032";
            case Books: return "267";
            case Household: return "11700";
            default: return null;
        }
    }

    // XXX may want methods for:
    // - Retrieving offers
    // - Ending offers
    // - Updating offers
    // - Managing inventory items (getting, deleting)
    // Although initially these will be low-volumen enough
    // that they can be done through the web ui.

    // Quickie main - make into a test when further along.
    public static void main(String[] args) {
        var lister = new EBayMarket("test_access_token");
        var item = new Item();
        item.setName("Something for sale");
        item.setDescription("""
                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""");
        item.setAskingPrice(42.5d);
        item.setCondition(Condition.USED);
        item.setConditionQualification("Only used by a little old gorilla on Tuesdays at noon");
        item.setCategory(Category.Antiques);
        lister.list(item);
    }
}
