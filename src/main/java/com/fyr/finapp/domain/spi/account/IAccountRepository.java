package com.fyr.finapp.domain.spi.account;

import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.util.List;

public interface IAccountRepository {
    void save(Account account);
    List<Account> findByUserId(UserId userId);
    boolean existsByUserIdAndName(UserId userId, AccountName name);
}
