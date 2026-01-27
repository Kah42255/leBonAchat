package org.example.lebonachat.ModuleCommande.Repository;

import org.example.lebonachat.ModuleCommande.Metier.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {}

