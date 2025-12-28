package org.example.lebonachat.ModuleUser.Presentation;

import jakarta.validation.Valid;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Repository.AnnouncementRepository;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleCategory.Metier.Category;
import org.example.lebonachat.ModuleCategory.Repository.CategoryRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Controller
public class userController {

    private final UserService userService;
    private final AnnouncementService announcementService;
    private final AnnouncementRepository announcementRepository;
    private final CategoryRepository categoryRepository ;

    @Autowired
    public userController(UserService userService, AnnouncementService announcementService,AnnouncementRepository announcementRepository ,
    CategoryRepository categoryRepository) {
        this.userService = userService;
        this.announcementService = announcementService;
        this.announcementRepository = announcementRepository;
        this.categoryRepository = categoryRepository;
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
    public String accueil(
            @RequestParam(required = false) Long categoryId ,
            Model model) {

        utilisateur user = userService.getConnectedUser();
        model.addAttribute("nom", user != null ? user.getNom() : null);

        List<Announcement> annonces;
        String categoryNom = null;

        if (categoryId != null) {
            // Filtrer par catégorie
            annonces = announcementRepository.findByCategoryId(categoryId);

            // Récupérer le nom de la catégorie pour l'affichage
            Optional<Category> category = categoryRepository.findById(categoryId);
            if (category.isPresent()) {
                categoryNom = category.get().getNom(); // Note: getNom() au lieu de getName()
                model.addAttribute("categoryNom", categoryNom);
            }

            model.addAttribute("categoryId", categoryId);
        } else {
            // Toutes les annonces
            annonces = announcementRepository.findAll();
        }

        model.addAttribute("annonces", annonces);


        return "accueil";
    }







    @GetMapping("/profil")
    public String profilePage(Model model) {
        // Get the currently logged-in user
        utilisateur user = userService.getConnectedUser();

        if (user == null) {
            return "redirect:/login";
        }

        // Get the annonces published by this user
        List<Announcement> annoncesUser = announcementService.getByUser(user);

        // Add attributes for Thymeleaf
        model.addAttribute("user", user);
        model.addAttribute("annonces", annoncesUser);

        return "profil";
    }



}
