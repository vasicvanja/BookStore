package com.bookshop.repository;

import com.bookshop.model.ShoppingCart;
import com.bookshop.model.enumerations.ShoppingCartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserUsernameAndStatus(String userId, ShoppingCartStatus status);

    boolean existsByUserUsernameAndStatus(String username, ShoppingCartStatus status);

    List<ShoppingCart> findAllByUserUsername(String username);

    List<ShoppingCart> findAllByUserUsernameAndStatus(String username, ShoppingCartStatus status);
}
