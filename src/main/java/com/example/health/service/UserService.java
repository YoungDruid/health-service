package com.example.health.service;

import com.example.health.model.User;
import com.example.health.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public User update(Long id, User user) {

        if (!userRepository.existsById(id)) {
            return null;
        }

        user.setId(id);

        return userRepository.save(user);
    }

    public boolean delete(Long id) {

        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }
}