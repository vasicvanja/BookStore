package com.bookshop.web;

import com.bookshop.model.Book;
import com.bookshop.model.ShoppingCart;
import com.bookshop.model.dto.ChargeRequest;
import com.bookshop.service.AuthService;
import com.bookshop.service.ShoppingCartService;
import com.bookshop.service.WishListService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Value("pk_test_NmWuTRvlUKaYJdsr3S5VPSYY004AiDmEJn")
    private String publicKey;

    private final ShoppingCartService shoppingCartService;
    private final AuthService authService;
    private final WishListService wishListService;

    public PaymentController(ShoppingCartService shoppingCartService, AuthService authService, WishListService wishListService) {
        this.shoppingCartService = shoppingCartService;
        this.authService = authService;
        this.wishListService = wishListService;
    }

    @GetMapping("/charge")
    public String getCheckoutPage(Model model) {
        try {
            ShoppingCart shoppingCart = this.shoppingCartService.findActiveShoppingCartByUsername(this.authService.getCurrentUserId());
            model.addAttribute("shoppingCart", shoppingCart);
            model.addAttribute("currency", "mkd");
            model.addAttribute("amount", (int) (shoppingCart.getBooks().stream().mapToDouble(Book::getPrice).sum() * 100));
            model.addAttribute("stripePublicKey", this.publicKey);
            return "checkout";
        } catch (RuntimeException ex) {
            return "redirect:/books?error=" + ex.getLocalizedMessage();
        }
    }

    @PostMapping("/charge")
    public String checkout(ChargeRequest chargeRequest, Model model) {
        try {
            this.shoppingCartService.checkoutShoppingCart(this.authService.getCurrentUserId(), chargeRequest);
            return "redirect:/books?message=Successful Payment";
        } catch (RuntimeException | StripeException ex) {
            return "redirect:/payments/charge?error=" + ex.getLocalizedMessage();
        }
    }

    @PostMapping("/{bookId}/remove-book")
    public String removeBookFromShoppingCart(@PathVariable Long bookId) {
        this.shoppingCartService.removeBookFromShoppingCart(this.authService.getCurrentUserId(), bookId);
        return "redirect:/payments/charge";
    }

    @PostMapping("/{bookId}/wish-book")
    public String addBookToWishList(@PathVariable Long bookId) {
        try {
            String userId = this.authService.getCurrentUserId();
            wishListService.addBookToWishList(userId, bookId);
            shoppingCartService.removeBookFromShoppingCart(this.authService.getCurrentUserId(), bookId);
        } catch (RuntimeException ex) {
            if (ex.getLocalizedMessage().contains("is already in the wish list")){
                shoppingCartService.removeBookFromShoppingCart(this.authService.getCurrentUserId(), bookId);
                return "redirect:/payments/charge";
            }
            else return "redirect:/payments/charge?error="  + ex.getLocalizedMessage();
        }
        return "redirect:/payments/charge";
    }

}