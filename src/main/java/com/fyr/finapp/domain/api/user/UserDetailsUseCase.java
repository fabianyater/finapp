package com.fyr.finapp.domain.api.user;

import java.time.OffsetDateTime;

public interface UserDetailsUseCase {
    UserResult get();

    record UserResult(
            String userId,
            String name,
            String surname,
            String username,
            String email,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}
}
