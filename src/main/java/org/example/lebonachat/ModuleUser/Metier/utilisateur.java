package org.example.lebonachat.ModuleUser.Metier;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.lebonachat.ModuleUser.Metier.Enum.Role;

@Entity
@Table(name = "users")
public class utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @NotBlank(message = "Le prenom est obligatoire")
    private String prenom;
    @NotBlank(message = "Le email est obligatoire")
    /*@Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message =" l'email et incorect"
    )*/
    @Email(message="email est incorrect")
    private String email;


    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
            message = "Le mot de passe doit contenir une majuscule, une minuscule et un chiffre"
    )
    @NotBlank(message = "Le Mot de passe est obligatoire")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "numTel")
    @NotBlank(message = "Le Mot de passe est obligatoire")
    @Pattern(
            regexp = "^0[5-7][0-9]{8}$",
            message ="ce numero est invalide"
    )
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public boolean isAdmin() {
        return this.role == Role.ROLE_ADMIN;
    }


}
