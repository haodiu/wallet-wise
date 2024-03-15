package com.haonguyen.walletwise.repository;

import com.haonguyen.walletwise.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);

    @Query("SELECT c FROM _category c JOIN _transaction t ON c.id = t.category.id WHERE c.name = :name ORDER BY :sortBy")
    Optional<Category> findByName(String name, String sortBy);
}
