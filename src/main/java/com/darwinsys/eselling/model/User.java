package com.darwinsys.eselling.model;

import static com.darwinsys.security.DigestUtils.md5;

public record User(String name, String passwordHash) {
    public User withPassword(String s) {
        return new User(name, md5(s));
    }
}
