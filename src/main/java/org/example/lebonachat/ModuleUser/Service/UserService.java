package org.example.lebonachat.ModuleUser.Service;

import jakarta.transaction.Transactional;
import org.example.lebonachat.ModuleUser.Metier.Enum.Role;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(userRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public utilisateur registerUser(String nom, String prenom, String email, String password, String numTel) {
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email déjà utilisé");
        }

        utilisateur user = new utilisateur();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.ROLE_USER);
        user.setNumTel(numTel);

        return userRepository.save(user);
    }


   /* public utilisateur loginUser(String email, String password) {
        utilisateur user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }*/
}
