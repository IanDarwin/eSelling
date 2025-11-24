package com.darwinsys.eselling.base;

import com.darwinsys.eselling.listing.*;
import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import jakarta.inject.Inject;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Predicate;

import com.darwinsys.eselling.model.Condition;

import static com.darwinsys.eselling.model.Constants.sellSites;

@Route(value = "/admin")
@PageTitle("E-Selling")
@SuppressWarnings("unused") // It really is used!
public class AdminView extends VerticalLayout {

    @Inject LoginService loginService;
    @Inject ItemService itemService;
    @Inject CategoryService categoryService;
    @Inject FBMarket fbMarket;
    @Inject EBayMarket eBayMarket;
    @Inject KijijiMarket kijijiMarket;
    private boolean isLoggedIn = false;
    private String loggedInUser = "";
    private final Grid<Item> grid = new Grid<>(Item.class);
    private final FormLayout form = new FormLayout();
    private final TextField nameField = new TextField("Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField askPriceField = new NumberField("Asking Price");
    private final TextField photosDirField = new TextField("Photos Dir");
    private final TextField tagsField = new TextField("Keyword Tags");
    private final ComboBox<Condition> conditionComboBox = new ComboBox<>("Condition:");
    private final ComboBox<Category> categoryComboBox = new ComboBox<>("Category");
    Checkbox active = new Checkbox("Active?");
    private Item selectedItem;
    private List<Item> items;
    private List<TextField> urlFields;

    public AdminView() {

        if (!isLoggedIn) {
            showLoginDialog();
        } else {
            initializeView();
        }
    }

    private void showLoginDialog() {
        Dialog dialog = new Dialog();
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Button loginButton = new Button("Login", event -> {
            String username = usernameField.getValue();
            String passwordClear = passwordField.getValue();
            if (username != null && !username.trim().isEmpty() &&
                passwordClear != null && !passwordClear.trim().isEmpty() &&
                loginService.verify(username, passwordClear)) {
                isLoggedIn = true;
                loggedInUser = username;
                dialog.close();
                initializeView();
            } else {
				Notification.show("Please enter a valid username and password");
            }
        });

        var dialogLayout = new VerticalLayout();
        dialogLayout.add(usernameField, passwordField, loginButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void initializeView() {
        removeAll();
        H1 header = new H1("E-Sales Items - Logged in as " + loggedInUser);
        Button addButton = new Button("Add Item");
        addButton.addClickListener(event1 -> showItemDialog());
        Button listFBButton = new Button("Export Selected to FB");
        listFBButton.addClickListener(event1 -> {
            showUploadResult(prepareAndList(fbMarket), fbMarket);
        });
        Button listEBayButton = new Button("Export Selected to eBay");
        listEBayButton.addClickListener(event1 -> {
            showUploadResult(prepareAndList(eBayMarket), eBayMarket);
        });
        Button listKijijiButton = new Button("Export Selected to Kijiji");
        listKijijiButton.addClickListener(event1 -> {
            showUploadResult(prepareAndList(kijijiMarket), kijijiMarket);
        });
        var bottomRow = new HorizontalLayout();
        bottomRow.add(addButton, listFBButton, listEBayButton, listKijijiButton);
        add(header, grid, bottomRow); // Add the button
        setSizeFull();
        grid.setSizeFull();

        urlFields = new ArrayList<>();
        for (var s : sellSites) {
            urlFields.add(new TextField(s + " URL"));
        }

        conditionComboBox.setItems(Condition.values());
        conditionComboBox.setValue(Condition.USED);

        categoryComboBox.setItems(categoryService.getCategories());

        items = itemService.getItems();
        grid.setItems(items); // Use the stored items list
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setColumns("name", "listed", "description", "category", "condition", "askingPrice");
        grid.getColumnByKey("name").setHeader("Item Name");
        grid.getColumnByKey("askingPrice").setHeader("Asking Price");
        grid.getColumnByKey("listed").setHeader("Listed(any)?");

        grid.addItemDoubleClickListener(event -> { // Add double click listener
            selectedItem = event.getItem();
            showItemDialog(); // Re-use the add item dialog
        });
    }

    /// Common code to show an upload result(s)
    private void showUploadResult(ListResponse resp, Market market) {
        if (resp.getSuccessCount() > 0) {
            resp.stringBuilder
                    .append(String.format(
                            "Exported %d items with %d messages.\n",
                            resp.getSuccessCount(), resp.getMessages().size()));
            for (String s : resp.getMessages()) {
                resp.stringBuilder.append("; ").append(s);
            }
            resp.stringBuilder
                    .append("Now upload ").append(market.getFileLocation())
                    .append(" to ")
                    .append(market.getUploadURL());
            showMessageDialog("Saved_for_upload", resp.stringBuilder.toString());
        }
    }

    private ListResponse prepareAndList(Market<?> market) {
        final Set<Item> selectedItems = grid.getSelectedItems();
        if (selectedItems.isEmpty()) {
            showMessageDialog("Correct and resubmit", "No items selected!");
            return new ListResponse();
        }
        return market.list(selectedItems);
    }

    private void showItemDialog() {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        // Populate fields if editing an existing item
        if (selectedItem != null) {
            nameField.setValue(selectedItem.getName());
            descriptionField.setValue(selectedItem.getDescription());
            for (int i = 0; i < urlFields.size(); i++) {
                String value = selectedItem.getUrls().get(i);
                urlFields.get(i).setValue(value);
            }
            conditionComboBox.setValue(selectedItem.getCondition());
            // System.out.printf("item %s category %s\n", selectedItem, selectedItem.getCategory());
            categoryComboBox.setValue(selectedItem.getCategory());
            askPriceField.setValue(selectedItem.getAskingPrice());
            active.setValue(selectedItem.getActive());
        } else {
            // Clear fields from previous use if creating a new item
            nameField.clear();
            descriptionField.clear();
            for (var tf : urlFields) {
                tf.setValue("");
            }
            conditionComboBox.setValue(Condition.getDefault());
            askPriceField.clear();
            active.setValue(true);
        }

        active.addClickListener(
                evt -> selectedItem.setActive(active.getValue()));

        Button saveButton = new Button("Save", event -> {
            saveItem();
            items = itemService.getItems();
            grid.setItems(items);
            dialog.close();
            selectedItem = null;
        });

        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
            selectedItem = null;
        });

		// Now add everything to the form, in order
        formLayout.add(nameField, descriptionField);
        formLayout.add(photosDirField, tagsField);
        formLayout.add(askPriceField, conditionComboBox, categoryComboBox);
        for (TextField tf : urlFields) {
            formLayout.add(tf);
        }
		formLayout.add(active);
        var bottomRow = new HorizontalLayout();
        bottomRow.add(saveButton, cancelButton);
        formLayout.add(bottomRow);
        dialog.add(formLayout);
        dialog.open();
    }

    void showMessageDialog(String title, String message) {
        var dialog = new Dialog();
        dialog.setHeaderTitle(title);
        dialog.add(message);
        Button OKButton = new Button("OK", e -> dialog.close());
        dialog.getFooter().add(OKButton);
        dialog.open();
    }

    private void saveItem() {
        String name = nameField.getValue();
        String description = descriptionField.getValue();
        Category category = categoryComboBox.getValue();
        Double askPrice = askPriceField.getValue();
        if (name == null) {
            Notification.show("Short name is required");
            // return;
        }
        if (category == null) {
            Notification.show("A Category is required");
            // return;
        }
        if (description == null) {
            Notification.show("A description is required");
            // return;
        }
        if (askPrice == null) {
            Notification.show("Asking price is required");
            // return;
        }

        if (selectedItem != null) { // Update existing item
            populateItemFromFields(selectedItem);
        } else { // Create a new item
            Item newItem = new Item();
            populateItemFromFields(newItem);
            items.add(newItem);
            grid.setItems(items);
        }
    }

    private void populateItemFromFields(Item item) {
        item.setName(nameField.getValue());
        item.setDescription(descriptionField.getValue());
        item.setPhotosDir(photosDirField.getValue());
        item.setTags(tagsField.getValue());
        List<String> urls = new ArrayList<>();
        for (var tf : urlFields) {
            urls.add(tf.getValue());
        }
        item.setUrls(urls);
        item.setCondition(conditionComboBox.getValue());
        item.setCategory(categoryComboBox.getValue());
        item.setAskingPrice(askPriceField.getValue());
        itemService.updateItem(item);
    }

    /** For use as a Binder validator in the URL fields */
    public static Predicate<String> urlFieldValidator =
            s -> s.isEmpty() || s.startsWith("https://") || s.startsWith("http://");
}
