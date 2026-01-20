package org.example.lebonachat.ModuleReport.Repository;

import org.example.lebonachat.ModuleReport.Metier.Report;
import org.example.lebonachat.ModuleReport.Metier.ReportStatus; // Import ajouté
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByAnnouncement(Announcement announcement);

    List<Report> findByReporter(utilisateur reporter);

    List<Report> findByStatus(ReportStatus status); // Changé de String à ReportStatus

    Optional<Report> findByAnnouncementAndReporter(Announcement announcement, utilisateur reporter);

    Long countByAnnouncement(Announcement announcement);

    Long countByAnnouncementAndStatus(Announcement announcement, ReportStatus status); // Correction ici
}