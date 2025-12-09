package org.example.lebonachat.ModuleUser.Presentation;

import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class userController {

    private final UserService userService;

    @Autowired
    public userController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String nom,
                               @RequestParam String prenom,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String numTel,
                               Model model) {
        System.out.println("Formulaire reçu pour email : " + email);
        try {
            userService.registerUser(nom, prenom, email, password, numTel);
            return "redirect:/login"; // après inscription, redirection vers login
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // page de login
    }

    @GetMapping("/accueil")
    public String accueil() {
        return "accueil"; // page après login
    }
}
