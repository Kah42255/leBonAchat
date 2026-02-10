package org.example.lebonachat.ModuleReport.Service;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleNotification.Metier.NotificationType;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModuleReport.Metier.Report;
import org.example.lebonachat.ModuleReport.Metier.ReportStatus;
import org.example.lebonachat.ModuleReport.Repository.ReportRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final AnnouncementService announcementService;
    private final NotificationService notificationService;
    public Report reportAnnouncement(Long announcementId, String reason) {
        utilisateur reporter = userService.getConnectedUser();
        if (reporter == null) {
            throw new RuntimeException("Vous devez être connecté pour signaler une annonce");
        }

        Announcement announcement = announcementService.getById(announcementId);
        if (announcement == null) {
            throw new RuntimeException("Annonce non trouvée");
        }

        // Vérifier si l'utilisateur a déjà signalé cette annonce
        if (hasUserAlreadyReported(announcement, reporter)) {
            throw new RuntimeException("Vous avez déjà signalé cette annonce");
        }

        // Vérifier si l'utilisateur essaie de signaler sa propre annonce
        if (announcement.getCreatedBy() != null &&
                announcement.getCreatedBy().getId().equals(reporter.getId())) {
            throw new RuntimeException("Vous ne pouvez pas signaler votre propre annonce");
        }

        Report report = Report.builder()
                .announcement(announcement)
                .reporter(reporter)
                .reason(reason)
                .status(ReportStatus.PENDING) // Correction ici
                .reportedAt(LocalDateTime.now())
                .build();
        Report savedReport = reportRepository.save(report);


        Notification notif = new Notification();
        notif.setUtilisateur(userService.getAdminUser()); // Méthode pour récupérer l’admin
        notif.setType(NotificationType.ANNONCE);
        notif.setTitre("Annonce signalée");
        notif.setMessage("L’annonce '" + announcement.getTitre() + "' a été signalée pour : " + reason);
        notif.setLien("/admin/gerer_report/reports" + savedReport.getId()); // lien vers la gestion
        notificationService.creerNotificationSimple(notif);
        return savedReport ;
    }

    public boolean hasUserAlreadyReported(Announcement announcement, utilisateur reporter) {
        return reportRepository.findByAnnouncementAndReporter(announcement, reporter).isPresent();
    }

    public List<Report> getUserReports(utilisateur user) {
        return reportRepository.findByReporter(user);
    }

    public List<Report> getAnnouncementReports(Announcement announcement) {
        return reportRepository.findByAnnouncement(announcement);
    }

    public Long getReportCountForAnnouncement(Announcement announcement) {
        return reportRepository.countByAnnouncement(announcement);
    }

    @Transactional
    public void updateReportStatus(Long reportId, ReportStatus status) { // Correction ici
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Signalement non trouvé"));

        report.setStatus(status);
        reportRepository.save(report);
    }

    public List<Report> getAllPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING); // Correction ici
    }

    // Méthode pour vérifier si une annonce a été signalée par l'utilisateur actuel
    public boolean hasCurrentUserReported(Long announcementId) {
        utilisateur user = userService.getConnectedUser();
        if (user == null) return false;

        Announcement announcement = announcementService.getById(announcementId);
        if (announcement == null) return false;

        return hasUserAlreadyReported(announcement, user);
    }

    public Report getAnnouncementReportsById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Signalement non trouvé"));
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Announcement annonce = announcementService.getById(announcementId);
        if (annonce != null) {
            announcementService.deleteAnnouncement(announcementId);
        }
    }

    @Transactional
    public void addAdminComment(Long reportId, String comment) {
        Report report = getAnnouncementReportsById(reportId);
        report.setAdminComment(comment);
        reportRepository.save(report);
    }
    public long getAnnouncementReportCount(Long announcementId) {
        return reportRepository.countReportsByAnnouncement(announcementId);
    }

    public List<Report> getAllAnnouncementReports() {
        return reportRepository.findAll();
    }
    @Transactional
    public void sendWarningForAnnouncement(Long reportId) {
        Report report = getAnnouncementReportsById(reportId);

        utilisateur annonceur = report.getAnnouncement().getCreatedBy();

        Notification notif = new Notification();
        notif.setUtilisateur(annonceur);
        notif.setType(NotificationType.WARNING);
        notif.setTitre("⚠️ Avertissement");
        notif.setMessage("Votre annonce '" + report.getAnnouncement().getTitre() +
                "' a été signalée. Veuillez respecter les règles de la plateforme.");
        notif.setDateCreation(LocalDateTime.now());
        notif.setLue(false);

        notificationService.creerNotificationSimple(notif);

        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
    }
    @Transactional
    public void supprimerAnnonceEtResoudre(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Signalement introuvable"));

        Announcement annonce = report.getAnnouncement();
        if (annonce != null) {
            announcementService.deleteAnnouncement(annonce.getId());
        }

        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
    }

}