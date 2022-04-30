package com.bookshop.service.impl;

import com.bookshop.model.Book;
import com.bookshop.model.ShoppingCart;
import com.bookshop.model.User;
import com.bookshop.model.dto.ChargeRequest;
import com.bookshop.model.enumerations.ShoppingCartStatus;
import com.bookshop.model.exceptions.*;
import com.bookshop.repository.ShoppingCartRepository;
import com.bookshop.service.BookService;
import com.bookshop.service.PaymentService;
import com.bookshop.service.ShoppingCartService;
import com.bookshop.service.UserService;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final UserService userService;
    private final BookService bookService;
    private final PaymentService paymentService;
    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartServiceImpl(UserService userService, BookService bookService, PaymentService paymentService, ShoppingCartRepository shoppingCartRepository) {
        this.userService = userService;
        this.bookService = bookService;
        this.paymentService = paymentService;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Override
    public ShoppingCart findActiveShoppingCartByUsername(String userId) {
        return this.shoppingCartRepository.findByUserUsernameAndStatus(userId, ShoppingCartStatus.CREATED)
                .orElseThrow(() -> new ShoppingCartIsNotActiveException(userId));
    }

    @Override
    public List<ShoppingCart> findAllByUsername(String userId) {
        return this.shoppingCartRepository.findAllByUserUsername(userId);
    }

    @Override
    public ShoppingCart createNewShoppingCart(String userId) {
        if (this.userService.findById(userId).isSuspended()) {
            throw new AccountSuspendedException();
        }
        User user = this.userService.findById(userId);
        if (this.shoppingCartRepository.existsByUserUsernameAndStatus(
                user.getUsername(),
                ShoppingCartStatus.CREATED
        )) {
            throw new ShoppingCartIsAlreadyCreatedException(userId);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return this.shoppingCartRepository.save(shoppingCart);
    }
    @Transactional
    @Override
    public ShoppingCart addBookToShoppingCart(String userId, Long bookId) {
        if (this.userService.findById(userId).isSuspended()) {
            throw new AccountSuspendedException();
        }

        ShoppingCart shoppingCart = this.getActiveShoppingCart(userId);
        Book book = this.bookService.findById(bookId);
        for (Book b : shoppingCart.getBooks()){
            if (b.getId().equals(bookId)) {
                throw new BookAlreadyInWishListException(book.getName());
            }
        }
        if (book.getQuantity() <= 0){
            throw  new BookOutOfStockException(book.getName());
        }
        shoppingCart.getBooks().add(book);
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public boolean existsByUserUsernameAndStatus(String username, ShoppingCartStatus status) {
        return this.shoppingCartRepository.existsByUserUsernameAndStatus(username, status);
    }

    @Transactional
    @Override
    public ShoppingCart removeBookFromShoppingCart(String userId, Long bookId) {
        ShoppingCart shoppingCart = this.getActiveShoppingCart(userId);
        shoppingCart.setBooks(
                shoppingCart.getBooks()
                        .stream()
                        .filter(book -> !book.getId().equals(bookId))
                        .collect(Collectors.toList())
        );
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart getActiveShoppingCart(String userId) {
        return this.shoppingCartRepository.findByUserUsernameAndStatus(userId, ShoppingCartStatus.CREATED)
                .orElseGet(() -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    User user = this.userService.findById(userId);
                    shoppingCart.setUser(user);
                    return this.shoppingCartRepository.save(shoppingCart);
                });
    }

    @Override
    public ShoppingCart cancelActiveShoppingCart(String userId) {
        ShoppingCart shoppingCart = this.shoppingCartRepository.findByUserUsernameAndStatus(userId, ShoppingCartStatus.CREATED)
                .orElseThrow(() -> new ShoppingCartIsNotActiveException(userId));

        shoppingCart.setStatus(ShoppingCartStatus.CANCELED);
        shoppingCart.setCloseDate(LocalDateTime.now());
        return this.shoppingCartRepository.save(shoppingCart);

    }
    @Transactional
    @Override
    public ShoppingCart checkoutShoppingCart(String userId, ChargeRequest chargeRequest) throws StripeException {
        if (this.userService.findById(userId).isSuspended()) {
            throw new AccountSuspendedException();
        }
        ShoppingCart shoppingCart = this.shoppingCartRepository.findByUserUsernameAndStatus(userId, ShoppingCartStatus.CREATED)
                .orElseThrow(() -> new ShoppingCartIsNotActiveException(userId));

        List<Book> books = shoppingCart.getBooks();

        for (Book book : books) {
            if (book.getQuantity() <= 0) {
                throw new BookOutOfStockException(book.getName());
            }
            book.setQuantity(book.getQuantity()-1);
        }
        try {
            this.paymentService.pay(chargeRequest);
        } catch (CardException | AuthenticationException | InvalidRequestException e) {
            throw new TransactionFailedException(userId, e.getMessage());
        }
        shoppingCart.setBooks(books);
        shoppingCart.setStatus(ShoppingCartStatus.FINISHED);
        shoppingCart.setCloseDate(LocalDateTime.now());
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public int countTransactionsByUsername(String username) {
        List<ShoppingCart> transactions = this.shoppingCartRepository.findAllByUserUsernameAndStatus(username, ShoppingCartStatus.FINISHED);
        return transactions.size();
    }

    @Override
    public void deleteShoppingCartsById(String userId) {
        List<ShoppingCart> shoppingCartsToBeDeleted = this.findAllByUsername(userId);
        for (ShoppingCart cart : shoppingCartsToBeDeleted) {
            this.shoppingCartRepository.delete(cart);
        }
    }
}