package com.bookshop.repository;

import com.bookshop.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByUserUsername(String userId);
    List<WishList> findAllByUserUsername(String userId);
}