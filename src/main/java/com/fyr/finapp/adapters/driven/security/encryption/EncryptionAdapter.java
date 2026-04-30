package com.fyr.finapp.adapters.driven.security.encryption;

import com.fyr.finapp.domain.model.user.vo.PasswordHash;
import com.fyr.finapp.domain.spi.auth.IEncryptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class EncryptionAdapter implements IEncryptionRepository {
    private final PasswordEncoder encoder;

    @Override
    public PasswordHash encode(String password) {
        return new PasswordHash(encoder.encode(password));
    }
}
