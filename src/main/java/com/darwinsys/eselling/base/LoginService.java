package com.darwinsys.eselling.base;

import com.darwinsys.eselling.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

import static com.darwinsys.security.DigestUtils.md5;

@ApplicationScoped
public class LoginService {

    @Inject
    EntityManager em;

    public boolean verify(String username, String passwdClear) {
        List<User> ret =
                em.createQuery("from User where name = ?1 and passwordHashed = ?2", User.class)
                        .setParameter(1, username)
                        .setParameter(2, hash(passwdClear))
                        .getResultList();
        if (ret.size() > 1) {
            throw new IllegalStateException("Multiple accounts with same name!");
        }
        return 1 == ret.size();
    }

    @Transactional
    public User createUser(String username, String password) {
        User user = new User(0, username, hash(password));
        em.persist(user);
        return user;
    }

    @Transactional
    public void updateUser(User user) {
        user = em.merge(user);
        em.persist(user);
    }

    public static String hash(String clear) {
        return md5(clear);
    }
}
