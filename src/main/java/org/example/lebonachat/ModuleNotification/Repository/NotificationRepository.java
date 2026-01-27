package org.example.lebonachat.ModuleNotification.Repository;

import org.example.lebonachat.ModuleNotification.Metier.Notification;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Récupérer les notifications d'un utilisateur avec la commande (fetch join)
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.commande WHERE n.utilisateur = :user ORDER BY n.dateCreation DESC")
    List<Notification> findByUtilisateurWithCommande(@Param("user") utilisateur user);

    long countByUtilisateurAndLueFalse(utilisateur user);

}
