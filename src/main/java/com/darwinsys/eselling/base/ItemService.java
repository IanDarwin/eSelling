package com.darwinsys.eselling.base;

import com.darwinsys.eselling.model.Item;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ItemService {

    @Inject
    EntityManager em;

    public List<Item> getItems() {
        return em.createQuery("from Item order by name", Item.class).getResultList();
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
