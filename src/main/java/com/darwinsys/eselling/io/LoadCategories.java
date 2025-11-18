package com.darwinsys.eselling.io;

import com.darwinsys.eselling.model.Category;
import com.darwinsys.sql.ConnectionUtil;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoadCategories {
    public static void main(String[] args) throws SQLException {
        // process();
    }

    public static void process() throws SQLException {
        Connection conn = ConnectionUtil.getConnection("eselling");
        PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT into category(name, fbCategory, eBayCategory) values(?,?,?);");
        PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE category set name = ?, fbCategory = ?, eBayCategory = ? WHERE name = ?;");
        for (Category c : CategoriesParser.getInstance().categories) {
            populate(c, insertStmt);
            try {
                insertStmt.execute();
            } catch (SQLException ex) {
                // System.out.printf("Insert %s failed, probably already present\n", c.name());
                // ex.printStackTrace();
            }
            // Now we do an update. Redundant if we just inserted but harmless;
            // but allows for updating of any of the category fields.
            populate(c, updateStmt);
            updateStmt.setString(4, c.name());
            // And it should not fail - if it does, we terminate.
            updateStmt.executeUpdate();
            System.out.println(c.name() + " is done.");
        }
    }

    private static void populate(Category c, PreparedStatement statement) throws SQLException {
        statement.setString(1, c.name());
        statement.setString(2, c.fbCategory());
        statement.setInt(3, c.eBayCategory());
    }
}
