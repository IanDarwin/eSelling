package com.darwinsys.eselling.base;

import com.darwinsys.eselling.listing.CategoriesParser;
import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Item;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;


@Route(value = "")
@PageTitle("Ian's Yard Sale")
@SuppressWarnings("unused") // It really is used!
public class ListView extends VerticalLayout {
    @Inject ItemService itemService;
    private final Grid<Item> grid = new Grid<>(Item.class);


    public ListView(@Named("itemService") ItemService itemService) {
        this.itemService = itemService;
        initializeView();
    }

    private void initializeView() {
        removeAll();
        H1 header = new H1("Ian's Yard Sale Items");
        var categories = CategoriesParser.getInstance().categories;
        HorizontalLayout topBox = new HorizontalLayout();
        topBox.setWrap(true);
        final Button all = new Button("All");
        all.addClickListener(e->{
            grid.setItems(itemService.getItems());
        });
        topBox.add(all);
        for (Category cat : categories) {
            Button b = new Button(cat.name());
            b.addClickListener(e->{
                grid.setItems(itemService.getItems(cat));
            });
            topBox.add(b);
        }
        add(header, topBox, grid);

        var items = itemService.getItems();
        grid.setItems(items); // Use the stored items list
        grid.setColumns("name", "description", "category", "condition", "askingPrice");
        grid.getColumnByKey("name").setHeader("Item Name");
        grid.getColumnByKey("description").setHeader("Details");
        grid.getColumnByKey("askingPrice").setHeader("Asking Price");

        setSizeFull();
        grid.setSizeFull();
    }
}
