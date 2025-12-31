package org.example.lebonachat.ModulePanier.Service;

import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModulePanier.Metier.LignePanier;
import org.example.lebonachat.ModulePanier.Metier.Panier;
import org.example.lebonachat.ModulePanier.Repository.PanierRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.stereotype.Service;

@Service
public class PanierService {

    private final PanierRepository panierRepository;

    public PanierService(PanierRepository panierRepository) {
        this.panierRepository = panierRepository;
    }

    public Panier getPanierByUser(utilisateur user) {
        return panierRepository.findByUtilisateur(user)
                .orElseGet(() -> {
                    Panier nouveauPanier = new Panier();
                    nouveauPanier.setUtilisateur(user);
                    return panierRepository.save(nouveauPanier);
                });
    }

    public void ajouterAuPanier(utilisateur user, Announcement annonce, int quantite) {
        Panier panier = getPanierByUser(user);
        panier.getLignes().add(new LignePanier(annonce, quantite));
        panierRepository.save(panier);
    }

    public void supprimerDuPanier(Panier panier, Long ligneId) {
        panier.getLignes().removeIf(ligne -> ligne.getId().equals(ligneId));
        panierRepository.save(panier);
    }
}
