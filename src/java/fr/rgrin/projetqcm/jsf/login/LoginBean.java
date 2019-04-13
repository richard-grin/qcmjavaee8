package fr.rgrin.projetqcm.jsf.login;

import fr.rgrin.projetqcm.jsf.Connexion;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.security.enterprise.AuthenticationStatus.SEND_CONTINUE;
import static javax.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;
import javax.servlet.ServletException;

/**
 * Backing bean pour la page de login avec un formulaire personnalisé (custom).
 *
 * @author grin
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean {

  @Inject
  private SecurityContext securityContext;
  @Inject
  private FacesContext facesContext;
  @Inject
  private ExternalContext externalContext;
//  @Inject
//  private AccueilController accueilController;
  @Inject
  private Principal principal;
  @Inject
  private HttpServletRequest request;

//  @Inject
//  private LoginFacade loginFacade;
  private String nom;
  private String motDePasse;

  /**
   * Get the value of motDePasse
   *
   * @return the value of motDePasse
   */
  public String getMotDePasse() {
    return motDePasse;
  }

  /**
   * Set the value of motDePasse
   *
   * @param motDePasse new value of motDePasse
   */
  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  /**
   * Get the value of nom
   *
   * @return the value of nom
   */
  public String getNom() {
    return nom;
  }

  /**
   * Set the value of nom
   *
   * @param nom new value of nom
   */
  public void setNom(String nom) {
    this.nom = nom;
  }

  public void login() {
    Credential credential
            = new UsernamePasswordCredential(nom, new Password(motDePasse));
    AuthenticationStatus status = securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            withParams().credential(credential).rememberMe(true));
    if (status.equals(SEND_CONTINUE)) { // ??**??
      // Récupère les infos sur le login et le range dans le bean de porté
      // session accueilController.
//      String nomUtilisateur = nom;
//              = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
      // Récupère le login et la personne associée (mode eager pour
      // les associations N-1).
//      if (nomUtilisateur != null) {
//      Login login = loginFacade.find(nom);
//      accueilController.setLogin(login);
      facesContext.responseComplete();
    } else if (status.equals(SEND_FAILURE)) {
      addError(facesContext, "Nom / mot de passe incorrects");
    }
  }

  public String deconnect() {
    try {
      request.logout();
    } catch (ServletException ex) {
      Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
    }
    request.getSession().invalidate();
//    System.out.println("********DECONNEXION !!!!");
//    try {
//      ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
//    } catch (ServletException ex) {
//      Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
//    }
    return "/index";
  }

  public String getNomUtilisateur() {
    return principal.getName();
  }

  /**
   * Ajoute une erreur à afficher dans la page de login.
   *
   * @param facesContext
   * @param authentication_failed
   */
  private void addError(FacesContext facesContext,
          String message) {
    facesContext.addMessage(
            null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    message,
                    null));
  }

//  /**
//   * Exécuté quand l'utilisateur a oublié son mot de passe.
//   *
//   * @return
//   */
//  public String oubliMotDePasse() {
//    return "/login/oubliMotDePasse";
//  }
//
//  public String inscription() {
//    return "/login/inscription?faces-redirect=true";
//  }
}
