package com.fyr.finapp.adapters.driven.persistence.jpa.adapter;

import com.fyr.finapp.adapters.driven.persistence.jpa.entity.CategoryEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.entity.UserEntity;
import com.fyr.finapp.adapters.driven.persistence.jpa.mapper.ICategoryEntityMapper;
import com.fyr.finapp.adapters.driven.persistence.jpa.repository.CategoryJpaRepository;
import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.TransactionType;
import com.fyr.finapp.domain.spi.category.ICategoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class CategoryAdapter implements ICategoryRepository {
    private final CategoryJpaRepository repo;
    private final EntityManager entityManager;
    private final ICategoryEntityMapper mapper;

    @Override
    public void save(Category category) {
        UUID id = category.getId().value();
        Optional<CategoryEntity> existing = repo.findById(id);
        CategoryEntity entity;

        if (existing.isPresent()) {
            entity = existing.get();
            mapper.updateEntityFromDomain(category, entity);
        } else {
            entity = new CategoryEntity();
            entity.setId(id);
            mapper.updateEntityFromDomain(category, entity);
        }

        entity.setUser(entityManager.getReference(UserEntity.class, category.getUserId().value()));

        repo.save(entity);
    }

    @Override
    public void saveAll(Iterable<Category> categories) {
        List<CategoryEntity> entities = StreamSupport.stream(categories.spliterator(), false)
                .map(category -> {
                    CategoryEntity entity = mapper.toEntity(category);
                    entity.setId(category.getId().value());

                    entity.setUser(entityManager.getReference(UserEntity.class, category.getUserId().value()));
                    return entity;
                })
                .toList();

        repo.saveAll(entities);
    }

    @Override
    public List<Category> findAllByUserId(UserId userId) {
        return repo.findAllByUser_IdAndIsDeletedFalse(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Category> findById(CategoryId categoryId) {
        return repo.findById(categoryId.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndTypeAndName(UserId userId, TransactionType type, CategoryName name) {
        return repo.existsByUser_IdAndTypeAndNameAndIsDeletedFalse(userId.value(), type.name(), name.value());
    }
}
