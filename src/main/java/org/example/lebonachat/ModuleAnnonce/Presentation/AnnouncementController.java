package org.example.lebonachat.ModuleAnnonce.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Service.AnnouncementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    @GetMapping("/annonces")
    public String list(Model model) {
        model.addAttribute("annonces", service.getAll());
        return "annonces";
    }

    @GetMapping("/annonce/new")
    public String createForm(Model model) {
        model.addAttribute("annonce", new Announcement());
        return "annonce_form";
    }

    @PostMapping("/annonce/save")
    public String save(@ModelAttribute Announcement annonce) {
        service.save(annonce);
        return "redirect:/annonces";
    }

    @GetMapping("/annonce/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("annonce", service.getById(id));
        return "annonce_details";
    }

    @GetMapping("/annonce/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/annonces";
    }
}
