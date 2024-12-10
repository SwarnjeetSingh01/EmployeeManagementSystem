package controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // Mock authentication logic
        if ("admin".equals(username) && "admin123".equals(password)) {
            return "Login successful!";
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}

