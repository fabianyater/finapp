package com.fyr.finapp.domain.model.user;

import com.fyr.finapp.domain.model.user.vo.*;

import java.time.OffsetDateTime;

public class User {
    private UserId id;
    private PersonName name;
    private PersonName surname;
    private Username username;
    private Email email;
    private PasswordHash passwordHash;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User() {
    }

    public User(UserId id, PersonName name, PersonName surname, Username username, Email email, PasswordHash passwordHash, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
    }

    public PersonName getName() {
        return name;
    }

    public void setName(PersonName name) {
        this.name = name;
    }

    public PersonName getSurname() {
        return surname;
    }

    public void setSurname(PersonName surname) {
        this.surname = surname;
    }

    public Username getUsername() {
        return username;
    }

    public void setUsername(Username username) {
        this.username = username;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(PasswordHash passwordHash) {
        this.passwordHash = passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
