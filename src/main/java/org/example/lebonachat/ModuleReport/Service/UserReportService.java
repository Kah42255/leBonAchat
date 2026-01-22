package org.example.lebonachat.ModuleReport.Service;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleReport.Metier.ReportStatus;
import org.example.lebonachat.ModuleReport.Metier.UserReport;
import org.example.lebonachat.ModuleReport.Repository.UserReportRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserService userService;

    public void reportUser(Long reportedUserId, String reason) {
        utilisateur reporter = userService.getConnectedUser(); // récupère l'utilisateur connecté
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
    }
}
