package org.example.lebonachat.ModulePanier.Service;

import jakarta.transaction.Transactional;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModulePanier.Metier.LignePanier;
import org.example.lebonachat.ModulePanier.Metier.Panier;
import org.example.lebonachat.ModulePanier.Repository.PanierRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PanierService {

    private final PanierRepository panierRepository;

    public PanierService(PanierRepository panierRepository) {
        this.panierRepository = panierRepository;
    }

    // Récupérer le panier de l'utilisateur ou le créer s'il n'existe pas
    @Transactional
    public Panier getPanierByUser(utilisateur user) {
        return panierRepository.findByUtilisateur(user)
                .orElseGet(() -> {
                    Panier nouveauPanier = new Panier();
                    nouveauPanier.setUtilisateur(user);
                    return panierRepository.save(nouveauPanier);
                });
    }

    // Ajouter une annonce au panier
    @Transactional
    public void ajouterAuPanier(utilisateur user, Announcement annonce, int quantite) {
        Panier panier = getPanierByUser(user);

        // Vérifier si l'annonce existe déjà dans le panier
        Optional<LignePanier> ligneExistante = panier.getLignes().stream()
                .filter(l -> l.getAnnonce().getId().equals(annonce.getId()))
                .findFirst();

        if (ligneExistante.isPresent()) {
            // Si elle existe, incrémenter la quantité
            LignePanier ligne = ligneExistante.get();
            ligne.setQuantite(ligne.getQuantite() + quantite);
        } else {
            // Sinon créer une nouvelle ligne
            LignePanier ligne = new LignePanier();
            ligne.setAnnonce(annonce);
            ligne.setQuantite(quantite);
            ligne.setPanier(panier); // ⚡ très important pour la relation
            panier.getLignes().add(ligne);
        }

        panierRepository.save(panier);
    }

    // Supprimer une ligne ou décrémenter la quantité
    @Transactional
    public void supprimerDuPanier(Panier panier, Long ligneId) {
        panier.getLignes().removeIf(ligne -> {
            if (ligne.getId().equals(ligneId)) {
                if (ligne.getQuantite() > 1) {
                    ligne.setQuantite(ligne.getQuantite() - 1);
                    return false; // on ne supprime pas complètement
                }
                return true; // supprimer si quantité = 1
            }
            return false;
        });
        panierRepository.save(panier);
    }

    // Incrémenter la quantité
    @Transactional
    public void incrementerQuantite(Panier panier, Long ligneId) {
        panier.getLignes().forEach(ligne -> {
            if (ligne.getId().equals(ligneId)) {
                ligne.setQuantite(ligne.getQuantite() + 1);
            }
        });
        panierRepository.save(panier);
    }

    // Décrémenter la quantité
    @Transactional
    public void decrementerQuantite(Panier panier, Long ligneId) {
        panier.getLignes().removeIf(ligne -> {
            if (ligne.getId().equals(ligneId)) {
                if (ligne.getQuantite() > 1) {
                    ligne.setQuantite(ligne.getQuantite() - 1);
                    return false;
                }
                return true; // supprimer si quantité = 1
            }
            return false;
        });
        panierRepository.save(panier);
    }

    // Calculer le total du panier
    public double calculerTotal(Panier panier) {
        return panier.getLignes().stream()
                .mapToDouble(ligne -> ligne.getAnnonce().getPrix() * ligne.getQuantite())
                .sum();
    }

    // Nombre total d'articles
    public int countArticles(utilisateur user) {
        Panier panier = getPanierByUser(user);
        return panier.getLignes().stream()
                .mapToInt(LignePanier::getQuantite)
                .sum();
    }

    // Vider le panier
    @Transactional
    public void viderPanier(Panier panier) {
        panier.getLignes().clear();
        panierRepository.save(panier);
    }

    // Sauvegarder le panier
    @Transactional
    public void save(Panier panier) {
        panierRepository.save(panier);
    }
}
