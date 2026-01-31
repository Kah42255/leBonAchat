package org.example.lebonachat.ModuleReport.Presentation;


import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleReport.Metier.Report;
import org.example.lebonachat.ModuleReport.Metier.UserReport;
import org.example.lebonachat.ModuleReport.Service.ReportService;
import org.example.lebonachat.ModuleReport.Service.UserReportService;
import org.example.lebonachat.ModuleUser.Service.UserService;
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

    // ⚡ Affichage d'un report (annonce ou utilisateur)
    @GetMapping({"/annonce/{reportId}", "/user/{userReportId}"})
    public String showReport(@PathVariable(required = false) Long reportId,
                             @PathVariable(required = false) Long userReportId,
                             Model model) {

        if (reportId != null) {
            Report report = reportService.getAnnouncementReportsById(reportId);
            model.addAttribute("report", report);
            model.addAttribute("type", "annonce");
        } else if (userReportId != null) {
            UserReport userReport = userReportService.getUserReportById(userReportId);
            model.addAttribute("report", userReport);
            model.addAttribute("type", "user");
        }

        return "admin/gerer_report";

    }

    // ⚡ Supprimer l'annonce signalée
    @PostMapping("/annonce/supprimer")
    public String deleteAnnouncement(@RequestParam Long reportId,
                                     RedirectAttributes redirectAttributes) {
        try {
            Report report = reportService.getAnnouncementReportsById(reportId);
            reportService.deleteAnnouncement(report.getAnnouncement().getId());
            redirectAttributes.addFlashAttribute("success", "Annonce supprimée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/gerer_report";
    }

    // ⚡ Supprimer l'utilisateur signalé
    @PostMapping("/user/supprimer")
    public String deleteUser(@RequestParam Long userReportId,
                             RedirectAttributes redirectAttributes) {
        try {
            UserReport report = userReportService.getUserReportById(userReportId);
            userService.deleteUser(report.getReportedUser().getId());
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/gerer_report";
    }

    // ⚡ Ajouter un commentaire admin
    @PostMapping("/commenter")
    public String addComment(@RequestParam Long reportId,
                             @RequestParam String adminComment,
                             RedirectAttributes redirectAttributes) {
        try {
            reportService.addAdminComment(reportId, adminComment);
            redirectAttributes.addFlashAttribute("success", "Commentaire ajouté.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reports";
    }
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

        return "/admin/reports";
    }



}
