package fr.rgrin.projetqcm.jsf.login;

import fr.rgrin.login.entite.Login;
import fr.rgrin.projetqcm.ejb.LoginFacade;
import fr.rgrin.projetqcm.util.EnvoyeurEmail;
import fr.rgrin.projetqcm.util.GenerateurMotDePasse;
import fr.rgrin.projetqcm.util.HashMdp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;


/**
 * Backing bean pour la page oubliMotDePasse.xhtml.
 * @author richard
 */
@Named(value = "oubliMotDePasseController")
@RequestScoped
public class OubliMotDePasseController {
  private String nomLogin;
  /**
   * Login qui correspond à ce nom de login.
   */
  private Login login;
  /**
   * true si on est dans la phase 2, après envoi du nouveau mot de passe.
   */
  private boolean phase2 = false;
  
  @EJB
  private LoginFacade loginFacade;
  @Inject
  private HashMdp passwordHash;
  
  @Inject
  private ExternalContext externalContext;
  
  /** Creates a new instance of OubliMotDePasseController */
  public OubliMotDePasseController() {
  }

  public String getNomLogin() {
    return nomLogin;
  }

  public void setNomLogin(String nomLogin) {
    this.nomLogin = nomLogin;
  }

  public boolean isPhase2() {
    return phase2;
  }

  /**
   * Envoyer un email à l'utilisateur qui a oublié son mot de passe
   * avec un lien pour qu'il puisse saisir un nouveau mot de passe.
   */
  public void envoiEmail() {
    String motDePasseTemporaire = 
            new GenerateurMotDePasse().genererMotDePasse();
    try {
      // Envoi de l'email
      String nomJNDIPourEmail = externalContext.getInitParameter("fr.unice.EMAIL_JNDI_NAME");
      EnvoyeurEmail envoyeurEmail = new EnvoyeurEmail(nomJNDIPourEmail);
      String texteEmail = "Hi " + login.getLogin() + ","
              + "\nYou recently said that you had forgotten your password."
              + "\nHere is your new password:\n"
              + motDePasseTemporaire
              + "\nCheers,"
              + "\nThe OpenFormations Team";
      String sujetEmail = "New password";
      envoyeurEmail.envoyer(texteEmail, sujetEmail, login.getEmail());
      // Changement du mot de passe du login
      login.setMotDePasse(passwordHash.generate(motDePasseTemporaire));
      loginFacade.edit(login);
      phase2 = true;
    } catch (AddressException ex) {
      Logger.getLogger(OubliMotDePasseController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MessagingException ex) {
      Logger.getLogger(OubliMotDePasseController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NamingException ex) {
      // Ajouter un message d'erreur JSF
      // TODO
      Logger.getLogger(OubliMotDePasseController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  /**
   * Pour valider le nom de login saisi par l'utilisateur.
   * @param ctxt
   * @param composant
   * @param valeur 
   */
  public void validateLogin(FacesContext ctxt, UIComponent composant, 
          Object valeur) {
    // Vérifie que le login existe
    login = loginFacade.find(valeur);
    if (login == null) {
      // Génère un message d'erreur qui sera affiché près du login
      // saisi par l'utilisateur
      FacesMessage message = new FacesMessage("Ce login n'existe pas");
      throw new ValidatorException(message);
    }
  }
}
