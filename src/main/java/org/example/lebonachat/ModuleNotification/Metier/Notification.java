package org.example.lebonachat.ModuleNotification.Metier;

import jakarta.persistence.*;
import org.example.lebonachat.ModuleCommande.Metier.Commande;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lien;
    private String message;
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private LocalDateTime dateCreation=LocalDateTime.now();;

    private Boolean lue = false;

    // ⚠ Associer correctement l'utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id_user", nullable = false)
    private utilisateur utilisateur;

    // ⚠ Lien vers la commande si applicable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id")
    private Commande commande;

    // getters & setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Boolean getLue() { return lue; }
    public void setLue(Boolean lue) { this.lue = lue; }

    public utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
}
