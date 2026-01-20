package org.example.lebonachat.ModuleReport.Service;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.example.lebonachat.ModuleReport.Metier.Report;
import org.example.lebonachat.ModuleReport.Metier.ReportStatus; // Import ajouté
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

        return reportRepository.save(report);
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
}