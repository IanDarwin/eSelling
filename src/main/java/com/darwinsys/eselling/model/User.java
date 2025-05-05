package com.darwinsys.eselling.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static com.darwinsys.security.DigestUtils.md5;

@Entity
@Table(name="users") // "user" is a keyword in some DBs
public class User {
    @Id long id;
    String name;
    String passwordHashed;

    public User() {
        // Needed by JPA
    }

    public User(String name, String passwordHashed) {
        this(0L, name, passwordHashed);
    }

    public User(long id, String name, String passwordHashed) {
        this.id = id;
        this.name = name;
        this.passwordHashed = passwordHashed;
    }

    public User withClearPassword(String s) {
        return new User(id, name, md5(s));
    }
}
