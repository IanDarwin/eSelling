package com.darwinsys.eselling.admin;

import com.darwinsys.eselling.listing.MarketName;
import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Condition;
import com.darwinsys.eselling.model.Item;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Modal dialog for creating a new Item or editing an existing one.
 */
public class ItemEditDialog extends JDialog {

    private boolean confirmed = false;

    // Core fields
    private final JTextField     nameField        = new JTextField(30);
    private final JTextArea      descArea         = new JTextArea(5, 30);
    private final JComboBox<Condition>  conditionBox     = new JComboBox<>(Condition.values());
    private final JTextField     qualField        = new JTextField(20);
    private final JTextField     priceField       = new JTextField(10);
    private final JSpinner       qtySpinner       = new JSpinner(new SpinnerNumberModel(1, 0, 99999, 1));
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JCheckBox      activeCheck      = new JCheckBox("Active", true);
    private final JTextField     tagsField        = new JTextField(30);
    private final JTextField     photosDirField   = new JTextField(30);

    // One URL field per market
    private final JTextField[]   urlFields        = new JTextField[MarketName.values().length];

    public ItemEditDialog(Frame owner, String title,
                          Item item, List<Category> categories) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Populate category combo
        for (Category c : categories) categoryBox.addItem(c);

        // Build tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("General",     buildGeneralTab());
        tabs.addTab("Description", buildDescriptionTab());
        tabs.addTab("Market URLs", buildUrlsTab());

        // Buttons
        JButton okBtn     = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        okBtn    .addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(okBtn);
        buttons.add(cancelBtn);

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(tabs,    BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        // Populate from existing item (edit mode)
        if (item != null) populateFrom(item);

        pack();
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(owner);
    }

    // ── Tab builders ──────────────────────────────────────────────────────────

    private JPanel buildGeneralTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints lc = lc(), vc = vc();

        int row = 0;
        addRow(p, row++, lc, vc, "Name *:",           nameField);
        addRow(p, row++, lc, vc, "Condition:",         conditionBox);
        addRow(p, row++, lc, vc, "Condition detail:",  qualField);
        addRow(p, row++, lc, vc, "Asking price ($) *:", priceField);
        addRow(p, row++, lc, vc, "Quantity:",          qtySpinner);
        addRow(p, row++, lc, vc, "Category:",          categoryBox);
        addRow(p, row++, lc, vc, "Tags (comma sep.):", tagsField);
        addRow(p, row++, lc, vc, "Photos directory:",  photosDirField);

        lc.gridy = row; vc.gridy = row;
        p.add(new JLabel(""), lc);
        p.add(activeCheck, vc);

        return p;
    }

    private JPanel buildDescriptionTab() {
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(descArea);
        scroll.setBorder(new TitledBorder("Description"));

        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildUrlsTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints lc = lc(), vc = vc();

        MarketName[] markets = MarketName.values();
        for (int i = 0; i < markets.length; i++) {
            urlFields[i] = new JTextField(30);
            lc.gridy = i; vc.gridy = i;
            p.add(new JLabel(markets[i].name() + " URL:"), lc);
            p.add(urlFields[i], vc);
        }
        return p;
    }

    // ── Data binding ──────────────────────────────────────────────────────────

    private void populateFrom(Item item) {
        nameField      .setText(item.getName());
        descArea       .setText(item.getDescription());
        priceField     .setText(item.getAskingPrice() != null
                                ? String.valueOf(item.getAskingPrice()) : "0");
        qtySpinner     .setValue(item.getQuantity());
        activeCheck    .setSelected(Boolean.TRUE.equals(item.getActive()));
        tagsField      .setText(item.getTags() != null ? item.getTags() : "");
        photosDirField .setText(item.getPhotosDir() != null ? item.getPhotosDir() : "");
        qualField      .setText(item.getConditionQualification() != null
                                ? item.getConditionQualification() : "");

        if (item.getCondition() != null) conditionBox.setSelectedItem(item.getCondition());

        if (item.getCategory() != null) {
            for (int i = 0; i < categoryBox.getItemCount(); i++) {
                if (categoryBox.getItemAt(i).getId().equals(item.getCategory().getId())) {
                    categoryBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        MarketName[] markets = MarketName.values();
        for (int i = 0; i < markets.length && i < urlFields.length; i++) {
            try { urlFields[i].setText(item.getUrl(markets[i])); }
            catch (Exception ignored) { urlFields[i].setText(""); }
        }
    }

    private void onSave() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a number.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        confirmed = true;
        dispose();
    }

    /** Returns true if the user pressed Save. */
    public boolean isConfirmed() { return confirmed; }

    /**
     * Applies the dialog's field values onto an existing (or new) Item.
     * Call only when {@link #isConfirmed()} is true.
     */
    public void applyTo(Item item) {
        item.setName(nameField.getText().trim());
        item.setDescription(descArea.getText().trim());
        item.setCondition((Condition) conditionBox.getSelectedItem());
        item.setConditionQualification(qualField.getText().trim());
        item.setAskingPrice(Double.parseDouble(priceField.getText().trim()));
        item.setQuantity((Integer) qtySpinner.getValue());
        item.setCategory((Category) categoryBox.getSelectedItem());
        item.setActive(activeCheck.isSelected());
        item.setTags(tagsField.getText().trim());
        item.setPhotosDir(photosDirField.getText().trim());

//        MarketName[] markets = MarketName.values();
//        for (int i = 0; i < markets.length && i < urlFields.length; i++) {
//            item.setUrl(markets[i], urlFields[i].getText().trim());
//        }
    }

    // ── GBC helpers ───────────────────────────────────────────────────────────

    private void addRow(JPanel p, int row,
                        GridBagConstraints lc, GridBagConstraints vc,
                        String label, JComponent comp) {
        lc.gridy = row; vc.gridy = row;
        p.add(new JLabel(label), lc);
        p.add(comp, vc);
    }

    private GridBagConstraints lc() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(4, 4, 4, 6);
        return c;
    }

    private GridBagConstraints vc() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0;
        c.insets = new Insets(4, 0, 4, 4);
        return c;
    }
}
