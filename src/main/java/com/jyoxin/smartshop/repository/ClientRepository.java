package com.jyoxin.smartshop.repository;

import com.jyoxin.smartshop.entity.Client;
import com.jyoxin.smartshop.entity.enums.Tier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    Optional<Client> findByUserId(Long userId);

    List<Client> findByLoyaltyTier(Tier tier);

    boolean existsByEmail(String email);

    Page<Client> findByLoyaltyTier(Tier tier, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.stats.totalOrders >= :minOrders")
    List<Client> findByMinimumOrders(@Param("minOrders") int minOrders);

    @Query("SELECT c FROM Client c WHERE c.stats.totalSpent >= :minSpent")
    List<Client> findByMinimumSpent(@Param("minSpent") java.math.BigDecimal minSpent);

    @Query("SELECT c FROM Client c ORDER BY c.stats.totalSpent DESC")
    List<Client> findTopClientsBySpending(Pageable pageable);
}