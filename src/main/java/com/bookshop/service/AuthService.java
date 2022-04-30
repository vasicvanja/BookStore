package com.bookshop.service;

import com.bookshop.model.User;

public interface AuthService {
    User getCurrentUser();

    String getCurrentUserId();

    User signUpUser(String name, String surname, String username, String password, String repeatedPassword);

    User makeUserModerator(String userId);

    User removeUserModerator(String userId);
}
