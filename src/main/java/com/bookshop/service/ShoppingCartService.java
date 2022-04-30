package com.bookshop.service;

import com.bookshop.model.ShoppingCart;
import com.bookshop.model.dto.ChargeRequest;
import com.bookshop.model.enumerations.ShoppingCartStatus;
import com.stripe.exception.StripeException;

import java.util.List;

public interface ShoppingCartService {

    ShoppingCart findActiveShoppingCartByUsername(String userId);

    List<ShoppingCart> findAllByUsername(String userId);

    ShoppingCart createNewShoppingCart(String userId);

    ShoppingCart addBookToShoppingCart(String userId,
                                       Long bookId);

    boolean existsByUserUsernameAndStatus(String username, ShoppingCartStatus status);

    ShoppingCart removeBookFromShoppingCart(String userId, Long bookId);

    ShoppingCart getActiveShoppingCart(String userId);

    ShoppingCart cancelActiveShoppingCart(String userId);

    ShoppingCart checkoutShoppingCart(String userId, ChargeRequest chargeRequest) throws StripeException;

    int countTransactionsByUsername(String username);

    void deleteShoppingCartsById(String userId);
}
