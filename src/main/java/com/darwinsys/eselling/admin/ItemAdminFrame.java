package com.darwinsys.eselling.admin;

import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Item;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Main frame for the ESelling Admin application.
 *
 * Layout:
 *   ┌─────────────────────────────────────────────┐
 *   │  Toolbar: [New] [Edit] [Delete] [Refresh]   │
 *   ├──────────────────┬──────────────────────────┤
 *   │  Item list table │  Item detail panel        │
 *   │  (top 50%)       │  (right side)             │
 *   └──────────────────┴──────────────────────────┘
 *   │  Status bar                                  │
 *   └─────────────────────────────────────────────┘
 */
public class ItemAdminFrame extends JFrame {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ItemTableModel  tableModel = new ItemTableModel();
    private final JTable          table      = new JTable(tableModel);
    private final ItemDetailPanel detailPanel = new ItemDetailPanel(this);
    private final JLabel          statusBar   = new JLabel(" Ready");

    public ItemAdminFrame(ItemService itemService, CategoryService categoryService) {
        super("ESelling Admin");
        this.itemService = itemService;
        this.categoryService = categoryService;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { onExit(); }
        });

        buildUI();
        loadItems();
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildToolbar(),  BorderLayout.NORTH);
        add(buildCenter(),   BorderLayout.CENTER);
        add(statusBar,       BorderLayout.SOUTH);

        // Row selection → populate detail panel
        table.getSelectionModel().addListSelectionListener(this::onSelectionChanged);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        // Double-click → edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(40);
        table.getColumnModel().getColumn(4).setPreferredWidth(30);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);
        table.getColumnModel().getColumn(7).setPreferredWidth(50);
    }

    private JToolBar buildToolbar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton newBtn     = new JButton("New");
        JButton editBtn    = new JButton("Edit");
        JButton deleteBtn  = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        newBtn    .addActionListener(e -> createNew());
        editBtn   .addActionListener(e -> editSelected());
        deleteBtn .addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadItems());

        bar.add(newBtn);
        bar.add(editBtn);
        bar.add(deleteBtn);
        bar.addSeparator();
        bar.add(refreshBtn);

        // Search field on the right
        bar.add(Box.createHorizontalGlue());
        bar.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate (DocumentEvent e) { applyFilter(searchField.getText()); }
            public void removeUpdate (DocumentEvent e) { applyFilter(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { applyFilter(searchField.getText()); }
        });
        bar.add(searchField);

        return bar;
    }

    private JSplitPane buildCenter() {
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Items"));

        JScrollPane detailScroll = new JScrollPane(detailPanel);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tableScroll, detailScroll);
        split.setResizeWeight(0.55);
        split.setDividerLocation(600);
        return split;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void loadItems() {
        statusBar.setText(" Loading…");
        SwingWorker<List<Item>, Void> worker = new SwingWorker<>() {
            @Override protected List<Item> doInBackground() {
                return itemService.findAll();
            }
            @Override protected void done() {
                try {
                    tableModel.setItems(get());
                    statusBar.setText(" " + tableModel.getRowCount() + " items loaded.");
                } catch (Exception ex) {
                    showError("Failed to load items", ex);
                }
            }
        };
        worker.execute();
    }

    private void createNew() {
        List<Category> cats = categoryService.findAllCategories();
        ItemEditDialog dialog = new ItemEditDialog(this, "New Item", null, cats);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Item item = new Item();
            dialog.applyTo(item);
            try {
                itemService.save(item);
                loadItems();
                status("Item created.");
            } catch (Exception ex) {
                showError("Failed to save item", ex);
            }
        }
    }

    private void editSelected() {
        Item item = getSelectedItem();
        if (item == null) { JOptionPane.showMessageDialog(this, "Select an item first."); return; }

        List<Category> cats = categoryService.findAllCategories();
        ItemEditDialog dialog = new ItemEditDialog(this, "Edit Item #" + item.getId(), item, cats);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            dialog.applyTo(item);
            try {
                itemService.save(item);
                loadItems();
                status("Item #" + item.getId() + " saved.");
            } catch (Exception ex) {
                showError("Failed to save item", ex);
            }
        }
    }

    private void deleteSelected() {
        Item item = getSelectedItem();
        if (item == null) { JOptionPane.showMessageDialog(this, "Select an item first."); return; }

        int resp = JOptionPane.showConfirmDialog(this,
                "Delete item '" + item.getName() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            try {
                itemService.delete(item.getId());
                detailPanel.clear();
                loadItems();
                status("Item deleted.");
            } catch (Exception ex) {
                showError("Failed to delete item", ex);
            }
        }
    }

    private void applyFilter(String text) {
        TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
        if (sorter == null) return;
        if (text == null || text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void onSelectionChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        Item item = getSelectedItem();
        detailPanel.display(item);
    }

    Item getSelectedItem() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        return tableModel.getItemAt(modelRow);
    }

    private void onExit() {
        itemService.close();
        dispose();
        System.exit(0);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void status(String msg) { statusBar.setText(" " + msg); }

    private void showError(String msg, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                msg + ":\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        statusBar.setText(" Error: " + ex.getMessage());
    }
}
