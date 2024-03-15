package com.haonguyen.walletwise.repository;

import com.haonguyen.walletwise.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findById(Long id);

//    @Query("SELECT t.category.id, SUM(t.amount) " +
//            "FROM _transaction t " +
//            "WHERE (:startDate IS NULL OR t.date >= :startDate) " +
//            "AND (:endDate IS NULL OR t.date <= :endDate) " +
//            "GROUP BY t.category.id")
//    List<Object[]> getTotalAmountByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
