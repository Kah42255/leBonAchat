package org.example.lebonachat.ModuleAnnonce.Metier;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AnnouncementDTO {
    private Long id;
    private String titre;
    private String description;
    private double prix;
    private String ville;
    private String etat;
    private String imageUrl;  // URL Cloudinary
    private LocalDate date;
    private String category;   // Nom de la catégorie
    private String createdBy;  // Nom de l’utilisateur
}
