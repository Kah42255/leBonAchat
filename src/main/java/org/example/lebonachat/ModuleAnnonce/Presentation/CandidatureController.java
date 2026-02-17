package org.example.lebonachat.ModuleAnnonce.Presentation;

import jakarta.servlet.http.HttpServletResponse;
import org.example.lebonachat.ModuleAnnonce.Metier.Candidature;
import org.example.lebonachat.ModuleAnnonce.Service.CandidatureService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/candidature")
public class CandidatureController {

    private final CandidatureService candidatureService;

    public CandidatureController(CandidatureService candidatureService) {
        this.candidatureService = candidatureService;
    }

    // Liste des candidatures pour une annonce
    @GetMapping("/liste/{annonceId}")
    public String listCandidatures(@PathVariable Long annonceId, Model model) {
        List<Candidature> cvs = candidatureService.getCandidaturesForAnnonce(annonceId);
        model.addAttribute("candidatures", cvs);
        return "candidature/list";
    }

    // Téléchargement du CV
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCV(@PathVariable Long id, Principal principal) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Candidature c = candidatureService.getCandidatureById(id);
        if (c == null || c.getCvData() == null || c.getCvData().length == 0) {
            return ResponseEntity.notFound().build();
        }

        byte[] cvBytes = c.getCvData();
        String fileName = c.getCvFileName();
        if (fileName == null || fileName.isBlank()) {
            fileName = "cv_" + c.getId() + ".pdf";
        }

        // Encodage UTF-8 pour Chrome/Edge
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .contentLength(cvBytes.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(cvBytes);
    }

    @GetMapping("/download/test")
    public ResponseEntity<byte[]> testDownload() throws IOException {
        byte[] pdf = Files.readAllBytes(Paths.get("C:\\temp\\cv_test.pdf"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cv_test.pdf")
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    @GetMapping("/postuler/{annonceId}")
    public String showUploadPage(@PathVariable Long annonceId, Model model) {
        model.addAttribute("annonceId", annonceId);
        return "candidature/upload"; // formulaire upload CV
    }

    // Traiter le CV uploadé
    @PostMapping("/postuler/{annonceId}")
    public String submitCV(@PathVariable Long annonceId,
                           @RequestParam("cvFile") MultipartFile cvFile,
                           Principal principal) throws IOException {
        if (principal == null) {
            throw new RuntimeException("Utilisateur non connecté !");
        }

        String email = principal.getName();
        candidatureService.saveCandidature(annonceId, email, cvFile);

        return "redirect:/accueil";
    }

}
