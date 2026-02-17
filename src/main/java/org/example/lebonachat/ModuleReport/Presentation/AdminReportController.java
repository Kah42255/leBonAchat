package org.example.lebonachat.ModuleReport.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleReport.Metier.Report;
import org.example.lebonachat.ModuleReport.Metier.UserReport;
import org.example.lebonachat.ModuleReport.Metier.ReportStatus;
import org.example.lebonachat.ModuleReport.Service.ReportService;
import org.example.lebonachat.ModuleReport.Service.UserReportService;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gerer_report")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;
    private final UserReportService userReportService;
    private final UserService userService;

    // ⚡ Liste tous les reports pour affichage
    @GetMapping("/reports")
    public String listReports(Model model) {
        List<Report> reports = reportService.getAllAnnouncementReports();
        for (Report r : reports) {
            long count = reportService.getAnnouncementReportCount(
                    r.getAnnouncement().getId()
            );
            r.setReportCount(count);
        }

        List<UserReport> userReports = userReportService.getAllUserReports();
        for (UserReport u : userReports) {
            long count = userReportService.getUserReportCount(
                    u.getReportedUser().getId()
            );
            u.setReportCount(count);
        }

        model.addAttribute("announcementReports", reports);
        model.addAttribute("userReports", userReports);

        return "admin/reports";
    }

    // ⚠️ Envoyer avertissement pour une annonce
    @PostMapping("/annonce/avertir")
    public String warnAnnouncement(@RequestParam Long reportId,
                                   RedirectAttributes redirectAttributes) {
        try {
            reportService.sendWarningForAnnouncement(reportId);
            redirectAttributes.addFlashAttribute("success", "Avertissement envoyé et signalement résolu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/gerer_report/reports";
    }

    // ⚠️ Supprimer l'annonce signalée
    @PostMapping("/annonce/supprimer")
    public String deleteAnnouncement(@RequestParam Long reportId,
                                     RedirectAttributes redirectAttributes) {
        try {
            reportService.supprimerAnnonceEtResoudre(reportId);
            redirectAttributes.addFlashAttribute("success", "Annonce supprimée et signalement résolu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/gerer_report/reports";
    }

    // ⚠️ Envoyer avertissement pour un utilisateur
    @PostMapping("/user/avertir")
    public String warnUser(@RequestParam Long userReportId,
                           RedirectAttributes redirectAttributes) {
        try {
            userReportService.sendWarningForUser(userReportId);
            redirectAttributes.addFlashAttribute("success", "Avertissement envoyé et signalement résolu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/gerer_report/reports";
    }

    // ⚠️ Supprimer l'utilisateur signalé
    @PostMapping("/user/supprimer")
    public String deleteUser(@RequestParam Long userReportId,
                             RedirectAttributes redirectAttributes) {
        try {
            userReportService.supprimerUtilisateurEtResoudre(userReportId);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé et signalement résolu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/gerer_report/reports";
    }

}
