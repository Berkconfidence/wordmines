package com.example.wordmines.service;

import com.example.wordmines.dto.UserDto;
import com.example.wordmines.entity.User;
import com.example.wordmines.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setPassword(user.getPassword());
                    dto.setSuccess_rate(user.getSuccess_rate());
                    dto.setCreated(user.getCreated());
                    return dto;
                })
                .collect(Collectors.toList());

    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    public User createUser(String username, String email, String password) {

        if(username == null || email == null || password == null)
            throw new IllegalArgumentException("Bilgiler eksik");

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setCreated(new Date());
        newUser.setSuccess_rate(0F);

        return userRepository.save(newUser);
    }


}
