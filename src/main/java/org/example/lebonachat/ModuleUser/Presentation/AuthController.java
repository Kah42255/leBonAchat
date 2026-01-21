package org.example.lebonachat.ModuleUser.Presentation;

import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.example.lebonachat.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        utilisateur user = userService.findByEmail(data.get("email"));

        if (!userService.matches(data.get("password"), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", user
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody utilisateur user) {
        utilisateur u = userService.registerUser(
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getPassword(),
                user.getNumTel()
        );
        return ResponseEntity.ok(u);
    }
}
