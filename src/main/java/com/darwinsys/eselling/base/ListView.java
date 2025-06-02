package com.darwinsys.eselling.base;

import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Item;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;

import java.util.List;

@Route(value = "")
@PageTitle("Ian's Yard Sale")
public class ESalesLists extends VerticalLayout {
    @Inject ItemService itemService;
    private final Grid<Item> grid = new Grid<>(Item.class);
    Button all = new Button("All?");

    public ESalesLists() {
        initializeView();
    }

    private void initializeView() {
        removeAll();
        H1 header = new H1("Ian's Yard Sale Items");
        var categories = Category.values();
        HorizontalLayout topBox = new HorizontalLayout();
        topBox.add(all);
        for (Category cat : categories) {
            Button b = new Button(cat.name());
            topBox.add(b);
        }
        add(topBox, grid);

        var items = getItems();
        grid.setItems(items); // Use the stored items list
        grid.setColumns("name", "description", "category", "condition", "category", "askingPrice");
        grid.getColumnByKey("name").setHeader("Item Name");
        grid.getColumnByKey("description").setHeader("Details");
        grid.getColumnByKey("askingPrice").setHeader("Asking Price");

        setSizeFull();
        grid.setSizeFull();
    }

    private List<Item> getItems() {
        return itemService.getItems();
    }
}
