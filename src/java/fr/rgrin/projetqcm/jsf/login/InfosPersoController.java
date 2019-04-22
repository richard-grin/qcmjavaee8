package fr.rgrin.projetqcm.jsf.login;

import fr.rgrin.login.entite.Login;
import fr.rgrin.projetqcm.ejb.login.LoginFacade;
import fr.rgrin.projetqcm.util.HashMdp;
import java.io.Serializable;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;

/**
 * Backing bean pour la page informationsPerso.xhtml qui gère les infos sur le
 * login : email, mot de passe, infos sur la personne qui correspond à cet
 * email.
 *
 * Remarque : la portée request suffit puisqu'on récupère le login actuel à
 * chaque création d'un nouveau bean.
 *
 * @author richard
 */
@Named(value = "infosPersoController")
@ViewScoped
//@RequestScoped
public class InfosPersoController implements Serializable {

  @Inject
  private Principal principal;

  private Login login;
  // true si on vient d'éditer les infos sur le login (mot de passe ou email)
  // Utilisé pour savoir quels messages on doit afficher dans la page.
  private boolean editLogin;
  @Inject
  private LoginFacade loginFacade;
  @Inject
  private HashMdp passwordHash;
  @Inject
  private SecurityContext securityContext;

  private String motDePasse, motDePasse2;
  private boolean changementEmail;

  /**
   * Creates a new instance of InfosPersoController
   */
  public InfosPersoController() {
  }

  @PostConstruct
  private void init() {
    String nomLogin = principal.getName();
//    System.out.println("+=+=+= Nom de l'utilisateur dans getLogin() de AcceuilController=" + nomLogin);
    this.login = loginFacade.findByNom(nomLogin);
  }

  public Login getLogin() {
//    login = accueilController.getLogin();
//    if (login == null) {
//      login = loginFacade.find(securityContext.getCallerPrincipal().getName());
//      System.out.println("**********Nom de l'utilisateur : " + login.getLogin());
//    }
//      login = loginFacade.find(accueilController.getLogin().getLogin());
//    if (login == null) {
//      // Récupérer le login
//      String nomUtilisateur =
//              FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
//      // Récupère le login et la personne associée (mode eager pour
//      // les associations N-1).
//      if (nomUtilisateur != null) {
//        login = loginFacade.find(nomUtilisateur);
//      }
//    }
    return login;
  }

  public String getMotDePasse() {
    return motDePasse;
  }

  public String getMotDePasse2() {
    return motDePasse2;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public void setMotDePasse2(String motDePasse2) {
    this.motDePasse2 = motDePasse2;
  }

  public boolean isEditLogin() {
    return editLogin;
  }

  /**
   * Si un mot de passe a été saisi. Validateur pour le 2ème mot de passe.
   *
   * @param context
   * @param composant
   * @param valeur
   */
  public void validateMotsDePasse(FacesContext context,
          UIComponent composant, Object valeur) {
    // TODO:
    // Comment faire pour effacer le message d'erreur qui suit le 2ème
    // mot de passe dans le cas où
    // tout est OK et que l'utilisateur a rectifié en tapant en dernier
    // le 1er mot de passe ?
    // Récupère la valeur du 1er mot de passe :
    UIInput composantMdp1 = (UIInput) composant.findComponent("motDePasse");
//    System.out.println("====Le composant 1:" + composantMdp1);
    Object o1 = composantMdp1.getLocalValue();

//    UIInput composantMdp10 = (UIInput)composant.findComponent("login");
//    Object o10 = composantMdp10.getLocalValue();
//    System.out.println("Valeur locale du composant 10:" + o10 + "Valeur du composant 10:" + composantMdp10.getValue());
//    UIInput composantMdp11 = (UIInput)composant.findComponent("email");
//    Object o11 = composantMdp11.getLocalValue();
//    System.out.println("Valeur locale du composant 11:" + o11 + "Valeur du composant 11:" + composantMdp11.getValue());
    if ((o1 == null || o1.equals("")) && (valeur == null || valeur.equals(""))) {
      // Aucun mot de passe saisi ; tout va bien
      return;
    }
    // Au moins un des 2 mots de passe n'est pas vide.
    // Il doit être égal à l'autre mot de passe.
    if (o1 != null && o1.equals(valeur)) {
      // Tout va bien
      return;
    }
//    System.out.println("Valeur du composant 1:" + composantMdp1.getValue());
//    System.out.println("Les 2 mots de passe : o1 =" + o1 + ";valeur=" + valeur);
    // Problème !
    FacesMessage message
            = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Mots de passes différents",
                    " Vous n'avez pas tapé 2 fois le même mot de passe !");
    throw new ValidatorException(message);
  }

  /**
   * Enregistre les modifications du login.
   *
   * @return
   */
  public String enregistrerModifsLogin() {
    // Est-ce que le mot de passe a été modifié ?
    if (!((motDePasse == null || motDePasse.equals(""))
            && (motDePasse2 == null || motDePasse2.equals("")))) {
      // Mot de passe modifié
      // Si on est ici c'est que la vérification de l'égalité des mots
      // de passe a déjà été faite par les validateurs.
      // On enregistre donc le nouveau mot de passe.
//      login.setMotDePasse(Sha256.hashToStringBase64(motDePasse));
      login.setMotDePasse(passwordHash.generate(motDePasse));
      FacesMessage message = new FacesMessage("Nouveau mot de passe enregistré");
      FacesContext.getCurrentInstance().addMessage(null, message);
    }
    // Enregistre les modifications dans la base de données.
    loginFacade.edit(login);
    if (changementEmail) {
      FacesMessage message
              = new FacesMessage("Nouvel email enregistré");
      FacesContext.getCurrentInstance().addMessage(null, message);
    }
    editLogin = true;
    // Reste sur la même page
    return null;
  }

//  /**
//   * Remarque : je dois passer par accueilController pour rafraichir ce qui est
//   * affiché par le template.
//   *
//   * @return
//   */
//  public String enregistrerModifsPersos() {
////    System.out.println("==============enregistrerModifsPersos: login=" + login);
////    System.out.println("==============enregistrerModifsPersos: personne=" + login.getPersonne() + "-prénom=" + login.getPersonne().getPrenom());
//    editLogin = false;
////    accueilController.setLogin(login);
////    FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("nom"); // Ne marche pas pour rafraichir le template !!
//    FacesMessage message
//            = new FacesMessage("Nouvelle information personnelle enregistrée");
//    FacesContext.getCurrentInstance().addMessage(null, message);
//    return "informationsPerso";
//  }
  public void changementEmail(ValueChangeEvent event) {
    changementEmail = true;
  }
}
