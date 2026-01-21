package org.example.lebonachat.ModuleReport.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleReport.Service.UserReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserReportController {

    private final UserReportService userReportService;

    @PostMapping("/report/submitUser")
    public String submitUserReport(@RequestParam Long userId,
                                   @RequestParam String reason,
                                   RedirectAttributes redirectAttributes) {
        try {
            userReportService.reportUser(userId, reason);
            redirectAttributes.addFlashAttribute("success", "Utilisateur signalé avec succès.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profil/" + userId; // retourne sur le profil de l'utilisateur signalé
    }
}
