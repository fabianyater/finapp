package com.fyr.finapp.domain.model.account;

import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.account.vo.AccountType;
import com.fyr.finapp.domain.model.common.vo.Money;
import com.fyr.finapp.domain.model.common.vo.Currency;
import com.fyr.finapp.domain.model.common.vo.Color;
import com.fyr.finapp.domain.model.user.vo.UserId;

import java.time.OffsetDateTime;

public class Account {
    private AccountId id;
    private AccountName name;
    private AccountType type;
    private Money initialBalance;
    private Currency currency;
    private String icon;
    private Color color;
    private Boolean defaultAccount;
    private Boolean archived;
    private Boolean excludedFromTotal;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UserId user;
}
