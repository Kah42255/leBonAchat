package org.example.lebonachat.ModulePanier.Presentation;

import jakarta.transaction.Transactional;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Repository.AnnouncementRepository;
import org.example.lebonachat.ModulePanier.Metier.LignePanier;
import org.example.lebonachat.ModulePanier.Metier.Panier;
import org.example.lebonachat.ModulePanier.Repository.PanierRepository;
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
@Autowired
private PanierRepository panierRepository;
    // Ajouter au panier
    @PostMapping("/ajouter/{id}")
    public String ajouterAuPanier(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Announcement annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        panierService.ajouterAuPanier(user, annonce, 1);

        return "redirect:/accueil"; // ← ici la page se recharge et nbArticlesPanier se met à jour
    }


    // Consulter panier
    @GetMapping

    public String consulterPanier(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Panier panier = panierService.getPanierByUser(user);
        model.addAttribute("panier", panier);

        double total = panier.getLignes().stream()
                .mapToDouble(ligne -> ligne.getAnnonce().getPrix() * ligne.getQuantite())
                .sum();
        model.addAttribute("total", total);

        return "panier";
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerLigne(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Panier panier = panierService.getPanierByUser(user);

        panierService.supprimerDuPanier(panier, id);

        return "redirect:/panier";
    }

    @Transactional
    @PostMapping("/increment/{id}")
    public String incrementer(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails) {

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Panier panier = panierService.getPanierByUser(user);
        panierService.incrementerQuantite(panier, id);

        return "redirect:/panier";
    }



    @PostMapping("/decrement/{id}")
    public String decrementer(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails) {
        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Panier panier = panierService.getPanierByUser(user);
        panierService.decrementerQuantite(panier, id);

        return "redirect:/panier";
    }
    @GetMapping("/count")
    @ResponseBody
    public int getNbArticles(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return 0;
        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return 0;
        return panierService.countArticles(user);
    }


}