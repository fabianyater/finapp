package com.fyr.finapp.adapters.driven.security.user;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SecurityUser implements UserDetails {
    @Getter
    private final UUID id;
    @Getter
    private final String email;
    @Getter
    private final String fullName;
    private final String username;
    private final String passwordHash;
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(UUID id, String email, String fullName, String username, String passwordHash) {
        this(id, email, username, fullName, passwordHash, List.of());
    }

    public SecurityUser(UUID id, String email, String fullName, String username, String passwordHash,
                        Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.authorities = authorities == null ? List.of() : authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
