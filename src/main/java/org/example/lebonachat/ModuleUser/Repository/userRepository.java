package org.example.lebonachat.ModuleUser.Repository;

import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface userRepository extends JpaRepository<utilisateur, Long> {
    Optional<utilisateur> findByEmail(String email);
}