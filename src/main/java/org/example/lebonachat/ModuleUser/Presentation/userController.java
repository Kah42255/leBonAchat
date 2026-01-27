package org.example.lebonachat.ModuleUser.Presentation;

import jakarta.validation.Valid;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleCategory.Metier.Category;
import org.example.lebonachat.ModuleCategory.Repository.CategoryRepository;
import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;



@Controller
public class userController {

    private final UserService userService;
    private final AnnouncementService announcementService;
    private final CategoryRepository categoryRepository;
@Autowired
private NotificationService notificationService;
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
        System.out.println("DEBUG: /accueil appelé avec categoryId = " + categoryId);

        // 1️⃣ Récupérer l'utilisateur connecté
        utilisateur connectedUser = userService.getConnectedUser();
        model.addAttribute("connectedUser", connectedUser);
        model.addAttribute("nom", connectedUser != null ? connectedUser.getNom() : null);
        model.addAttribute("prenom", connectedUser != null ? connectedUser.getPrenom() : null);

        // ⚡ Charger le panier et ses lignes pour que le badge s'incrémente
        if (connectedUser != null && connectedUser.getPanier() != null) {
            connectedUser.getPanier().getLignes().size(); // force le chargement des lignes
        }

        // 2️⃣ Récupérer toutes les catégories pour la navbar
        List<Category> categories = categoryRepository.findAll();
        System.out.println("DEBUG: Nombre de catégories trouvées: " + categories.size());
        model.addAttribute("categories", categories);

        // 3️⃣ Récupérer les annonces (filtrées par catégorie si besoin)
        List<Announcement> annonces;
        String categoryNom = null;

        if (categoryId != null) {
            System.out.println("DEBUG: Filtrage par catégorie ID: " + categoryId);
            annonces = announcementService.getByCategoryId(categoryId);
            model.addAttribute("categoryId", categoryId);

            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isPresent()) {
                categoryNom = categoryOpt.get().getNom();
                model.addAttribute("categoryNom", categoryNom);
                System.out.println("DEBUG: Nom de catégorie: " + categoryNom);
            } else {
                System.out.println("DEBUG: Catégorie non trouvée pour ID: " + categoryId);
            }
        } else {
            System.out.println("DEBUG: Affichage de toutes les annonces");
            annonces = announcementService.getAll();
        }

        System.out.println("DEBUG: Nombre d'annonces récupérées: " + annonces.size());
        model.addAttribute("annonces", annonces);

        // 4️⃣ Notifications
        if (connectedUser != null) {
            List<Notification> notifications = notificationService.getNotificationsForUser(connectedUser);
            long nbNotificationsNonLues = notifications.stream().filter(n -> !n.getLue()).count();

            model.addAttribute("notifications", notifications);
            model.addAttribute("nbNotificationsNonLues", nbNotificationsNonLues);
        }

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
