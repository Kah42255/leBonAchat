package org.example.lebonachat.ModuleCommande.Presentation;

import org.example.lebonachat.ModuleCommande.Metier.Commande;
import org.example.lebonachat.ModuleCommande.Metier.Enum.EtatCommande;
import org.example.lebonachat.ModuleCommande.Service.CommandeService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/commande")
public class CommandeController {

    @Autowired
    private PanierService panierService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private userRepository utilisateurRepository;

    // ===== Valider la commande depuis le panier =====
    @GetMapping("/valider")
    public String validerCommande(@AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/login";

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Panier panier = panierService.getPanierByUser(user);

        if (panier.getLignes().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Votre panier est vide !");
            return "redirect:/accueil";
        }

        commandeService.creerCommande(user, panier.getLignes());
        panierService.viderPanier(panier);

        redirectAttributes.addFlashAttribute("message", "Commande validée avec succès !");
        return "redirect:/accueil";
    }

    // ===== Détails d'une commande =====
    @GetMapping("/details/{id}")
    public String detailsCommande(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {

        if (userDetails == null) return "redirect:/login";

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Commande commande = commandeService.getCommandeById(id);

        boolean estAcheteur = commande.getAcheteur().getId().equals(user.getId());
        boolean estAnnonceur = commande.getCommandesAnnonceur()
                .stream()
                .anyMatch(ca -> ca.getAnnonceur().getId().equals(user.getId()));

        // 🔐 sécurité correcte
        if (!estAcheteur && !estAnnonceur) {
            return "redirect:/accueil";
        }

        model.addAttribute("commande", commande);
        model.addAttribute("estAcheteur", estAcheteur);
        model.addAttribute("estAnnonceur", estAnnonceur);

        boolean estAnnulee = commande.getEtat() == EtatCommande.EST_ANNULEE;
        model.addAttribute("commandeAnnulee", estAnnulee);

        if (estAnnulee) {
            // Assure-toi que ta commande a un champ causeAnnulation
            String cause = commande.getCauseAnnulation() != null ? commande.getCauseAnnulation() : "Non précisée";
            model.addAttribute("commandeAnnuleeMessage", "Cette commande a été annulée. Cause : " + cause);
        }

        return "commande_Details"; // Thymeleaf template
    }

    // ===== Valider la commande par l'annonceur =====
    @PostMapping("/valider/{id}")
    public String validerCommandeAnnonceur(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Commande commande = commandeService.getCommandeById(id);

        commandeService.validerCommandeAnnonceur(commande, user);

        redirectAttributes.addFlashAttribute("message", "Commande validée avec succès.");
        return "redirect:/commande/details/" + id;
    }

    // ===== Annuler la commande par l'annonceur =====
    @PostMapping("/annuler/{id}")
    public String annulerCommandeAnnonceur(@PathVariable Long id,
                                           @RequestParam String cause,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Commande commande = commandeService.getCommandeById(id);

        commandeService.annulerCommandeAnnonceur(commande, user, cause);

        // Message flash pour popup dans l'accueil
        redirectAttributes.addFlashAttribute("commandeAnnuleeMessage",
                "La commande #" + commande.getId() + " a été annulée. Motif : " + cause);

        // Redirection vers l'accueil
        return "redirect:/accueil";
    }
}
