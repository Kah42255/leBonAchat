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

    // ==============================
    // 🔔 NOTIFICATION COMMANDE
    // ==============================
    @Transactional
    public void creerNotificationCommande(utilisateur destinataire, Commande commande) {
        Notification notification = new Notification();
        notification.setUtilisateur(destinataire);
        notification.setCommande(commande);
        notification.setDateCreation(LocalDateTime.now());
        notification.setLue(false);
        notification.setMessage("Vous avez reçu une nouvelle commande de " +
                commande.getAcheteur().getNom());
        notification.setTitre("Nouvelle commande");
        notification.setType(NotificationType.COMMANDE);
        notification.setLien("/commande/details/" + commande.getId());
        notificationRepository.save(notification);
    }

    // version personnalisée (OK)
    @Transactional
    public void creerNotificationCommande(utilisateur destinataire, Commande commande,
                                          String titre, String message) {

        Notification notif = new Notification();
        notif.setUtilisateur(destinataire);
        notif.setCommande(commande);
        notif.setType(NotificationType.COMMANDE);
        notif.setTitre(titre);
        notif.setMessage(message);
        notif.setDateCreation(LocalDateTime.now());
        notif.setLue(false);

        notificationRepository.save(notif);
    }

    // ==============================
    // 🔔 NOTIFICATION SIMPLE (ANNONCE)
    // ==============================
    @Transactional
    public void creerNotificationSimple(Notification notification) {

        if (notification.getType() == null) {
            throw new RuntimeException("NotificationType est obligatoire");
        }

        if (notification.getDateCreation() == null) {
            notification.setDateCreation(LocalDateTime.now());
        }

        notification.setLue(false);
        notificationRepository.save(notification);
    }

    // ==============================
    // 📥 CONSULTATION
    // ==============================
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
    @Transactional
    public void creerNotificationAnnonce(utilisateur destinataire, String titre, String message, String lien) {
        Notification notif = new Notification();
        notif.setUtilisateur(destinataire);
        notif.setTitre(titre);
        notif.setMessage(message);
        notif.setLien(lien);
        notif.setType(NotificationType.ANNONCE); // Assure-toi que tu as ce type
        notif.setDateCreation(LocalDateTime.now());
        notif.setLue(false);

        notificationRepository.save(notif);
    }

}
