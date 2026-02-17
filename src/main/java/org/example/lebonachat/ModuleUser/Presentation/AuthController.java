package org.example.lebonachat.ModuleUser.Presentation;

import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.example.lebonachat.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // autorise toutes les origines pour test mobile
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String password = data.get("password");

        utilisateur user = userService.findByEmail(email);
        if (user == null || !userService.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", user
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody utilisateur user) {
        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email déjà utilisé"));
        }
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
