package org.example.lebonachat.ModuleUser.Presentation;

import jakarta.validation.Valid;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleCategory.Repository.CategoryRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class userController {

    private final UserService userService;
    private final AnnouncementService announcementService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public userController(UserService userService,
                          AnnouncementService announcementService,
                          CategoryRepository categoryRepository) {
        this.userService = userService;
        this.announcementService = announcementService;
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
            model.addAttribute("errors", "Veuillez corriger les erreurs");
            return "register";
        }
        try {
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
    public String accueil(@RequestParam(required = false) Long categoryId, Model model) {
        utilisateur connectedUser = userService.getConnectedUser();
        model.addAttribute("connectedUser", connectedUser);
        model.addAttribute("nom", connectedUser != null ? connectedUser.getNom() : null);
        model.addAttribute("prenom", connectedUser != null ? connectedUser.getPrenom() : null);

        List<Announcement> annonces;
        if (categoryId != null) {
            annonces = announcementService.getByCategoryId(categoryId);
            model.addAttribute("categoryId", categoryId);
        } else {
            annonces = announcementService.getAll();
        }
        model.addAttribute("annonces", annonces);
        return "accueil";
    }

    @GetMapping("/profil")
    public String profilePage(Model model) {
        utilisateur connectedUser = userService.getConnectedUser();
        if (connectedUser == null) return "redirect:/login";

        List<Announcement> annoncesUser = announcementService.getByUser(connectedUser);
        model.addAttribute("user", connectedUser);
        model.addAttribute("annonces", annoncesUser);
        model.addAttribute("connectedUser", connectedUser);
        return "profil";
    }

    @GetMapping("/profil/edit")
    public String editProfile(Model model) {
        utilisateur user = userService.getConnectedUser();
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile_edit";
    }

    @PostMapping("/profil/save")
    public String saveProfile(@ModelAttribute("user") utilisateur updatedUser) {
        utilisateur currentUser = userService.getConnectedUser();
        if (currentUser == null) return "redirect:/login";

        if (!currentUser.getEmail().equals(updatedUser.getEmail())
                && userService.emailExists(updatedUser.getEmail())) {
            return "profile_edit";
        }

        currentUser.setNom(updatedUser.getNom());
        currentUser.setPrenom(updatedUser.getPrenom());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setNumTel(updatedUser.getNumTel());

        userService.update(currentUser);

        return "redirect:/profil";
    }

    @GetMapping("/profil/{id}")
    public String viewUserProfile(@PathVariable Long id, Model model) {
        utilisateur user = userService.getById(id);
        if (user == null) return "redirect:/accueil";

        List<Announcement> annoncesUser = announcementService.getByUser(user);
        utilisateur connectedUser = userService.getConnectedUser();
        model.addAttribute("user", user);
        model.addAttribute("annonces", annoncesUser);
        model.addAttribute("connectedUser", connectedUser);
        return "profil";
    }
}
