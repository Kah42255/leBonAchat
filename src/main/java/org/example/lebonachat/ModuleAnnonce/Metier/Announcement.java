package org.example.lebonachat.ModuleAnnonce.Metier;

import jakarta.persistence.*;
import lombok.*;
import org.example.lebonachat.ModuleCategory.Metier.Category;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private double prix;
    private String ville;
    private String imagePath;
    @ManyToOne
    private Category category;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private utilisateur createdBy;
}
