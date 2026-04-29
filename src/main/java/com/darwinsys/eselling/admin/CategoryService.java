package com.darwinsys.eselling.admin;

import com.darwinsys.eselling.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class CategoryService {
    // ── Categories ────────────────────────────────────────────────────────────

    private static final String PU = "eselling";

    private final EntityManagerFactory emf;

    public CategoryService() {
        emf = Persistence.createEntityManagerFactory(PU);
        var em = emf.createEntityManager();
        if (em.createQuery("FROM Category").getMaxResults() == 0) {
            em.getTransaction().begin();
            em.persist(new Category("TEST CAT", "FB CAT", 0, 0));
            em.getTransaction().commit();
        }
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
