package org.example.lebonachat.ModuleAnnonce.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Metier.AnnouncementDTO;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnnonceApiController {

    private final AnnouncementService service;

    // Toutes les annonces
    @GetMapping("/annonces")
    public List<AnnouncementDTO> getAll() {
        return service.getAll().stream().map(a ->
                new AnnouncementDTO(
                        a.getId(),
                        a.getTitre(),
                        a.getDescription(),
                        a.getPrix(),
                        a.getVille(),
                        a.getEtat().name(),
                        a.getImagePath() != null ? a.getImagePath() : "",
                        a.getDate(),
                        a.getCategory() != null ? a.getCategory().getNom() : "",
                        a.getCreatedBy() != null ? a.getCreatedBy().getNom() : ""
                )
        ).collect(Collectors.toList());
    }

    // Recherche par mot-clé
    @GetMapping("/search")
    public List<AnnouncementDTO> search(@RequestParam String keyword) {
        return service.search(keyword).stream().map(a ->
                new AnnouncementDTO(
                        a.getId(),
                        a.getTitre(),
                        a.getDescription(),
                        a.getPrix(),
                        a.getVille(),
                        a.getEtat().name(),
                        a.getImagePath() != null ? a.getImagePath() : "",
                        a.getDate(),
                        a.getCategory() != null ? a.getCategory().getNom() : "",
                        a.getCreatedBy() != null ? a.getCreatedBy().getNom() : ""
                )
        ).collect(Collectors.toList());
    }

    // Filtrer par catégorie
    @GetMapping("/filter/category")
    public List<AnnouncementDTO> filterByCategory(@RequestParam Long categoryId) {
        return service.getByCategoryId(categoryId).stream().map(a ->
                new AnnouncementDTO(
                        a.getId(),
                        a.getTitre(),
                        a.getDescription(),
                        a.getPrix(),
                        a.getVille(),
                        a.getEtat().name(),
                        a.getImagePath() != null ? a.getImagePath() : "",
                        a.getDate(),
                        a.getCategory() != null ? a.getCategory().getNom() : "",
                        a.getCreatedBy() != null ? a.getCreatedBy().getNom() : ""
                )
        ).collect(Collectors.toList());
    }
}
