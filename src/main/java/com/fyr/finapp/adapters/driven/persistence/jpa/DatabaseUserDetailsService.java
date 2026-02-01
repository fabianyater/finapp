package com.fyr.finapp.adapters.driven.persistence.jpa;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.UserJpaRepository;
import com.fyr.finapp.adapters.driven.security.user.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserJpaRepository userJpaRepository;

    public DatabaseUserDetailsService(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userJpaRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var fullName = "%s %s".formatted(user.getName(), user.getSurname());

        return new SecurityUser(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                fullName,
                user.getPasswordHash()
        );
    }
}
