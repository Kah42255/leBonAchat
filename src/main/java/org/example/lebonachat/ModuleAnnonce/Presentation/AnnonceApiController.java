package org.example.lebonachat.ModuleAnnonce.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// API REST pour Flutter
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnnonceApiController {

    private final AnnouncementService service;

    // Toutes les annonces
    @GetMapping("/annonces")
    public List<Announcement> getAll() {
        return service.getAll();
    }

    // Recherche par mot-clé
    @GetMapping("/search")
    public List<Announcement> search(@RequestParam String keyword) {
        return service.search(keyword);
    }

    // Filtrer par catégorie
    @GetMapping("/filter/category")
    public List<Announcement> filterByCategory(@RequestParam Long categoryId) {
        return service.filterByCategory(categoryId);
    }
}

