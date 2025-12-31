package org.example.lebonachat.ModulePanier.Metier;


import jakarta.persistence.*;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private utilisateur utilisateur;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes = new ArrayList<>();

    public void setUtilisateur(utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<LignePanier> getLignes() {
        return lignes;
    }

    public Long getId() {
        return id;
    }
}
