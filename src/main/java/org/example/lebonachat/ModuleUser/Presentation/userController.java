package org.example.lebonachat.ModuleUser.Presentation;

import jakarta.validation.Valid;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class userController {

    private final UserService userService;

    @Autowired
    public userController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("utilisateur", new utilisateur());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid utilisateur utilisateur,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors"," veuilliez corriger les erreurs");
            if (bindingResult.hasFieldErrors("password")) {
                String password = utilisateur.getPassword();
                String message = null;

                if (password == null || password.isEmpty()) {
                    message = "Le mot de passe est obligatoire";
                } else if (password.length() < 8) {
                    message = "Le mot de passe doit contenir au moins 8 caractères";
                } else if (!password.matches(".*[A-Z].*")) {
                    message = "Le mot de passe doit contenir au moins une majuscule";
                } else if (!password.matches(".*[a-z].*")) {
                    message = "Le mot de passe doit contenir au moins une minuscule";
                } else if (!password.matches(".*\\d.*")) {
                    message = "Le mot de passe doit contenir au moins un chiffre";
                }

                if (message != null) {
                    bindingResult.rejectValue("password", "error.password", message);
                }
            }

            return "register";
        }
        try {
            System.out.println("Formulaire reçu pour email : " + utilisateur.getEmail());


            userService.registerUser(
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    utilisateur.getPassword(),
                    utilisateur.getNumTel());
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/accueil")
    public String accueil(Model model, Principal principal) {
        model.addAttribute("email", principal.getName());
        return "/accueil";
    }

}
