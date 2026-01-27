package org.example.lebonachat.ModuleCommande.Service;

import jakarta.transaction.Transactional;
import org.example.lebonachat.ModuleCommande.Metier.Commande;
import org.example.lebonachat.ModuleCommande.Metier.CommandeAnnonceur;
import org.example.lebonachat.ModuleCommande.Metier.Enum.EtatComAnnonceur;
import org.example.lebonachat.ModuleCommande.Metier.Enum.EtatCommande;
import org.example.lebonachat.ModuleCommande.Metier.LigneCommande;
import org.example.lebonachat.ModuleCommande.Repository.CommandeRepository;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModulePanier.Metier.LignePanier;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final NotificationService notificationService;

    public CommandeService(CommandeRepository commandeRepository, NotificationService notificationService) {
        this.commandeRepository = commandeRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void creerCommande(utilisateur user, List<LignePanier> lignesPanier) {
        if (lignesPanier == null || lignesPanier.isEmpty()) {
            throw new RuntimeException("Le panier est vide, impossible de créer une commande.");
        }

        // 1️⃣ Créer la commande
        Commande commande = new Commande();
        commande.setAcheteur(user);
        commande.setEtat(EtatCommande.EN_ATTENTE);
        commande.setLignes(new ArrayList<>());               // Initialiser la liste des lignes
        commande.setCommandesAnnonceur(new ArrayList<>());   // Initialiser la liste des CommandeAnnonceur

        // 2️⃣ Grouper les lignes par annonceur
        Map<utilisateur, List<LignePanier>> lignesParAnnonceur = new HashMap<>();
        for (LignePanier ligne : lignesPanier) {
            utilisateur annonceur = ligne.getAnnonce().getCreatedBy();
            lignesParAnnonceur.computeIfAbsent(annonceur, k -> new ArrayList<>()).add(ligne);
        }

        // 3️⃣ Créer CommandeAnnonceur et ses lignes
        for (Map.Entry<utilisateur, List<LignePanier>> entry : lignesParAnnonceur.entrySet()) {
            CommandeAnnonceur ca = new CommandeAnnonceur();
            ca.setCommande(commande);
            ca.setAnnonceur(entry.getKey());
            ca.setEtat(EtatComAnnonceur.EN_ATTENTE);
            ca.setLignes(new ArrayList<>());  // Initialiser la liste des lignes du CommandeAnnonceur

            for (LignePanier ligne : entry.getValue()) {
                LigneCommande lc = new LigneCommande();
                lc.setCommande(commande);           // Lier au parent Commande
                lc.setCommandeAnnonceur(ca);
                lc.setAnnonce(ligne.getAnnonce());
                lc.setQuantite(ligne.getQuantite());

                ca.getLignes().add(lc);
                commande.getLignes().add(lc);       // Ajouter directement à la commande
            }

            commande.getCommandesAnnonceur().add(ca);
        }

        // 4️⃣ Calculer le total de la commande
        double total = commande.getLignes().stream()
                .mapToDouble(l -> l.getAnnonce().getPrix() * l.getQuantite())
                .sum();
        commande.setTotal(total);

        // 5️⃣ Sauvegarder la commande
        commandeRepository.save(commande);

        // 6️⃣ Créer les notifications pour chaque annonceur
        for (CommandeAnnonceur ca : commande.getCommandesAnnonceur()) {
            notificationService.creerNotificationCommande(ca.getAnnonceur(), commande);
        }
    }

    // Méthode pour récupérer une commande par son ID
    public Commande getCommandeById(Long commandeId) {
        return commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable : " + commandeId));
    }
}
