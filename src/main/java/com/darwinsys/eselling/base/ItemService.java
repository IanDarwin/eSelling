package com.darwinsys.eselling.base;

import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Item;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;

@SessionScoped @Default
@Named("itemService")
public class ItemService implements Serializable {

    @Inject
    EntityManager em;

    public List<Item> getAllItems() {
        return em.createQuery(
                "SELECT i from Item i WHERE active IS true order by name", Item.class).getResultList();
    }

    public List<Item> getItems() {
        return em.createQuery(
                // Want to add: AND urls != '{"","","","",""}' but that doesn't compute.
                "SELECT i from Item i WHERE active IS true  order by name", Item.class).getResultList();
    }

    public List<Item> getItems(Category category) {
        final TypedQuery<Item> query = em.createQuery(
                "SELECT i FROM Item i WHERE i.category = ?1 AND active IS true order by name", Item.class);
        query.setParameter(1, category);
        return query.getResultList();
    }

    @Transactional
    public void createItem(Item item) {
        em.persist(item);
    }

    @Transactional
    public void updateItem(Item item) {
        item = em.merge(item);
        em.persist(item);
    }
}
