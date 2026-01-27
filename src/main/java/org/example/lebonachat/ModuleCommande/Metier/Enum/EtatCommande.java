package org.example.lebonachat.ModuleCommande.Metier.Enum;

public enum EtatCommande {
    EST_ANNULEE,
    EN_ATTENTE,
    PAYEE,
    PARTIELLE,      // certains annonceurs ont refusé
    COMPLETE;
}
