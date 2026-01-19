package org.example.lebonachat.ModulePanier.Presentation;

import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Repository.AnnouncementRepository;
import org.example.lebonachat.ModulePanier.Metier.Panier;
import org.example.lebonachat.ModulePanier.Service.PanierService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/panier")
public class PanierController {

    @Autowired
    private PanierService panierService;

    @Autowired
    private userRepository utilisateurRepository;

    @Autowired
    private AnnouncementRepository annonceRepository;

    // Ajouter au panier
    @PostMapping("/ajouter/{id}")
    public String ajouterAuPanier(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {

        // Récupérer l'utilisateur complet depuis l'email
        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer l'annonce
        Announcement annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        // Ajouter l'annonce au panier de l'utilisateur
        panierService.ajouterAuPanier(user, annonce, 1);

        return "redirect:/accueil"; // redirige vers l'accueil après ajout
    }

    // Consulter panier
    @GetMapping
    public String consulterPanier(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {

        // Récupérer l'utilisateur complet depuis l'email
        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Panier panier = panierService.getPanierByUser(user);
        model.addAttribute("panier", panier);
        return "panier"; // Thymeleaf template panier.html
    }
}
