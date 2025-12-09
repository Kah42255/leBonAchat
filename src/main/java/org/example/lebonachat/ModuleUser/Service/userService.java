package org.example.lebonachat.ModuleUser.Service;

import org.example.lebonachat.ModuleUser.Metier.Enum.Role;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class userService {
    private final userRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public userService(userRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public utilisateur rejisterUser(String nom, String prenom, String email, String password, String numTel) {
        utilisateur exist = userRepository.findByEmail(email);
        if (exist != null) {
            throw new RuntimeException("Email déjà existente");
        }


        utilisateur user = new utilisateur();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        user.setNumTel(numTel);

        return userRepository.save(user);
    }
    public utilisateur gisterUser(String email, String password) {
        utilisateur user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user; }
        return null;
    }
}
