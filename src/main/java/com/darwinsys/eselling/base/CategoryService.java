package com.darwinsys.eselling.base;

import com.darwinsys.eselling.model.Category;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;

@SessionScoped @Default
@Named("categoryService")
public class CategoryService implements Serializable {

    @Inject
    EntityManager em;

    public List<Category> getCategories() {
        return em.createQuery(
                "SELECT c from Category c order by name", Category.class).getResultList();
    }

    @Transactional
    public void createCategory(Category category) {
        em.persist(category);
    }

    @Transactional
    public void updateItem(Category category) {
        category = em.merge(category);
        em.persist(category);
    }
}
