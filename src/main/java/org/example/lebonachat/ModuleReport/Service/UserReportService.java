package org.example.lebonachat.ModuleReport.Service;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Metier.NotificationType;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModuleReport.Metier.ReportStatus;
import org.example.lebonachat.ModuleReport.Metier.UserReport;
import org.example.lebonachat.ModuleReport.Repository.UserReportRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    public void reportUser(Long reportedUserId, String reason) {
        utilisateur reporter = userService.getConnectedUser();
        utilisateur reportedUser = userService.getById(reportedUserId);

        if (reporter == null) {
            throw new RuntimeException("Vous devez être connecté pour signaler un utilisateur.");
        }

        if (reportedUser == null) {
            throw new RuntimeException("Utilisateur introuvable.");
        }

        UserReport report = UserReport.builder()
                .reportedUser(reportedUser)
                .reporter(reporter)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .build();

        userReportRepository.save(report);
        Notification notif = new Notification();
        notif.setUtilisateur(userService.getAdminUser()); // Méthode pour récupérer l’admin
        notif.setType(NotificationType.INFO);
        notif.setTitre("Utilisateur signalé");
        notif.setMessage("L'utilisateur '" + reportedUser.getNom() + "' a été signalé pour : " + reason);
        notif.setLien("/admin/gerer_report/reports" + reportedUserId); // lien vers la gestion
        notificationService.creerNotificationSimple(notif);
    }
    public UserReport getUserReportById(Long userReportId) {
        return userReportRepository.findById(userReportId)
                .orElseThrow(() -> new RuntimeException("Signalement utilisateur non trouvé"));
    }
    public List<UserReport> getAllUserReports() {
        return userReportRepository.findAll();
    }
    public long getUserReportCount(Long userId) {
        return userReportRepository.countReportsByUser(userId);
    }
    @Transactional
    public void sendWarningForUser(Long userReportId) {
        UserReport report = getUserReportById(userReportId);

        utilisateur reportedUser = report.getReportedUser();

        Notification notif = new Notification();
        notif.setUtilisateur(reportedUser);
        notif.setType(NotificationType.WARNING);
        notif.setTitre("⚠️ Avertissement");
        notif.setMessage("Votre compte a été signalé. Merci de respecter les règles de la plateforme.");
        notif.setDateCreation(LocalDateTime.now());
        notif.setLue(false);

        notificationService.creerNotificationSimple(notif);

        report.setStatus(ReportStatus.RESOLVED);
        userReportRepository.save(report);
    }
    @Transactional
    public void supprimerUtilisateurEtResoudre(Long userReportId) {
        UserReport report = userReportRepository.findById(userReportId)
                .orElseThrow(() -> new RuntimeException("Signalement utilisateur introuvable"));

        utilisateur user = report.getReportedUser();
        if (user != null) {
            userService.deleteUser(user.getId());
        }

        report.setStatus(ReportStatus.RESOLVED);
        userReportRepository.save(report);
    }


}
