package org.example.lebonachat.ModuleCommande.Metier;

import jakarta.persistence.*;
import org.example.lebonachat.ModuleCommande.Metier.Enum.EtatCommande;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;

import java.util.ArrayList;
import java.util.List;


    @Entity
    public class Commande {
        @Id @GeneratedValue
        private Long id;
        private String causeAnnulation;
        @ManyToOne
        private utilisateur acheteur;
        @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
        private List<LigneCommande> lignes;
        @Enumerated(EnumType.STRING)
        private EtatCommande etat;
        private double total;
        @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
        private List<CommandeAnnonceur> commandesAnnonceur = new ArrayList<>();


        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public utilisateur getAcheteur() {
            return acheteur;
        }

        public List<LigneCommande> getLignes() {
            return lignes;
        }

        public void setLignes(List<LigneCommande> lignes) {
            this.lignes = lignes;
        }

        public void setAcheteur(utilisateur acheteur) {
            this.acheteur = acheteur;
        }

        public EtatCommande getEtat() {
            return etat;
        }

        public void setEtat(EtatCommande etat) {
            this.etat = etat;
        }

        public List<CommandeAnnonceur> getCommandesAnnonceur() {
            return commandesAnnonceur;

        }

        public void setCommandesAnnonceur(List<CommandeAnnonceur> commandesAnnonceur) {
            this.commandesAnnonceur = commandesAnnonceur;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCauseAnnulation() {
            return causeAnnulation;
        }

        public void setCauseAnnulation(String causeAnnulation) {
            this.causeAnnulation = causeAnnulation;
        }
    }


