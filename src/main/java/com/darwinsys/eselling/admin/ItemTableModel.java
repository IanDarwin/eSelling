package com.darwinsys.eselling.admin;

import com.darwinsys.eselling.model.Item;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing TableModel for displaying a list of Items.
 */
public class ItemTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "ID", "Name", "Condition", "Price", "Qty", "Category", "Active", "Listed"
    };

    private List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) {
        this.items = new ArrayList<>(items);
        fireTableDataChanged();
    }

    public Item getItemAt(int row) {
        return items.get(row);
    }

    @Override public int getRowCount()    { return items.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0 -> Long.class;
            case 3 -> Double.class;
            case 4 -> Integer.class;
            case 6, 7 -> Boolean.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int col) {
        Item item = items.get(row);
        return switch (col) {
            case 0 -> item.getId();
            case 1 -> item.getName();
            case 2 -> item.getCondition();
            case 3 -> item.getAskingPrice();
            case 4 -> item.getQuantity();
            case 5 -> item.getCategory() == null ? "NO CATEGORY" : item.getCategoryName();
            case 6 -> item.getActive();
            case 7 -> item.isListed();
            default -> null;
        };
    }
}
