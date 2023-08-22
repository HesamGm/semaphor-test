package com.jiring.jiringexam.conroller;

import com.jiring.jiringexam.dto.SignInAttempt;
import com.jiring.jiringexam.dto.User;
import com.jiring.jiringexam.dto.UserIn;
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
    public ResponseEntity<Void> signUp(@RequestBody UserIn userIn) {
        userService.signUp(userIn);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<User> signIn(@RequestParam Long userId, @RequestParam String password) {
        // Handle user sign-in request
        return ResponseEntity.ok(userService.signIn(userId, password));
    }

    @GetMapping("/signin-attempts")
    public ResponseEntity<List<SignInAttempt>> getLatestSignInAttempts() {
        List<SignInAttempt> latestAttempts = userService.getLatestSignInAttempts();
        return ResponseEntity.ok(latestAttempts);
    }

    @PatchMapping("/ban")
    public ResponseEntity<Void> banUser(@RequestParam Long userId) {
        this.userService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unban")
    public ResponseEntity<Void> unbanUser(@RequestParam Long userId) {
        this.userService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }
}
