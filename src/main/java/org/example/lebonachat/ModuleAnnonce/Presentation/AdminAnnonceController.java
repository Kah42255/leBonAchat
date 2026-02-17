package org.example.lebonachat.ModuleAnnonce.Presentation;

import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminAnnonceController {

    private final AnnouncementService announcementService;
    private final NotificationService notifService;
    public AdminAnnonceController(AnnouncementService announcementService,  NotificationService notifService) {
        this.announcementService = announcementService;
        this.notifService = notifService;
    }

    @GetMapping("/admin/annonces")
    public String gererAnnonces(Model model) {
        List<Announcement> annonces = announcementService.getAll(); // récupère toutes les annonces
        model.addAttribute("annonces", annonces);
        return "admin/gerer_annonce"; // Thymeleaf va chercher templates/admin/gerer_annonce.html
    }


    @PostMapping("/admin/annonces/valider/{id}")
    public String validerAnnonce(@PathVariable Long id) {
        announcementService.validerAnnonce(id);
        return "redirect:/admin/annonces";
    }

    @PostMapping("/admin/annonces/annuler/{id}")
    public String annulerAnnonce(@PathVariable Long id,
                                 @RequestParam String causeAnnulation) {

        announcementService.annulerAnnonce(id, causeAnnulation);
        return "redirect:/admin/annonces";
    }

    @GetMapping("/admin/notification/{id}")
    public String voirNotification(@PathVariable Long id) {
        Notification notif = notifService.getNotificationById(id); // récupère la notification par son id
        notif.setLue(true);
        notifService.creerNotificationSimple(notif);
        return "redirect:/admin/annonces";
    }



}
