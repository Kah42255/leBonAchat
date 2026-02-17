package org.example.lebonachat.ModuleAnnonce.Repository;

import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Metier.Enum.EtatAnnonce;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // Recherche simple
    List<Announcement> findByTitreContainingIgnoreCase(String keyword);

    List<Announcement> findByVilleContainingIgnoreCase(String ville);

    List<Announcement> findByCategoryId(Long categoryId);

    // Filtrage par état
    List<Announcement> findByEtat(EtatAnnonce etat);

    List<Announcement> findByEtatAndCategoryId(EtatAnnonce etat, Long categoryId);

    List<Announcement> findByEtatAndTitreContainingIgnoreCase(EtatAnnonce etat, String keyword);

    List<Announcement> findByCreatedBy(utilisateur user);
}
