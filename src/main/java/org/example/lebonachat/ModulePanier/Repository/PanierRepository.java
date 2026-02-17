package org.example.lebonachat.ModulePanier.Repository;



import org.example.lebonachat.ModulePanier.Metier.Panier;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUtilisateur(utilisateur utilisateur);
}
