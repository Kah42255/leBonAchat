package org.example.lebonachat.ModuleAnnonce.Presentation;

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

    // ----------------- LISTE DES ANNONCES -----------------
    @GetMapping("/annonces")
    public String list(Model model) {
        model.addAttribute("annonces", service.getAll());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", userService.getConnectedUser());
        return "annonces";
    }

    // ----------------- FORMULAIRE D'AJOUT -----------------
    @GetMapping("/annonce/new")
    public String createForm(Model model) {
        model.addAttribute("annonce", new Announcement());
        model.addAttribute("categories", categoryService.getAll());
        return "annonce_form";
    }

    // ----------------- AJOUT AVEC UPLOAD CLOUDINARY -----------------
    @PostMapping("/annonce/save")
    public String saveAnnonce(@ModelAttribute Announcement annonce,
                              @RequestParam("imageFile") MultipartFile file) {

        try {
            // Upload image vers Cloudinary avec taille maximale
            if (!file.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "width", 800,       // largeur max
                                "height", 600,      // hauteur max
                                "crop", "limit"     // conserve les proportions
                        )
                );

                String imageUrl = uploadResult.get("secure_url").toString();
                annonce.setImagePath(imageUrl);
            }

            // Lier l'annonce à l'utilisateur connecté
            annonce.setCreatedBy(userService.getConnectedUser());

            service.save(annonce);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/annonces";
    }

    // ----------------- DETAILS -----------------
    @GetMapping("/annonce/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("annonce", service.getById(id));
        model.addAttribute("user", userService.getConnectedUser());
        return "annonce_details";
    }

    // ----------------- SUPPRESSION -----------------
    @GetMapping("/annonce/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/annonces";
    }

    // ----------------- RECHERCHE -----------------
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("annonces", service.search(keyword));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", userService.getConnectedUser());
        return "annonces";
    }

    // ----------------- FILTRE PAR CATÉGORIE -----------------
    @GetMapping("/filter/category")
    public String filterByCategory(@RequestParam Long categoryId, Model model) {
        model.addAttribute("annonces", service.filterByCategory(categoryId));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("user", userService.getConnectedUser());
        return "annonces";
    }
}
