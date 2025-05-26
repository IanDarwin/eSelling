package com.darwinsys.eselling.base;

import com.darwinsys.eselling.listing.FBMarket;
import com.darwinsys.eselling.listing.ListResponse;
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

import com.darwinsys.eselling.model.Condition;

import static com.darwinsys.eselling.model.Constants.sellSites;

@Route(value = "")
@PageTitle("E-Selling")
@SuppressWarnings("unused") // It really is used!
public class ESalesView extends VerticalLayout {

    @Inject ItemService itemService;
    @Inject LoginService loginService;
    @Inject FBMarket fbMarket;
    private boolean isLoggedIn = false;
    private String loggedInUser = "";
    private final Grid<Item> grid = new Grid<>(Item.class);
    private final FormLayout form = new FormLayout();
    private final TextField nameField = new TextField("Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField askPriceField = new NumberField("Asking Price");
    private final ComboBox<Condition> conditionComboBox = new ComboBox<>("Condition:");
    private final ComboBox<Category> categoryComboBox = new ComboBox<>("Category");
    Checkbox active = new Checkbox("Active?");
    private Item selectedItem;
    private List<Item> items;
    private List<TextField> urlFields;

    public ESalesView() {

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
            final Set<Item> selectedItems = grid.getSelectedItems();
            if (selectedItems.isEmpty()) {
                 showMessageDialog("Correct and resubmit", "No items selected!");
                 return;
            }
            final ListResponse resp = fbMarket.list(selectedItems);
            var sb = new StringBuffer(
                String.format("Saved %d Items with %d warnings",
                Integer.valueOf(resp.successCount()), resp.warnings().size()));
            for (String s : resp.warnings()) {
                sb.append("; ").append(s);
            }
            if (resp.successCount() > 0) {
                sb.append(". Now upload ").append(resp.location())
                        .append(" to ")
                        .append("to https://www.facebook.com/marketplace/create/bulk");
            }
            showMessageDialog("Saved",  sb.toString());
        });
        var bottomRow = new HorizontalLayout();
        bottomRow.add(addButton, listFBButton);
        add(header, grid, bottomRow); // Add the button
        setSizeFull();
        grid.setSizeFull();

        urlFields = new ArrayList<>();
        for (var s : sellSites) {
            urlFields.add(new TextField(s + " URL"));
        }

        conditionComboBox.setItems(Condition.values());
        conditionComboBox.setValue(Condition.USED);

        categoryComboBox.setItems(Category.values()); // No default value

        items = getItems();
        grid.setItems(items); // Use the stored items list
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setColumns("name", "listed", "description", "condition", "askingPrice");
        grid.getColumnByKey("name").setHeader("Item Name");
        grid.getColumnByKey("askingPrice").setHeader("Asking Price");
        grid.getColumnByKey("listed").setHeader("Listed(any)?");

        grid.addItemDoubleClickListener(event -> { // Add double click listener
            selectedItem = event.getItem();
            showItemDialog(); // Re-use the add item dialog
        });
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
            items = getItems();
            grid.setItems(items);
            dialog.close();
            selectedItem = null;
        });

        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
            selectedItem = null;
        });

        formLayout.add(nameField, descriptionField);
        for (TextField tf : urlFields) {
            formLayout.add(tf);
        }

        formLayout.add(askPriceField, conditionComboBox, categoryComboBox);
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
            return;
        }
        if (category == null) {
            Notification.show("A Category is required");
            return;
        }
        if (description == null) {
            Notification.show("A description is required");
            return;
        }
        if (askPrice == null) {
            Notification.show("Asking price is required");
            return;
        }
        List<String> urls = new ArrayList<>();
        for (var tf : urlFields) {
            urls.add(tf.getValue());
        }

        if (selectedItem != null) { // Update existing item
            selectedItem.setName(name);
            selectedItem.setDescription(description);
            selectedItem.setUrls(urls);
            selectedItem.setCondition(conditionComboBox.getValue());
            selectedItem.setCategory(categoryComboBox.getValue());
            selectedItem.setAskingPrice(askPrice);
            itemService.updateItem(selectedItem);
        } else { // Create a new item
            Item newItem = new Item(0L, name, description, urls,
                    askPrice);
            newItem.setCondition(Condition.getDefault());
            items.add(newItem);
            grid.setItems(items);
            itemService.createItem(newItem);// Refresh the grid
        }
    }

    private List<Item> getItems() {
        return itemService.getItems();
    }
}
