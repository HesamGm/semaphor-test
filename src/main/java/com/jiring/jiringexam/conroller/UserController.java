package com.jiring.jiringexam.conroller;

import com.jiring.jiringexam.dto.SignInAttempt;
import com.jiring.jiringexam.dto.User;
import com.jiring.jiringexam.dto.UserInput;
import com.jiring.jiringexam.dto.UserSignInInput;
import com.jiring.jiringexam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody UserInput userInput) {
        userService.signUp(userInput);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<User> signIn(@RequestBody UserSignInInput input) {
        // Handle user sign-in request
        return ResponseEntity.ok(userService.signIn(input));
    }

    @GetMapping("/signin-attempts")
    public ResponseEntity<List<SignInAttempt>> getLatestSignInAttempts() {
        List<SignInAttempt> latestAttempts = userService.getLatestSignInAttempts();
        return ResponseEntity.ok(latestAttempts);
    }

    @PatchMapping("/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable("id") Long userId) {
        this.userService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable("id") Long userId) {
        this.userService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }
}
