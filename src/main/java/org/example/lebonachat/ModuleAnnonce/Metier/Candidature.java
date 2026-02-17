package org.example.lebonachat.ModuleAnnonce.Metier;

import jakarta.persistence.*;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import java.time.LocalDateTime;

@Entity
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private utilisateur candidat;

    @ManyToOne
    private Announcement annonce;

    private String cvFileName;          // Original PDF name

    @Lob
    private byte[] cvData;             // PDF bytes

    private LocalDateTime dateEnvoi;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public utilisateur getCandidat() { return candidat; }
    public void setCandidat(utilisateur candidat) { this.candidat = candidat; }

    public Announcement getAnnonce() { return annonce; }
    public void setAnnonce(Announcement annonce) { this.annonce = annonce; }

    public String getCvFileName() { return cvFileName; }
    public void setCvFileName(String cvFileName) { this.cvFileName = cvFileName; }

    public byte[] getCvData() { return cvData; }
    public void setCvData(byte[] cvData) { this.cvData = cvData; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
}
