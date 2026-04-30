package com.fyr.finapp.adapters.driven.persistence.jpa.specification;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.AccountMemberEntity;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AccountSpecifications {
    public static Specification<AccountEntity> withFilters(
            UUID userId,
            Set<String> types,
            String search,
            Instant createdAfter,
            Instant createdBefore
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Subquery<UUID> memberSub = query.subquery(UUID.class);
            Root<AccountMemberEntity> memberRoot = memberSub.from(AccountMemberEntity.class);
            memberSub.select(memberRoot.get("accountId"))
                    .where(cb.equal(memberRoot.get("userId"), userId));
            Predicate isOwner = cb.equal(root.get("user").get("id"), userId);
            Predicate isMember = root.get("id").in(memberSub);
            predicates.add(cb.or(isOwner, isMember));

            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("type").in(types));
            }

            if (search != null && !search.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + search.toLowerCase() + "%"
                ));
            }

            if (createdAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }

            if (createdBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
