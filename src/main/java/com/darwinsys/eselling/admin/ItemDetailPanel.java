package com.darwinsys.eselling.admin;

import com.darwinsys.eselling.listing.FBMarket;
import com.darwinsys.eselling.listing.ListResponse;
import com.darwinsys.eselling.listing.Market;
import com.darwinsys.eselling.listing.MarketName;
import com.darwinsys.eselling.model.Item;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Set;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Read-only panel showing full details of the selected Item.
 */
public class ItemDetailPanel extends JPanel {

    private final JLabel idLabel        = new JLabel();
    private final JLabel nameLabel      = new JLabel();
    private final JLabel conditionLabel = new JLabel();
    private final JLabel priceLabel     = new JLabel();
    private final JLabel qtyLabel       = new JLabel();
    private final JLabel categoryLabel  = new JLabel();
    private final JLabel activeLabel    = new JLabel();
    private final JLabel tagsLabel      = new JLabel();
    private final JLabel photosDirLabel = new JLabel();
    private final JTextArea descArea    = new JTextArea(4, 40);
    private final JPanel urlsPanel      = new JPanel(new GridLayout(0, 2, 4, 2));
    private final ItemAdminFrame parent;

    public ItemDetailPanel(ItemAdminFrame parent) {
        this.parent = parent;
        setBorder(new TitledBorder("Item Detail"));
        setLayout(new BorderLayout(8, 8));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints lc = labelConstraints();
        GridBagConstraints vc = valueConstraints();

        int row = 0;
        addRow(fields, row++, lc, vc, "ID:",          idLabel);
        addRow(fields, row++, lc, vc, "Name:",        nameLabel);
        addRow(fields, row++, lc, vc, "Condition:",   conditionLabel);
        addRow(fields, row++, lc, vc, "Price:",       priceLabel);
        addRow(fields, row++, lc, vc, "Quantity:",    qtyLabel);
        addRow(fields, row++, lc, vc, "Category:",    categoryLabel);
        addRow(fields, row++, lc, vc, "Active:",      activeLabel);
        addRow(fields, row++, lc, vc, "Tags:",        tagsLabel);
        addRow(fields, row++, lc, vc, "Photos dir:",  photosDirLabel);

        // Description
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createTitledBorder("Description"));

		// Actions panel
		var listButtonsPanel = new JPanel();
        listButtonsPanel.setBorder(BorderFactory.createTitledBorder("List"));
        final JButton listOnAmazon = new JButton("Amazon");
        listOnAmazon.addActionListener(e -> showMessageDialog(null, "Amazon not supported"));
        listButtonsPanel.add(listOnAmazon);
        final JButton listOnEBay = new JButton("eBay");
        listButtonsPanel.add(listOnEBay);
        final JButton listOnFB = new JButton("Fakebook");
        listOnFB.addActionListener(e -> prepareAndList(new FBMarket()));
        listButtonsPanel.add(listOnFB);
        final JButton listOnKJ = new JButton("Kijiji");
        listButtonsPanel.add(listOnKJ);

        // URLs panel
        urlsPanel.setBorder(BorderFactory.createTitledBorder("Market URLs"));

		// Wrapper
		var wrapper = new JPanel();
		wrapper.add(listButtonsPanel);
		wrapper.add(urlsPanel);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.add(descScroll, BorderLayout.NORTH);
        bottom.add(wrapper,  BorderLayout.SOUTH);

        add(fields, BorderLayout.NORTH);
        add(bottom, BorderLayout.CENTER);

        clear();
    }

    private String prepareAndList(Market<?> market) {
        System.out.println("ItemDetailPanel.prepareAndList");
        final Item selectedItem = parent.getSelectedItem();
        if (selectedItem == null) {
            showMessageDialog(null, "Select an item first");
            return "";
        }
        return market.list(selectedItem).toString();
    }


    public void display(Item item) {
        if (item == null) { clear(); return; }

        idLabel       .setText(String.valueOf(item.getId()));
        nameLabel     .setText(item.getName());
        conditionLabel.setText(item.getCondition() != null ? item.getCondition().name() : "");
        priceLabel    .setText(String.format("$%.2f", item.getAskingPrice()));
        qtyLabel      .setText(String.valueOf(item.getQuantity()));
        categoryLabel .setText(item.getCategoryName());
        activeLabel   .setText(item.getActive() != null && item.getActive() ? "Yes" : "No");
        tagsLabel     .setText(item.getTags() != null ? item.getTags() : "");
        photosDirLabel.setText(item.getPhotosDir() != null ? item.getPhotosDir() : "");
        descArea      .setText(item.getDescription());
        descArea      .setCaretPosition(0);

        urlsPanel.removeAll();
        for (MarketName market : MarketName.values()) {
            String url = "";
            try { url = item.getUrl(market); } catch (Exception ignored) {}
            urlsPanel.add(new JLabel(market.name() + ":"));
            JLabel urlLabel = new JLabel(url.isEmpty() ? "(not listed)" : url);
            urlLabel.setForeground(url.isEmpty() ? Color.GRAY : Color.BLUE.darker());
            urlsPanel.add(urlLabel);
        }
        urlsPanel.revalidate();
        urlsPanel.repaint();
    }

    public void clear() {
        idLabel.setText(""); nameLabel.setText(""); conditionLabel.setText("");
        priceLabel.setText(""); qtyLabel.setText(""); categoryLabel.setText("");
        activeLabel.setText(""); tagsLabel.setText(""); photosDirLabel.setText("");
        descArea.setText("");
        urlsPanel.removeAll();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addRow(JPanel p, int row,
                        GridBagConstraints lc, GridBagConstraints vc,
                        String labelText, JComponent value) {
        lc.gridy = row; vc.gridy = row;
        p.add(new JLabel(labelText), lc);
        p.add(value, vc);
    }

    private GridBagConstraints labelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(2, 4, 2, 4);
        return c;
    }

    private GridBagConstraints valueConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0;
        c.insets = new Insets(2, 0, 2, 4);
        return c;
    }
}
