package org.example.lebonachat.ModuleAnnonce.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Metier.Enum.EtatAnnonce;
import org.example.lebonachat.ModuleAnnonce.Repository.AnnouncementRepository;
import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Metier.NotificationType;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository repo;
    private final UserService userService;
    private final NotificationService notificationService;

    // ==================== Récupération ====================
    public List<Announcement> getAll() {
        return repo.findAll();
    }

    public List<Announcement> getAnnoncesValides() {
        return repo.findByEtat(EtatAnnonce.VALIDE);
    }

    public List<Announcement> getAnnoncesValidesByCategory(Long categoryId) {
        return repo.findByEtatAndCategoryId(EtatAnnonce.VALIDE, categoryId);
    }

    public List<Announcement> searchValides(String keyword) {
        return repo.findByEtatAndTitreContainingIgnoreCase(EtatAnnonce.VALIDE, keyword);
    }

    public List<Announcement> getByCategoryId(Long categoryId) {
        return repo.findByCategoryId(categoryId);
    }

    public List<Announcement> getByUser(utilisateur user) {
        return repo.findByCreatedBy(user);
    }

    public Announcement getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // ==================== CRUD ====================
    @Transactional
    public Announcement save(Announcement annonce) {
        if (annonce.getId() == null) {
            annonce.setEtat(EtatAnnonce.EN_ATTENTE);
        }

        Announcement saved = repo.save(annonce);

        // Notification admin
        utilisateur admin = userService.findAdmin(); // ✅ utilise la méthode du service
        Notification notif = new Notification();
        notif.setUtilisateur(admin);
        notif.setMessage("Nouvelle annonce en attente de validation par " + saved.getCreatedBy().getNom());
        notif.setTitre("Nouvelle annonce");
        notif.setLien("/admin/annonces");
        notif.setLue(false);
        notif.setType(NotificationType.INFO);

        notificationService.creerNotificationSimple(notif);

        return saved;
    }

    @Transactional
    public void validerAnnonce(Long id) {
        repo.findById(id).ifPresent(annonce -> {
            annonce.setEtat(EtatAnnonce.VALIDE);
            repo.save(annonce);


        });
    }
    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement annonce = getById(id); // vérifie que l'annonce existe
        repo.delete(annonce);
    }
    @Transactional
    public void annulerAnnonce(Long id, String causeAnnulation) {
        repo.findById(id).ifPresent(annonce -> {
            annonce.setEtat(EtatAnnonce.ANNULEE);
            repo.save(annonce);

            Notification notif = new Notification();
            notif.setUtilisateur(annonce.getCreatedBy());
            notif.setTitre("Annonce annulée");
            notif.setMessage(
                    "Votre annonce '" + annonce.getTitre() +
                            "' a été annulée.\nCause : " + causeAnnulation
            );
            notif.setLien("#");
            notif.setType(NotificationType.ANNONCE);

            notificationService.creerNotificationSimple(notif);
        });
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Announcement> search(String keyword) {
        return repo.findByTitreContainingIgnoreCase(keyword);
    }





}
