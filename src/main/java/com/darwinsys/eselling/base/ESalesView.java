package com.darwinsys.eselling.base;

import com.darwinsys.eselling.model.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
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

import static com.darwinsys.eselling.model.Constants.Condition;
import static com.darwinsys.eselling.model.Constants.sellSites;

@Route(value = "")
@PageTitle("E-Selling")
@SuppressWarnings("unused") // It really is used!
public class ESalesView extends VerticalLayout {

    @Inject ItemService service;
    private boolean isLoggedIn = false;
    private String loggedInUser = "";
    private final Grid<Item> grid = new Grid<>(Item.class);
    private final FormLayout form = new FormLayout();
    private final TextField nameField = new TextField("Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField askPriceField = new NumberField("Asking Price");
    private final NumberField soldPriceField = new NumberField("Sold Price");
    private final ComboBox<Condition> comboBox =
            new ComboBox<>("Condition:");
    private Item selectedItem;
    private List<Item> items;
    private List<TextField> tfs;

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
                username.equals("ian") &&
                passwordClear != null && !passwordClear.trim().isEmpty() &&
                passwordClear.equals("secret")) {
                isLoggedIn = true;
                loggedInUser = username;
                dialog.close();
                initializeView();
            } else {
				Notification.show("Please enter a username");
            }
        });

        var dialogLayout = new VerticalLayout();
        dialogLayout.add(usernameField, passwordField, loginButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void initializeView() {
        removeAll();
        H1 header = new H1("E-Sales Items - Logged in as: " + loggedInUser);
        Button addButton = new Button("Add Item");
        addButton.addClickListener(event1 -> showItemDialog());
        add(header, grid, addButton); // Add the button
        setSizeFull();
        grid.setSizeFull();

        tfs = new ArrayList<>();
        for (var s : sellSites) {
            tfs.add(new TextField(s + " URL"));
        }

        comboBox.setItems(Condition.values());
        comboBox.setValue(Condition.USED);

        items = getItems();
        grid.setItems(items); // Use the stored items list
        grid.setColumns("name", "description", "askingPrice", "soldPrice");
        grid.getColumnByKey("name").setHeader("Item Name");
        grid.getColumnByKey("askingPrice").setHeader("Ask Price");

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
            for (int i = 0; i < tfs.size(); i++) {
                tfs.get(i).setValue(selectedItem.getUrls().get(i));
            }
            comboBox.setValue(selectedItem.getCondition());
            askPriceField.setValue(selectedItem.getAskingPrice());
            soldPriceField.setValue(selectedItem.getSoldPrice());
        } else {
            // Clear fields from previous use if creating a new item
            nameField.clear();
            descriptionField.clear();
            for (var tf : tfs) {
                tf.clear();
            }
            comboBox.setValue(Condition.getDefault());
            askPriceField.clear();
            soldPriceField.clear();
        }
        Button saveButton = new Button("Save", event -> {
            saveItem();
            dialog.close();
            selectedItem = null;
        });

        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
            selectedItem = null;
        });

        formLayout.add(nameField, descriptionField);
        for (TextField tf : tfs) {
            formLayout.add(tf);
        }
        formLayout.add(comboBox, askPriceField, soldPriceField, saveButton, cancelButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private void saveItem() {
        String name = nameField.getValue();
        String description = descriptionField.getValue();
        Double askPrice = askPriceField.getValue();
        if (name == null) {
            Notification.show("Short name is required");
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
        Double soldPrice = soldPriceField.getValue();
        List<String> urls = new ArrayList<>();
        for (var tf : tfs) {
            urls.add(tf.getValue());
        }

        if (selectedItem != null) { // Update existing item
            selectedItem.setName(name);
            selectedItem.setDescription(description);
            selectedItem.setUrls(urls);
            selectedItem.setCondition(comboBox.getValue());
            selectedItem.setAskingPrice(askPrice);
            selectedItem.setSoldPrice(soldPrice);
            service.updateItem(selectedItem);
        } else { // Create a new item
            Item newItem = new Item(0L, name, description, urls,
                    askPrice, soldPrice);
            newItem.setCondition(Condition.getDefault());
            items.add(newItem);
            grid.setItems(items);
            service.createItem(newItem);// Refresh the grid
        }
    }

    private List<Item> getItems() {
        return service.getItems();
    }
}
