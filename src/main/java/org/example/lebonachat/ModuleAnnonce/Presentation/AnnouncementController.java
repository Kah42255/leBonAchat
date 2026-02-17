package org.example.lebonachat.ModuleAnnonce.Presentation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Metier.Enum.EtatAnnonce;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleCategory.Service.CategoryService;
import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Metier.NotificationType;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;
    private final CategoryService categoryService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    private Cloudinary cloudinary;

    // ==================== Liste annonces ====================
    @GetMapping("/annonces")
    public String list(Model model) {
        utilisateur connectedUser = userService.getConnectedUser();

        if (connectedUser != null && connectedUser.getRole().name().equals("ROLE_ADMIN")) {
            model.addAttribute("annonces", service.getAll());
        } else {
            model.addAttribute("annonces", service.getAnnoncesValides());
        }

        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", connectedUser);
        return "annonces";
    }

    // ==================== Formulaire ajout/modif ====================
    @GetMapping({"/annonce/new", "/annonce/modifier/{id}"})
    public String createOrEditForm(@PathVariable(required = false) Long id, Model model) {
        Announcement annonce;
        utilisateur connectedUser = userService.getConnectedUser();

        if (connectedUser == null) {
            return "redirect:/login"; // utilisateur non connecté
        }

        if (id != null) {
            annonce = service.getById(id);
            if (annonce == null || !connectedUser.getId().equals(annonce.getCreatedBy().getId())) {
                return "redirect:/annonces"; // sécurité
            }
        } else {
            annonce = new Announcement();
        }

        model.addAttribute("annonce", annonce);
        model.addAttribute("categories", categoryService.getAll());
        return "annonce_form";
    }

    // ==================== Sauvegarde annonce ====================
    @PostMapping("/annonce/save")
    public String saveAnnonce(@ModelAttribute Announcement annonce,
                              @RequestParam("imageFile") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        try {
            utilisateur connectedUser = userService.getConnectedUser();
            if (connectedUser == null) {
                throw new RuntimeException("Utilisateur non connecté");
            }

            if (annonce.getCategory() == null || annonce.getCategory().getId() == null) {
                throw new RuntimeException("La catégorie est obligatoire");
            }

            // Upload image
            if (file != null && !file.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap("width", 800, "height", 600, "crop", "limit")
                );
                annonce.setImagePath(uploadResult.get("secure_url").toString());
            }

            boolean isNew = (annonce.getId() == null);

            if (isNew) {
                annonce.setCreatedBy(connectedUser);
                annonce.setEtat(EtatAnnonce.EN_ATTENTE);
            } else {
                Announcement existing = service.getById(annonce.getId());
                if (existing == null) throw new RuntimeException("Annonce introuvable");
                if (!connectedUser.getId().equals(existing.getCreatedBy().getId())) return "redirect:/annonces";

                existing.setTitre(annonce.getTitre());
                existing.setDescription(annonce.getDescription());
                existing.setPrix(annonce.getPrix());
                existing.setVille(annonce.getVille());
                existing.setCategory(annonce.getCategory());
                if (annonce.getImagePath() != null) existing.setImagePath(annonce.getImagePath());
                annonce = existing;
            }

            service.save(annonce);

            // ✅ Message flash pour succès
            redirectAttributes.addFlashAttribute("message",
                    isNew ? "Annonce créée avec succès" : "Annonce modifiée avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            // ✅ Message flash pour erreur
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
            return "redirect:/annonce/new";
        }

        return "redirect:/annonces";
    }


    // ==================== Détails d'une annonce ====================
    @GetMapping("/annonce/{id}")
    public String details(@PathVariable Long id, Model model) {
        Announcement annonce = service.getById(id);
        utilisateur connectedUser = userService.getConnectedUser();

        if (annonce == null) {
            return "redirect:/annonces";
        }

        if (annonce.getEtat() != EtatAnnonce.VALIDE &&
                (connectedUser == null || !connectedUser.getRole().name().equals("ROLE_ADMIN"))) {
            return "redirect:/annonces";
        }

        model.addAttribute("annonce", annonce);
        model.addAttribute("user", connectedUser);
        return "annonce_details";
    }

    // ==================== Suppression ====================
    @PostMapping("/annonce/delete/{id}")
    public String delete(@PathVariable Long id) {
        Announcement annonce = service.getById(id);
        utilisateur connectedUser = userService.getConnectedUser();

        if (annonce != null && connectedUser != null &&
                (connectedUser.getId().equals(annonce.getCreatedBy().getId()) ||
                        connectedUser.getRole().name().equals("ROLE_ADMIN"))) {
            service.delete(id);
        }

        return "redirect:/annonces";
    }

    // ==================== Recherche ====================
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        utilisateur connectedUser = userService.getConnectedUser();
        if (connectedUser != null && connectedUser.getRole().name().equals("ROLE_ADMIN")) {
            model.addAttribute("annonces", service.search(keyword));
        } else {
            model.addAttribute("annonces", service.searchValides(keyword));
        }

        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", connectedUser);
        return "accueil";
    }

    // ==================== Filtrage par catégorie ====================
    @GetMapping("/filter/category")
    public String filterByCategory(@RequestParam Long categoryId, Model model) {
        utilisateur connectedUser = userService.getConnectedUser();
        if (connectedUser != null && connectedUser.getRole().name().equals("ROLE_ADMIN")) {
            model.addAttribute("annonces", service.getByCategoryId(categoryId));
        } else {
            model.addAttribute("annonces", service.getAnnoncesValidesByCategory(categoryId));
        }

        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", connectedUser);
        return "accueil";
    }

}
