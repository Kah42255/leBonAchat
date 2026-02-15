package org.example.lebonachat.ModuleCommande.Metier;

import jakarta.persistence.*;
import org.example.lebonachat.ModuleCommande.Metier.Enum.EtatComAnnonceur;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;

import java.util.ArrayList;
import java.util.List;

@Entity
public class CommandeAnnonceur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Commande commande;

    @ManyToOne
    private utilisateur annonceur;

    @Enumerated(EnumType.STRING)
    private EtatComAnnonceur etat;

    @OneToMany(mappedBy = "commandeAnnonceur", cascade = CascadeType.ALL)
    private List<LigneCommande> lignes = new ArrayList<>();

    public Commande getCommande() {
        return commande;
    }

    public utilisateur getAnnonceur() {
        return annonceur;
    }

    public EtatComAnnonceur getEtat() {
        return etat;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public void setAnnonceur(utilisateur annonceur) {
        this.annonceur = annonceur;
    }

    public void setEtat(EtatComAnnonceur etat) {
        this.etat = etat;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }
}

