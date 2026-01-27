package org.example.lebonachat.ModuleNotification.Service;

import jakarta.transaction.Transactional;
import org.example.lebonachat.ModuleCommande.Metier.Commande;
import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Metier.NotificationType;
import org.example.lebonachat.ModuleNotification.Repository.NotificationRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void creerNotificationCommande(utilisateur destinataire, Commande commande) {
        Notification notification = new Notification();
        notification.setUtilisateur(destinataire);
        notification.setCommande(commande);
        notification.setDateCreation(LocalDateTime.now());
        notification.setLue(false);
        notification.setMessage("Vous avez reçu une nouvelle commande de " + commande.getAcheteur().getNom());
        notification.setTitre("Nouvelle commande");
        notification.setType(NotificationType.COMMANDE);

        notificationRepository.save(notification);
    }

    // 🔹 Charger toutes les notifications avec les commandes
    public List<Notification> getNotificationsForUser(utilisateur user) {
        return notificationRepository.findByUtilisateurWithCommande(user);
    }

    public long countNotificationsNonLues(utilisateur user) {
        return notificationRepository.countByUtilisateurAndLueFalse(user);
    }

    @Transactional
    public Notification marquerCommeLue(Long notificationId, utilisateur user) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        if (!notif.getUtilisateur().getId().equals(user.getId())) {
            throw new RuntimeException("Accès interdit");
        }

        notif.setLue(true);
        return notificationRepository.save(notif);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
    }
}
