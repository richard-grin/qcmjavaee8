package fr.rgrin.projetqcm.jsf.login;

import fr.rgrin.login.entite.Login;
import fr.rgrin.projetqcm.ejb.LoginFacade;
import fr.rgrin.projetqcm.util.EnvoyeurEmail;
import fr.rgrin.projetqcm.util.HashMdp;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;

/**
 * Backing bean pour la page des nouvelles inscriptions.
 *
 * @author grin
 */
@Named(value = "inscriptionController")
@RequestScoped
public class InscriptionController {

  private Login login = new Login();
  private String motDePasse2;

  @Inject
  // Pour coder le mot de passe
  private HashMdp passwordHash;

  @EJB
  private LoginFacade loginFacade;

  @Inject
  private ExternalContext externalContext;

//  private String NOM_JNDI_EMAIL;

  /**
   * Creates a new instance of InscriptionController
   */
  public InscriptionController() {
  }

  public Login getLogin() {
    return login;
  }

//  public void setLogin(Login login) {
//    this.login = login;
//  }
  public String getMotDePasse2() {
    return motDePasse2;
  }

  /**
   * Set the value of mdp
   *
   * @param mdp new value of mdp
   */
  public void setMotDePasse2(String mdp) {
    this.motDePasse2 = mdp;
  }

  /**
   * Enregistrer un nouvel utilisateur. Appelée depuis la page JSF
   * inscription.xhtml.
   *
   * @return
   */
  public String enregistrer() {
    // Vérifications
    // Vérifier que le login (impossible) ou l'email (warning) n'existe pas déjà.
    // TODO

    // Enregister le nouvel utilisateur
    login.setStatut("email");
    // Repousser à la confirmation par email
//    login.addGroupe(groupeFacade.findByNom("inscrit"));
    // Passe le mot de passe en SHA-256 au dernier moment, juste avant
    // d'enregistrer le login dans la base de données.
    login.setMotDePasse(passwordHash.generate(login.getMotDePasse()));
    loginFacade.create(login);

    // Envoi email de demande de confirmation
    envoiEmailDemandeConfirmation();
    // Envoi sur une page d'accueil explicative avec menu.
    return "/login/apresInscription";
  }

  private void envoiEmailDemandeConfirmation() {
    try {
      /* Génération code et ajout à l'entité login.
       * Ce code ajoute à la sécurité : 
       * <ul>
       * <li>il est enregistré dans l'entité Login de l'utilisateur</li>
       * <li>il est envoyé avec l'URL du lien de l'email. Quand l'utilisateur
       * cliquera sur ce lien, la requête contiendra un paramètre 
       * qui aura ce code comme valeur.</li>
       * <li>à l'arrivée de la requête, la valeur de ce paramètre sera comparé
       * au code contenu dans l'entité Login.</li>
       * <li>la requête ne sera acceptée que si les 2 valeurs sont égales.</li>
       * </ul>
       */
      Random random = new Random(System.currentTimeMillis());
      long n = random.nextLong();
      String code = "" + n;
      int tailleCode = code.length();
      int max = 15;
      int tailleMax = tailleCode > max ? max : tailleCode;
      code = code.substring(0, tailleMax);
      // Envoi de l'email
      String lienConfirmation
              = "http://localhost:8080"
              + externalContext.getApplicationContextPath()
              + "/login/confirmation.xhtml?"
              + "id=" + login.getLogin()
              + "&email=" + login.getEmail()
              + "&cle=" + code;
      // Envoi de l'email
      String nomJNDIPourEmail = externalContext.getInitParameter("fr.unice.EMAIL_JNDI_NAME");
      EnvoyeurEmail envoyeurEmail = new EnvoyeurEmail(nomJNDIPourEmail);
//      EnvoyeurEmail envoyeurEmail = new EnvoyeurEmail(NOM_JNDI_EMAIL);
      String texteEmail = "Hi " + login.getLogin() + ","
              + "\nYou recently entered a new contact email address into OpenFormations."
              + "\nTo confirm your contact email, follow the link below:\n"
              + lienConfirmation
              + "\nCheers,"
              + "\nThe OpenFormations Team";
      String sujetEmail = "Confirm your registration";
      envoyeurEmail.envoyer(texteEmail, sujetEmail, login.getEmail());
      login.setCode(code);
      loginFacade.edit(login);
    } catch (AddressException ex) {
      Logger.getLogger(InscriptionController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MessagingException ex) {
      Logger.getLogger(InscriptionController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NamingException ex) {
      // Ajouter un message d'erreur JSF
      // TODO
      Logger.getLogger(InscriptionController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Vérifie que les 2 mots de passe sont bien les mêmes. Méthode listener de
   * l'événement postValidate de la page JSF inscription.xhtml
   *
   * @param event
   */
  public void validateMdps(ComponentSystemEvent event) {
    UIComponent source = event.getComponent();
    UIInput mdp1 = (UIInput) source.findComponent("motDePasse");
    UIInput mdp2 = (UIInput) source.findComponent("motDePasse2");

    String v1 = null, v2 = null;
    Object o1 = mdp1.getLocalValue();
    Object o2 = mdp2.getLocalValue();
    System.out.println("mdp1=" + o1 + ";mdp2=" + o2 + ".");
    if (o1 != null) {
      v1 = o1.toString();
    }
    if (o2 != null) {
      v2 = o2.toString();
    }
    if (v1 != null && !v1.equals(v2)) {
      ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
      String message = bundle.getString("mdpsDifferents");
      // Ne marche pas ; dommage !
//      throw new ValidatorException(new FacesMessage(message));
      FacesContext context = FacesContext.getCurrentInstance();
      FacesMessage fm = new FacesMessage(message);
      fm.setSeverity(FacesMessage.SEVERITY_ERROR);
      context.addMessage(source.getClientId(), fm);
      context.renderResponse();
    }
  }

  public void validateLogin(FacesContext context,
          UIComponent composant, Object valeur) {
    String nomLogin = (String) valeur;
    // Ne valide pas si le login a déjà été choisi par un autre utilisateur
    Login login = loginFacade.find(nomLogin);
    if (login != null) {
      FacesMessage message
              = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                      "Login déjà choisi",
                      nomLogin + " a déjà été choisi par un autre utilisateur");
      throw new ValidatorException(message);
    }
  }

}
