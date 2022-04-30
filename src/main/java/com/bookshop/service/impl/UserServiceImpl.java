package com.bookshop.service.impl;

import com.bookshop.model.User;
import com.bookshop.model.exceptions.UserNotFoundException;
import com.bookshop.model.exceptions.UsernameAlreadyExistsException;
import com.bookshop.repository.UserRepository;
import com.bookshop.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User findById(String userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public User registerUser(User user) {
        if (this.userRepository.existsById(user.getUsername())) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
        return this.userRepository.save(user);
    }

    @Override
    public void deleteUser(String username) {
        this.userRepository.delete(this.userRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException(username)));
    }

    @Override
    public User expire(String userId) {
        User user = this.findById(userId);
        user.setAccountNonExpired(!user.isAccountNonExpired());
        return this.userRepository.save(user);
    }

    @Override
    public User lock(String userId) {
        User user = this.findById(userId);
        user.setAccountNonLocked(!user.isAccountNonLocked());
        return this.userRepository.save(user);
    }

    @Override
    public User credentialExpire(String userId) {
        User user = this.findById(userId);
        user.setCredentialsNonExpired(!user.isCredentialsNonExpired());
        return this.userRepository.save(user);
    }

    @Override
    public User enable(String userId) {
        User user = this.findById(userId);
        user.setEnabled(!user.isEnabled());
        return this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));
    }
}
