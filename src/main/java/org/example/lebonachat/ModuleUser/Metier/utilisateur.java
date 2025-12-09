package org.example.lebonachat.ModuleUser.Metier;


import jakarta.persistence.*;
import org.example.lebonachat.ModuleUser.Metier.Enum.Role;

@Entity
@Table(name = "users")
public class utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "num_tel")
    private String numTel;

    public utilisateur() {
    }

    public utilisateur(Long id, String nom, String prenom, String email,
                String password, Role role, String numTel) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.numTel = numTel;
    }

    // Getters / Setters

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}
