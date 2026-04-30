package com.fyr.finapp.domain.spi.account;

import com.fyr.finapp.domain.model.account.Account;
import com.fyr.finapp.domain.model.account.vo.AccountId;
import com.fyr.finapp.domain.model.account.vo.AccountName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.pagination.PageRequest;
import com.fyr.finapp.domain.shared.pagination.SortDirection;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IAccountRepository {
    void save(Account account);

    PagedAccounts findByUserId(UserId userId, AccountFilters filters);

    List<Account> findAllByUserId(UserId userId);

    Optional<Account> findById(AccountId id);

    boolean existsByUserIdAndName(UserId userId, AccountName name);

    int unmarkAllAsDefault(UserId userId);

    void delete(AccountId id);

    void addMember(AccountId accountId, UserId userId, UserId invitedBy);

    void removeMember(AccountId accountId, UserId userId);

    List<MemberInfo> findMembers(AccountId accountId);

    boolean isMember(AccountId accountId, UserId userId);

    record MemberInfo(String userId, String email, String name, Instant joinedAt) {}

    record AccountFilters(
            PageRequest pageRequest,
            Set<String> types,
            String search,
            Instant createdAfter,
            Instant createdBefore
    ) {
        public AccountFilters {
            types = types == null ? Set.of() : types;
        }

        public int page() {
            return pageRequest.page();
        }

        public int size() {
            return pageRequest.size();
        }

        public String sortBy() {
            return pageRequest.sortBy();
        }

        public boolean isAscending() {
            return pageRequest.direction() == SortDirection.ASC;
        }
    }

    record PagedAccounts(
            List<Account> accounts,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious) {
    }
}
