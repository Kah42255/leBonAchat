package org.example.lebonachat.ModuleUser.Service;

import jakarta.transaction.Transactional;
import org.example.lebonachat.ModuleUser.Metier.Enum.Role;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public utilisateur findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec email: " + email));
    }

    public utilisateur registerUser(String nom, String prenom, String email, String password, String numTel) {
        if (userRepository.findByEmail(email).isPresent()) {
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

    /**
     * Renvoie l'utilisateur connecté ou null si aucun utilisateur n'est connecté
     */
    public utilisateur getConnectedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
            return null; // aucun utilisateur connecté
        }

        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @Transactional
    public utilisateur save(utilisateur user) {
        return userRepository.save(user);
    }

    @Transactional
    public void update(utilisateur user) {
        userRepository.save(user);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public utilisateur getById(Long id) {
        Optional<utilisateur> userOpt = userRepository.findById(id);
        return userOpt.orElse(null);
    }public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    public utilisateur findAdmin() {
        return userRepository.findFirstByRole(Role.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin introuvable"));
    }

    public utilisateur getAdminUser() {
        return findAdmin();
    }

    @Transactional
    public void deleteUser(Long id) {
        utilisateur user = getById(id); // vérifie que l'utilisateur existe
        userRepository.delete(user);
    }
}
