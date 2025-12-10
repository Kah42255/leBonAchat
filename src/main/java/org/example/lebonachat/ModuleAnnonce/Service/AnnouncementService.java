package org.example.lebonachat.ModuleAnnonce.Service;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Repository.AnnouncementRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository repo;
    private final UserService userService; // INJECTION

    public List<Announcement> getAll() {
        return repo.findAll();
    }

    public Announcement getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Announcement> search(String keyword) {
        return repo.findByTitreContainingIgnoreCase(keyword);
    }

    public List<Announcement> filterByCity(String city) {
        return repo.findByVilleContainingIgnoreCase(city);
    }

    public List<Announcement> filterByCategory(Long categoryId) {
        return repo.findByCategoryId(categoryId);
    }

    // 🔐 VERSION SÉCURISÉE DE save()
    public Announcement save(Announcement annonce) {

        // récupérer l'utilisateur connecté
        utilisateur user = userService.getConnectedUser();

        // assigner l'utilisateur comme créateur de l'annonce
        annonce.setCreatedBy(user);

        return repo.save(annonce);
    }
}
