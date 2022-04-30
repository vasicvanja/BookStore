package com.bookshop.web;

import com.bookshop.service.AuthService;
import com.bookshop.service.ShoppingCartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final AuthService authService;

    public ShoppingCartController(ShoppingCartService shoppingCartService,
                                  AuthService authService) {
        this.shoppingCartService = shoppingCartService;
        this.authService = authService;
    }


    @PostMapping("/{bookId}/new-book")
    public String addBookToShoppingCart(@PathVariable Long bookId) {
        try {
            String userId = this.authService.getCurrentUserId();
            shoppingCartService.addBookToShoppingCart(userId, bookId);
        } catch (RuntimeException ex) {
            return "redirect:/books?error=" + ex.getLocalizedMessage();
        }
        return "redirect:/books";
    }


    @PostMapping("/{bookId}/remove-book")
    public String removeBookFromShoppingCart(@PathVariable Long bookId) {
        this.shoppingCartService.removeBookFromShoppingCart(this.authService.getCurrentUserId(), bookId);
        return "redirect:/books";
    }
}
