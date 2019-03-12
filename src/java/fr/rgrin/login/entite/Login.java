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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Utilisateur de l'application.
 * @author richard
 */
  @NamedQuery(name = "Login.findByEmail",
          query = "SELECT l FROM Login l where l.email = :email")
  @NamedQuery(name = "Login.getAll", query = "select l from Login l")
  @NamedQuery(name = "Login.findByNom", 
          query = "select l from Login l where l.login = :nomLogin")
@Entity
public class Login implements Serializable {

  @Id
  @Column(length = 50)
  private String login;
  @Column(name = "MOT_DE_PASSE", length = 160)
  private String motDePasse;
  @Column(length = 255)
  private String email;
  @ManyToMany
  @JoinTable(
          name="login_groupe",
          joinColumns=@JoinColumn(name="LOGIN"),
          inverseJoinColumns=@JoinColumn(name="ID_GROUPE")
  )
//  @JoinColumn(
//          name="LOGIN",
//          referencedColumnName="ID_GROUPE")
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
  
}
