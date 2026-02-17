package org.example.lebonachat.ModulePanier.Presentation;


import org.example.lebonachat.ModulePanier.Service.PanierService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Repository.userRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class panierGlo {

    private final PanierService panierService;
    private final userRepository utilisateurRepository;

    public panierGlo(PanierService panierService,
                              userRepository utilisateurRepository) {
        this.panierService = panierService;
        this.utilisateurRepository = utilisateurRepository;
    }
    @GetMapping("/count")
    @ResponseBody
    public int getNbArticles(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return 0;

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return 0;

        return panierService.countArticles(user);
    }

    @ModelAttribute
    public void addPanierInfo(Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails != null) {
            utilisateur user = utilisateurRepository
                    .findByEmail(userDetails.getUsername())
                    .orElse(null);

            if (user != null) {
                int nbArticles = panierService.countArticles(user);
                model.addAttribute("nbArticlesPanier", nbArticles);
            }
        }
    }
}
