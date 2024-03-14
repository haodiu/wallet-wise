package com.haonguyen.walletwise.repository;

import com.haonguyen.walletwise.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);
}
