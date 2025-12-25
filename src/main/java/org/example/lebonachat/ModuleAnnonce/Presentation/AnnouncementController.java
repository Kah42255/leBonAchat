package org.example.lebonachat.ModuleAnnonce.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleCategory.Service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;
    private final CategoryService categoryService;  // <-- IMPORTANT

    // ----------------- LISTE DES ANNONCES -----------------
    @GetMapping("/annonces")
    public String list(Model model) {
        model.addAttribute("annonces", service.getAll());
        model.addAttribute("categories", categoryService.getAll()); // <-- AJOUTÉ
        return "annonces";
    }

    // ----------------- FORMULAIRE D'AJOUT -----------------
    @GetMapping("/annonce/new")
    public String createForm(Model model) {
        model.addAttribute("annonce", new Announcement());
        model.addAttribute("categories", categoryService.getAll()); // nécessaire pour le select
        return "annonce_form";
    }

    // ----------------- AJOUT AVEC UPLOAD -----------------
    @PostMapping("/annonce/save")
    public String saveAnnonce(@ModelAttribute Announcement annonce,
                              @RequestParam("imageFile") MultipartFile file) throws Exception {

        // upload image
        if (!file.isEmpty()) {
            String uploadDir = "uploads/";
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            annonce.setImagePath("/uploads/" + fileName);
        }

        service.save(annonce);
        return "redirect:/annonces";
    }

    // ----------------- DETAILS -----------------
    @GetMapping("/annonce/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("annonce", service.getById(id));
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
        return "annonces";
    }

    // ----------------- FILTRE PAR CATÉGORIE -----------------
    @GetMapping("/filter/category")
    public String filterByCategory(@RequestParam Long categoryId, Model model) {
        model.addAttribute("annonces", service.filterByCategory(categoryId));
        model.addAttribute("categories", categoryService.getAll());
        return "annonces";
    }
}
