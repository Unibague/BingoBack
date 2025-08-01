package bingo.unibague.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.models.autentication.User;
import bingo.unibague.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users.stream().map(u -> 
            "ID: " + u.getId() + ", Username: " + u.getUsername() + ", Email: " + u.getEmail()
        ).toList());
    }
}