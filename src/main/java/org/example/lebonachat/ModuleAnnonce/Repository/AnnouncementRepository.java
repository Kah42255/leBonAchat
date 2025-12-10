package org.example.lebonachat.ModuleAnnonce.Repository;

import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByTitreContainingIgnoreCase(String keyword);

    List<Announcement> findByVilleContainingIgnoreCase(String ville);
}
