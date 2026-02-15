package org.example.lebonachat.ModuleAnnonce.Repository;

import org.example.lebonachat.ModuleAnnonce.Metier.Candidature;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByAnnonce(Announcement annonce);
}
