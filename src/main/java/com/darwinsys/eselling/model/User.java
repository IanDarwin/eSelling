package com.darwinsys.eselling.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import static com.darwinsys.security.DigestUtils.md5;

@Entity
public record User(@Id int id, String name, String passwordHash) {
    public User withPassword(String s) {
        return new User(id, name, md5(s));
    }
}
