package org.example.lebonachat.ModuleAnnonce.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleCategory.Service.CategoryService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    private Cloudinary cloudinary;

    // Liste des annonces
    @GetMapping("/annonces")
    public String list(Model model) {
        model.addAttribute("annonces", service.getAll());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", userService.getConnectedUser());
        return "annonces";
    }

    // Formulaire ajout ou modification
    @GetMapping({"/annonce/new", "/annonce/modifier/{id}"})
    public String createOrEditForm(@PathVariable(required = false) Long id, Model model) {
        Announcement annonce;
        if (id != null) {
            annonce = service.getById(id);
            utilisateur connectedUser = userService.getConnectedUser();
            if (!connectedUser.getId().equals(annonce.getCreatedBy().getId())) {
                return "redirect:/annonces"; // sécurité : seul le créateur peut modifier
            }
        } else {
            annonce = new Announcement();
        }
        model.addAttribute("annonce", annonce);
        model.addAttribute("categories", categoryService.getAll());
        return "annonce_form";
    }

    // Enregistrement / Mise à jour d'une annonce
    @PostMapping("/annonce/save")
    public String saveAnnonce(@ModelAttribute Announcement annonce,
                              @RequestParam("imageFile") MultipartFile file) {
        try {
            // Upload image si présent
            if (!file.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "width", 800,
                                "height", 600,
                                "crop", "limit"
                        )
                );
                annonce.setImagePath(uploadResult.get("secure_url").toString());
            }

            if (annonce.getId() == null) {
                // Nouvelle annonce
                annonce.setCreatedBy(userService.getConnectedUser());
                service.save(annonce);
            } else {
                // Modification : récupérer l'annonce existante
                Announcement existing = service.getById(annonce.getId());
                utilisateur connectedUser = userService.getConnectedUser();

                if (!connectedUser.getId().equals(existing.getCreatedBy().getId())) {
                    return "redirect:/annonces"; // sécurité
                }

                // Mettre à jour uniquement les champs modifiables
                existing.setTitre(annonce.getTitre());
                existing.setDescription(annonce.getDescription());
                existing.setPrix(annonce.getPrix());
                existing.setVille(annonce.getVille());
                existing.setCategory(annonce.getCategory());
                if (annonce.getImagePath() != null) {
                    existing.setImagePath(annonce.getImagePath());
                }

                service.save(existing);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/annonces";
    }

    // Détails d'une annonce
    @GetMapping("/annonce/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("annonce", service.getById(id));
        model.addAttribute("user", userService.getConnectedUser());
        return "annonce_details";
    }

    // Suppression d'une annonce
    @PostMapping("/annonce/delete/{id}")
    public String delete(@PathVariable Long id) {
        Announcement annonce = service.getById(id);
        utilisateur connectedUser = userService.getConnectedUser();

        // Sécurité : seul le créateur ou un admin peut supprimer
        if (connectedUser != null &&
                (connectedUser.getId().equals(annonce.getCreatedBy().getId()) ||
                        connectedUser.getRole().name().equals("ROLE_ADMIN"))) {
            service.delete(id);
        }

        return "redirect:/annonces";
    }
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("annonces", service.search(keyword));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", userService.getConnectedUser());
        return "accueil";
    }
    @GetMapping("/filter/category")
    public String filterByCategory(@RequestParam Long categoryId, Model model) {
        model.addAttribute("annonces", service.filterByCategory(categoryId));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", userService.getConnectedUser());
        return "accueil";
    }
}
