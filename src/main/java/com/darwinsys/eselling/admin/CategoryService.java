package com.darwinsys.eselling.admin;

import com.darwinsys.eselling.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

/// ── Categories support ─────
public class CategoryService {

    private static final String PU = "eselling";

    private final EntityManagerFactory emf;

    public CategoryService() {
        emf = Persistence.createEntityManagerFactory(PU);
        var em = emf.createEntityManager();
        final List categories = em.createQuery("FROM Category").getResultList();
        if (categories.size() == 0) {
            em.getTransaction().begin();
            em.persist(new Category("TEST CAT", "TEST FB CAT", 123, 456));
            em.getTransaction().commit();
            System.out.println("Created TEST CAT");
        } else {
            System.out.println("Found categories = " + categories);
        }
        em.close();
    }

    public List<Category> findAllCategories() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
