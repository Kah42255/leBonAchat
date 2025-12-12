package org.example.lebonachat.ModuleAnnonce.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleCategory.Service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;
    private final CategoryService categoryService;

    // Chemin de stockage des images
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    // ----------------- LISTE DES ANNONCES -----------------
    @GetMapping("/annonces")
    public String list(Model model) {
        model.addAttribute("annonces", service.getAll());
        model.addAttribute("categories", categoryService.getAll());
        return "annonces";
    }

    // ----------------- FORMULAIRE D'AJOUT -----------------
    @GetMapping("/annonce/new")
    public String createForm(Model model) {
        model.addAttribute("annonce", new Announcement());
        model.addAttribute("categories", categoryService.getAll());
        return "annonce_form";
    }

    // ----------------- AJOUT AVEC UPLOAD -----------------
    @PostMapping("/annonce/save")
    public String saveAnnonce(@ModelAttribute Announcement annonce,
                              @RequestParam("imageFile") MultipartFile file) throws IOException {

        if (file != null && !file.isEmpty() && !file.getOriginalFilename().isEmpty()) {
            try {
                // Créer le dossier s'il n'existe pas
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    System.out.println("Dossier créé : " + uploadPath.toAbsolutePath());
                }

                // Générer un nom de fichier unique
                String originalFileName = file.getOriginalFilename();
                String fileExtension = "";

                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }

                String fileName = System.currentTimeMillis() + fileExtension;
                Path filePath = uploadPath.resolve(fileName);

                // Copier le fichier
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Stocker le chemin URL (commence par /uploads/)
                String imagePath = "/uploads/" + fileName;
                annonce.setImagePath(imagePath);

                System.out.println("Fichier sauvegardé : " + filePath.toAbsolutePath());
                System.out.println("Chemin URL : " + imagePath);

            } catch (IOException e) {
                System.err.println("Erreur lors de l'upload : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun fichier téléchargé ou fichier vide");
        }

        // Sauvegarder l'annonce (avec ou sans image)
        service.save(annonce);
        return "redirect:/annonces";
    }

    // ----------------- DETAILS -----------------
    @GetMapping("/annonce/{id}")
    public String details(@PathVariable Long id, Model model) {
        Announcement annonce = service.getById(id);
        if (annonce == null) {
            return "redirect:/annonces";
        }
        model.addAttribute("annonce", annonce);
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

    // ----------------- FILTRE PAR VILLE -----------------
    @GetMapping("/filter/city")
    public String filterByCity(@RequestParam String city, Model model) {
        model.addAttribute("annonces", service.filterByCity(city));
        model.addAttribute("categories", categoryService.getAll());
        return "annonces";
    }
}