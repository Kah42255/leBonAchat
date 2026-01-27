package org.example.lebonachat.ModuleCommande.Metier;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;

@Entity
public class LigneCommande {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Commande commande;
    @ManyToOne
    private CommandeAnnonceur commandeAnnonceur;

    @ManyToOne
    private Announcement annonce;

    private int quantite;
    private double prix;

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Long getId() {
        return id;
    }

    public CommandeAnnonceur getCommandeAnnonceur() {
        return commandeAnnonceur;
    }

    public Announcement getAnnonce() {
        return annonce;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setCommandeAnnonceur(CommandeAnnonceur commandeAnnonceur) {
        this.commandeAnnonceur = commandeAnnonceur;
    }

    public void setAnnonce(Announcement annonce) {
        this.annonce = annonce;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}
