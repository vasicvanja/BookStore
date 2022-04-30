package com.bookshop.web;

import com.bookshop.model.User;
import com.bookshop.model.enumerations.ShoppingCartStatus;
import com.bookshop.service.AuthService;
import com.bookshop.service.ShoppingCartService;
import com.bookshop.service.UserService;
import com.bookshop.service.WishListService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final AuthService authService;
    private final ShoppingCartService shoppingCartService;
    private final WishListService wishListService;

    public AdminController(UserService userService, AuthService authService, ShoppingCartService shoppingCartService, WishListService wishListService) {
        this.userService = userService;
        this.authService = authService;
        this.shoppingCartService = shoppingCartService;
        this.wishListService = wishListService;
    }

    @GetMapping
    public String loadAdminPage(Model model) {
        List<User> users = this.userService.findAll().stream().filter(user -> !user.getUsername().equals("admin")).collect(Collectors.toList());
        model.addAttribute("users", users);
        model.addAttribute("shoppingCartService", shoppingCartService);
        return "admin";
    }

    @PostMapping("/{username}/deleteUser")
    @Secured("ROLE_ADMIN")
    public String deleteUser(@PathVariable String username) {
        if (!username.equals("admin")) {
            this.wishListService.deleteWishList(username);

            if (this.shoppingCartService.existsByUserUsernameAndStatus(username, ShoppingCartStatus.CREATED)) {
                this.shoppingCartService.cancelActiveShoppingCart(username);
            }

            this.shoppingCartService.deleteShoppingCartsById(username);
            this.userService.deleteUser(username);
            return "redirect:/admin";
        } else {
            throw new RuntimeException("Can't delete admin");
        }
    }

    @PostMapping("/{username}/makeMod")
    @Secured("ROLE_ADMIN")
    public String makeUserModerator(@PathVariable String username) {
        try {
            this.authService.makeUserModerator(username);
            return "redirect:/admin";
        }
        catch (RuntimeException ex) {
            return "redirect:/admin?error=" + ex.getLocalizedMessage();
        }

    }
    @PostMapping("/{username}/removeMod")
    @Secured("ROLE_ADMIN")
    public String removeUserModerator(@PathVariable String username) {
        try {
            this.authService.removeUserModerator(username);
            return "redirect:/admin";
        }
        catch (RuntimeException ex) {
            return "redirect:/admin?error=" + ex.getLocalizedMessage();
        }

    }

    @GetMapping("/{username}/nonExpired")
    @Secured("ROLE_MODERATOR")
    public String expireUser(@PathVariable String username) {
        this.userService.expire(username);
        return "redirect:/admin";
    }

    @GetMapping("/{username}/nonLocked")
    @Secured("ROLE_MODERATOR")
    public String lockUser(@PathVariable String username) {
        this.userService.lock(username);
        return "redirect:/admin";
    }

    @GetMapping("/{username}/credentialNonExpired")
    @Secured("ROLE_MODERATOR")
    public String expireUserCredential(@PathVariable String username) {
        this.userService.credentialExpire(username);
        return "redirect:/admin";
    }

    @GetMapping("/{username}/enabled")
    @Secured("ROLE_MODERATOR")
    public String enableUser(@PathVariable String username) {
        this.userService.enable(username);
        return "redirect:/admin";
    }
}
