package com.darwinsys.eselling.admin;

import com.darwinsys.eselling.model.Category;
import com.darwinsys.eselling.model.Item;
import jakarta.persistence.*;

import java.util.List;

/**
 * Thin service layer wrapping JPA operations for Item.
 * All public methods manage their own transactions.
 */
public class ItemService implements AutoCloseable {

    private static final String PU = "eselling";

    private final EntityManagerFactory emf;

    public ItemService() {
        emf = Persistence.createEntityManagerFactory(PU);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public List<Item> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT i FROM Item i ORDER BY i.id", Item.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Item findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Item.class, id);
        } finally {
            em.close();
        }
    }

    public Item save(Item item) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Re-attach category if needed
            if (item.getCategory() != null && item.getCategory().getId() != null) {
                Category managed = em.find(Category.class, item.getCategory().getId());
                item.setCategory(managed);
            }
            Item result = (item.getId() == null || item.getId() == 0)
                    ? em.merge(item)   // INSERT (id generated)
                    : em.merge(item);  // UPDATE
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Item item = em.find(Item.class, id);
            if (item != null) em.remove(item);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        if (emf != null && emf.isOpen()) emf.close();
    }
}
