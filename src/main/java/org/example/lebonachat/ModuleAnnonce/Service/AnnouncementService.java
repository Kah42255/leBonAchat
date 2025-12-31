package org.example.lebonachat.ModuleAnnonce.Service;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleAnnonce.Repository.AnnouncementRepository;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.example.lebonachat.ModuleUser.Service.UserService;
import org.jspecify.annotations.Nullable;
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

    // 🔐 VERSION SÉCURISÉE DE save()
    public Announcement save(Announcement annonce) {

        // récupérer l'utilisateur connecté
        utilisateur user = userService.getConnectedUser();

        // assigner l'utilisateur comme créateur de l'annonce
        annonce.setCreatedBy(user);

        return repo.save(annonce);
    }

    public List<Announcement> filterByCategory(Long categoryId) {

        // si aucune catégorie n'est choisie, renvoyer toutes les annonces
        if (categoryId == null || categoryId == 0) {
            return repo.findAll();
        }

        return repo.findByCategoryId(categoryId);
    }
    public List<Announcement> getByUser(utilisateur user) {
        return repo.findByCreatedBy(user);
    }
    public List<Announcement> getByCategoryId(Long categoryId) {
        return repo.findByCategoryId(categoryId);
    }
}
