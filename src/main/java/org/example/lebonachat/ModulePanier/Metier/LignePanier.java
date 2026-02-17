package org.example.lebonachat.ModulePanier.Metier;



import jakarta.persistence.*;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;

@Entity
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Announcement annonce;

    private int quantite;
    public LignePanier(Announcement annonce, int quantite) {
        this.annonce = annonce;
        this.quantite = quantite;
    }
    @ManyToOne
    private Panier panier;
    public LignePanier() {
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public Long getId() {
        return id;
    }

    public Announcement getAnnonce() {
        return annonce;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAnnonce(Announcement annonce) {
        this.annonce = annonce;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

}
