package com.bookshop.service;

import com.bookshop.model.WishList;

import java.util.List;

public interface WishListService {
    WishList findActiveWishListByUsername(String userId);

    List<WishList> findAllByUsername(String userId);

    WishList createWishList(String userId);

    WishList addBookToWishList(String userId,
                               Long bookId);

    WishList removeBookFromWishList(String userId, Long bookId);

    WishList getActiveWishList(String userId);

    void deleteWishList(String userId);
}
