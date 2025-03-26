package com.darwinsys.eselling.base;

import com.darwinsys.eselling.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.darwinsys.security.DigestUtils.md5;
import static java.util.Objects.hash;

@ApplicationScoped
public class LoginService {

    @Inject
    EntityManager em;

    public boolean verify(String username, String passwd) {
        List<User> ret =
                em.createQuery("from User where name = ? and password = ?")
                        .setParameter(1, username)
                        .setParameter(2, hash(passwd))
                        .getResultList();
        return 1 == ret.size();
    }

    @Transactional
    public void createUser(String username, String password) {
        User user = new User(username, hash(password));
        em.persist(user);
    }

    @Transactional
    public void updateItem(User user) {
        user = user.withPassword(user.passwordHash());
        user = em.merge(user);
        em.persist(user);
    }

    public static String hash(String clear) {
        return md5(clear);
    }
}
