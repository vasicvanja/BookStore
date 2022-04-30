package com.bookshop.web;

import com.bookshop.model.Book;
import com.bookshop.model.WishList;
import com.bookshop.service.AuthService;
import com.bookshop.service.ShoppingCartService;
import com.bookshop.service.WishListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wish-list")
public class WishListController {
    private final WishListService wishListService;
    private final AuthService authService;
    private final ShoppingCartService shoppingCartService;


    public WishListController(WishListService wishListService, AuthService authService, ShoppingCartService shoppingCartService) {
        this.wishListService = wishListService;
        this.authService = authService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public String getWishList(Model model){
        try {
            WishList wishList = this.wishListService.getActiveWishList(this.authService.getCurrentUserId());
            model.addAttribute("wishList", wishList);
            model.addAttribute("currency", "mkd");
            model.addAttribute("amount", (int)(wishList.getBooks().stream().mapToDouble(Book::getPrice).sum() * 100));
            return "wish-list";
        } catch (RuntimeException ex) {
            return "redirect:/books?error=" + ex.getLocalizedMessage();
        }
    }


    @PostMapping("/{bookId}/wish-book")
    public String addBookToWishList(@PathVariable Long bookId) {
        try {
            String userId = this.authService.getCurrentUserId();
            this.wishListService.addBookToWishList(userId, bookId);
        } catch (RuntimeException ex) {
            return "redirect:/books?error="  + ex.getLocalizedMessage();
        }
        return "redirect:/books";

    }

    @PostMapping("/{bookId}/remove-wish")
    public String removeBookFromWishList(@PathVariable Long bookId) {
        this.wishListService.removeBookFromWishList(this.authService.getCurrentUserId(), bookId);
        return "redirect:/wish-list";
    }


    @PostMapping("/{bookId}/new-book")
    public String addBookToShoppingCart(@PathVariable Long bookId) {
        try {
            String userId = this.authService.getCurrentUserId();
            this.shoppingCartService.addBookToShoppingCart(userId, bookId);
        } catch (RuntimeException ex) {
            return "redirect:/wish-list?error=" + ex.getLocalizedMessage();
        }
        return "redirect:/wish-list";
    }

    @PostMapping("/{bookId}/remove-book")
    public String removeBookFromShoppingCart(@PathVariable Long bookId) {
        this.shoppingCartService.removeBookFromShoppingCart(this.authService.getCurrentUserId(), bookId);
        return "redirect:/wish-list";
    }

}
