package com.fyr.finapp.domain.spi.category;

import com.fyr.finapp.domain.model.category.Category;
import com.fyr.finapp.domain.model.category.vo.CategoryId;
import com.fyr.finapp.domain.model.category.vo.CategoryName;
import com.fyr.finapp.domain.model.user.vo.UserId;
import com.fyr.finapp.domain.shared.vo.TransactionType;

import java.util.List;
import java.util.Optional;

public interface ICategoryRepository {
    void save(Category category);
    void saveAll(Iterable<Category> categories);
    List<Category> findAllByUserId(UserId userId);
    List<Category> findAllDeletedByUserId(UserId userId);
    Optional<Category> findById(CategoryId categoryId);
    boolean existsByUserIdAndTypeAndName(UserId userId, TransactionType type, CategoryName name);
}
