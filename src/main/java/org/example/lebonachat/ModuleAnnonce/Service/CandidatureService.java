package org.example.lebonachat.ModuleAnnonce.Service;

import org.example.lebonachat.ModuleAnnonce.Metier.Candidature;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Repository.CandidatureRepository;
import org.example.lebonachat.ModuleAnnonce.Repository.AnnouncementRepository;
import org.example.lebonachat.ModuleNotification.Service.NotificationService;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Repository.userRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class CandidatureService {

    private final NotificationService notificationService;
    private final CandidatureRepository candidatureRepository;
    private final AnnouncementRepository annonceRepository;
    private final userRepository userRepository;

    public CandidatureService(CandidatureRepository candidatureRepository,
                              AnnouncementRepository annonceRepository,
                              userRepository userRepository,
                              NotificationService notificationService) {
        this.candidatureRepository = candidatureRepository;
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // Sauvegarde une candidature avec PDF et envoie notification au créateur
    public Candidature saveCandidature(Long annonceId, String candidatEmail, MultipartFile cvFile) throws IOException {
        // 1️⃣ Récupérer l'annonce
        Announcement annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        // 2️⃣ Récupérer l'utilisateur déjà persistant
        utilisateur candidat = userRepository.findByEmail(candidatEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 3️⃣ Créer la candidature
        Candidature candidature = new Candidature();
        candidature.setAnnonce(annonce);
        candidature.setCandidat(candidat);
        candidature.setCvFileName(cvFile.getOriginalFilename());
        candidature.setCvData(cvFile.getBytes());
        candidature.setDateEnvoi(LocalDateTime.now());

        // 4️⃣ Sauvegarder la candidature
        Candidature saved = candidatureRepository.save(candidature);

        // 5️⃣ Créer notification pour le créateur de l'annonce
        utilisateur createur = annonce.getCreatedBy();  // Assure-toi que getCreatedBy() existe
        if (createur != null) {
            String message = "Nouveau CV reçu pour votre offre : " + annonce.getTitre();
            String lien = "/candidature/liste/" + annonce.getId();
            notificationService.creerNotificationAnnonce(createur, "Nouveau CV reçu", message, lien);
        }

        return saved;
    }

    // Liste des candidatures pour une annonce
    public List<Candidature> getCandidaturesForAnnonce(Long annonceId) {
        Announcement annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));
        return candidatureRepository.findByAnnonce(annonce);
    }

    // Récupérer une candidature par id
    public Candidature getCandidatureById(Long id) {
        return candidatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
    }
}
