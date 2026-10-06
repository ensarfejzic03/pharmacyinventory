package com.ensar.pharmacyinventory.service;

import com.ensar.pharmacyinventory.entity.User;
import com.ensar.pharmacyinventory.repository.UserRepository;
import com.ensar.pharmacyinventory.exception.DuplicateEmailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean passwordMatches(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createUser(
            String firstName,
            String lastName,
            String email,
            String password,
            String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("A user with this email already exists");
        }

        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public void deleteUser(int userID) {
        userRepository.deleteById(userID);
    }
}