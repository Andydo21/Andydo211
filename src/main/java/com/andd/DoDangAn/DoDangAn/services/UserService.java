package com.andd.DoDangAn.DoDangAn.services;

import com.andd.DoDangAn.DoDangAn.models.Role;
import com.andd.DoDangAn.DoDangAn.models.User;
import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Chuyển đổi Set<Role> thành mảng String roles
        String[] roles = user.getRoles().stream()
                .map(Role::getName) // Giả sử Role có phương thức getName()
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassWord())
                .roles(roles)
                .build();
    }

    public User registerUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUserName());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
        return userRepository.save(user);
    }
}