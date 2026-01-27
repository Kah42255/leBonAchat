package org.example.lebonachat.ModuleCommande.Repository;

import org.example.lebonachat.ModuleCommande.Metier.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
}
