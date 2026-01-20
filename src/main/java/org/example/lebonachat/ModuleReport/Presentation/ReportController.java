package org.example.lebonachat.ModuleReport.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleReport.Metier.ReportStatus; // Import ajouté
import org.example.lebonachat.ModuleReport.Service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{announcementId}")
    public String showReportForm(@PathVariable Long announcementId, Model model) {
        model.addAttribute("announcementId", announcementId);
        return "report_form";
    }

    @PostMapping("/submit")
    public String submitReport(@RequestParam Long announcementId,
                               @RequestParam String reason,
                               RedirectAttributes redirectAttributes) {
        try {
            reportService.reportAnnouncement(announcementId, reason);
            redirectAttributes.addFlashAttribute("success", "Annonce signalée avec succès. Nous allons l'examiner.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/annonce/" + announcementId;
    }

    // Endpoint pour l'admin
    @PostMapping("/admin/update/{reportId}")
    public String updateReportStatus(@PathVariable Long reportId,
                                     @RequestParam ReportStatus status, // Utilise ReportStatus
                                     RedirectAttributes redirectAttributes) {
        try {
            reportService.updateReportStatus(reportId, status);
            redirectAttributes.addFlashAttribute("success", "Statut du signalement mis à jour.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reports";
    }
}