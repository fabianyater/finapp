package com.fyr.finapp.domain.spi.auth;

import com.fyr.finapp.domain.model.user.vo.PasswordHash;

public interface IEncryptionRepository {
    PasswordHash encode(String password);
}
