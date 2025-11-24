package com.darwinsys.eselling.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Collection;
import java.util.Objects;

@Entity
public class Category {
    @Id
    private String name;
    private String fbCategory;
    private int eBayCategory;
    private int kijijiCategory;
    @OneToMany
    Collection<Item> items;

    public Category(String name, String fbCategory, int eBayCategory, int kijijiCategory) {
        this.name = name;
        this.fbCategory = fbCategory;
        this.eBayCategory = eBayCategory;
        this.kijijiCategory = kijijiCategory;
    }

    public Category() {
        // Empty, just for JPA
    }

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }

    public String fbCategory() {
        return fbCategory;
    }

    public int eBayCategory() {
        return eBayCategory;
    }

    public int kijijiCategory() {
        return kijijiCategory;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Category) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.fbCategory, that.fbCategory) &&
                this.eBayCategory == that.eBayCategory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fbCategory, eBayCategory);
    }

}

