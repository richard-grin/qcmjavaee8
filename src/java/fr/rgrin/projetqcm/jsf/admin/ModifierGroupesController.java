package fr.rgrin.projetqcm.jsf.admin;

import fr.rgrin.login.entite.Groupe;
import fr.rgrin.login.entite.Login;
import fr.rgrin.projetqcm.ejb.login.GroupeFacade;
import fr.rgrin.projetqcm.ejb.login.LoginFacade;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;

/**
 * Backing bean pour la page qui permet à un administrateur de modifier le
 * groupe d'un utilisateur.
 *
 * @author grin
 */
@Named(value = "modifierGroupes")
@RequestScoped
public class ModifierGroupesController {
  private DataModel<Login> logins;
  private List<Login> loginList;

  private List<Groupe> groupesDuLogin;

  @EJB
  private GroupeFacade groupeFacade;

  @EJB
  private LoginFacade loginFacade;

  @Inject
  private Principal principal;

  public ModifierGroupesController() {
  }

  public List<Login> getAllLogins() {
    return loginFacade.findAll();
  }

  public DataModel<Login> getAllLoginsWithGroups() {
    if (loginList == null) {
      loginList = loginFacade.findAllWithGroups();
      logins = new ListDataModel<>(loginList);
    }
    return logins;
  }

  public List<Groupe> getAllGroupes() {
    return groupeFacade.findAll();
  }

  public List<Groupe> getGroupesDuLogin() {
    return loginFacade.findGroupesUtilisateur(logins.getRowData().getLogin());
  }

  public void setGroupesDuLogin(List<Groupe> groupesDuLogin) {
    this.groupesDuLogin = groupesDuLogin;
  }

  public List<Groupe> getGroupesUtilisateur(Login login) {
    return groupeFacade.findGroupeUtilisateur(login);
  }

  /**
   * Utilisé pour la complétion des noms de groupe. On retire les groupe
   * "inscrit" qui est automatiquement attribué à tout nouvel utilisateur qui
   * n'a pas d'autres groupes.
   *
   * @param texte
   * @return
   */
  public List<Groupe> complete(String texte) {
    System.out.println("Méthode COMPLETE *******");
    return getAllGroupes().stream()
            .filter((Groupe groupe) -> (!groupe.getNom().toLowerCase().equals("inscrit")) && groupe.getNom().toLowerCase().startsWith(texte.toLowerCase()))
            .collect(Collectors.toList());
  }

  public String enregistrer(Login login) {
    login.setGroupes(groupesDuLogin);
    System.out.println("******* Les groupes de " + login.getLogin() + " : ");
    for (Groupe groupe : login.getGroupes()) {
      System.out.println("le groupe " + groupe.getNom());
    }
    loginFacade.edit(login);
    return null;
  }
}
