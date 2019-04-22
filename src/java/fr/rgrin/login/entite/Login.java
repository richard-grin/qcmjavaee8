package fr.rgrin.login.entite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Email;

/**
 * Utilisateur de l'application.
 *
 * @author richard
 */
@NamedQuery(name = "Login.findByEmail",
        query = "SELECT l FROM Login l where l.email = :email")
@NamedQuery(name = "Login.getAll", query = "select l from Login l")
@NamedQuery(name = "Login.getAllWithGroups", 
        query = "select distinct l from Login l join fetch l.groupes")
@NamedQuery(name = "Login.findByNom",
        query = "select l from Login l where l.login = :nomLogin")
@NamedQuery(name = "Login.findGroupesUtilisateur",
        query = "select g from Login l join l.groupes g where l.login = :nomLogin")
@Entity
public class Login implements Serializable {

  @Id
  @Column(length = 50)
  private String login;
  @Column(name = "MOT_DE_PASSE", length = 160)
  private String motDePasse;
  @Column(length = 255)
  @Email
  private String email;

  /**
   * Statut du login. "email" après l'envoi d'un email de demande de
   * confirmation ; "valide" seulement après le clic de l'utilisateur sur le
   * lien de l'email qu'il a reçu.
   */
  @Column(length = 10)
  private String statut;
  /**
   * Code aléatoire envoyé envoyé avec l'email.
   */
  private String code;

  @ManyToMany
  @JoinTable(
          name = "login_groupe",
          joinColumns = @JoinColumn(name = "LOGIN"),
          inverseJoinColumns = @JoinColumn(name = "ID_GROUPE")
  )
  private List<Groupe> groupes = new ArrayList<>();

  public Login(String login, String motDePasse) {
    this.login = login;
    this.motDePasse = motDePasse;
  }

  public Login() {
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public void addGroupe(Groupe groupe) {
    this.groupes.add(groupe);
    groupe.addUtilisateur(this);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public String getMotDePasse() {
    return motDePasse;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public String getStatut() {
    return statut;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public List<Groupe> getGroupes() {
    return groupes;
  }

  public void setGroupes(List<Groupe> groupes) {
    this.groupes = groupes;
  }

}
