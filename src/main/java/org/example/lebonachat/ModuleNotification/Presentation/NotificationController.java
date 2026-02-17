package org.example.lebonachat.ModuleNotification.Presentation;

import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Repository.userRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final userRepository utilisateurRepository;

    public NotificationController(NotificationService notificationService,
                                  userRepository utilisateurRepository) {
        this.notificationService = notificationService;
        this.utilisateurRepository = utilisateurRepository;
    }


    @PostMapping("/marquer-lue/{id}")
    @ResponseBody
    public ResponseEntity<?> marquerCommeLue(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (userDetails == null) return ResponseEntity.status(401).build();

        utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Notification notif = notificationService.marquerCommeLue(id, user);

        Long nbNotificationsNonLues = notificationService.countNotificationsNonLues(user);

        return ResponseEntity.ok(new MarquerLueResponse(
                notif.getType().toString(),
                notif.getCommande() != null ? notif.getCommande().getId() : null,
                nbNotificationsNonLues
        ));

    }


    static class MarquerLueResponse {
        public String type;
        public Long commandeId;
        public Long nbNonLues;

        public MarquerLueResponse(String type, Long commandeId, Long nbNonLues) {
            this.type = type;
            this.commandeId = commandeId;
            this.nbNonLues = nbNonLues;
        }
    }
    @GetMapping("/click/{id}")
    public String handleNotificationClick(@PathVariable Long id) {
        Notification notif = notificationService.getNotificationById(id);
        notificationService.marquerCommeLue(id, notif.getUtilisateur());

        return "redirect:" + notif.getLien();
    }

}
