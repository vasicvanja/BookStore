package com.bookshop.service.impl;

import com.bookshop.model.Role;
import com.bookshop.model.User;
import com.bookshop.model.exceptions.PasswordsDoNotMatchException;
import com.bookshop.model.exceptions.UserIsAlreadyModeratorException;
import com.bookshop.repository.RoleRepository;
import com.bookshop.repository.UserRepository;
import com.bookshop.service.AuthService;
import com.bookshop.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserService userService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public String getCurrentUserId() {
        return this.getCurrentUser().getUsername();
    }

    @Override
    public User signUpUser(String name, String surname, String username, String password, String repeatedPassword) {
        if (!password.equals(repeatedPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        User user = new User(name, surname, username, this.passwordEncoder.encode(password));
        Role userRole = this.roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singletonList(userRole));
        return this.userService.registerUser(user);
    }

    @Override
    public User makeUserModerator(String userId) {
        User user = this.userService.findById(userId);
        List<Role> userRoles = user.getRoles();
        if (!userRoles.contains(this.roleRepository.findByName("ROLE_MODERATOR"))) {
            userRoles.add(this.roleRepository.findByName("ROLE_MODERATOR"));
            user.setRoles(userRoles);
            return this.userRepository.save(user);
        }
        else throw new UserIsAlreadyModeratorException(userId);
    }

    @Override
    public User removeUserModerator(String userId) {
        User user = this.userService.findById(userId);
        List<Role> userRoles = user.getRoles();
        if (userRoles.contains(this.roleRepository.findByName("ROLE_MODERATOR"))) {
            userRoles.remove(this.roleRepository.findByName("ROLE_MODERATOR"));
            user.setRoles(userRoles);
            return this.userRepository.save(user);
        }
        else throw new RuntimeException("User is not a moderator");
    }

//    @PostConstruct
//    public void init() {
//        if (!this.userRepository.existsById("admin")) {
//            Role role = new Role("ROLE_ADMIN");
//            Role role2 = new Role("ROLE_USER");
//            Role role3 = new Role("ROLE_MODERATOR");
//            this.roleRepository.save(role);
//            this.roleRepository.save(role2);
//            this.roleRepository.save(role3);
//            User admin = new User("admin", "admin", "admin", this.passwordEncoder.encode("admin"));
//            admin.setRoles(this.roleRepository.findAll());
//            this.userRepository.save(admin);
//        }
//    }
}
