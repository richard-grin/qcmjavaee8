package fr.rgrin.projetqcm.jsf.login;

import fr.rgrin.login.entite.Groupe;
import fr.rgrin.login.entite.Login;
import fr.rgrin.projetqcm.ejb.login.GroupeFacade;
import fr.rgrin.projetqcm.ejb.login.LoginFacade;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;

/**
 *
 * @author grin
 */
@Named(value = "confirmationController")
@RequestScoped
public class ConfirmationController {
  // valeur données par un paramètre de vue de confirmation.xhtml
  private String loginConfirm;
  private String codeConfirm;
  private String emailConfirm;
  
  @EJB
  private LoginFacade loginFacade;
  @EJB
  private GroupeFacade groupeFacade;

  /**
   * Creates a new instance of ConfirmationController
   */
  public ConfirmationController() {
  }

  public String getLoginConfirm() {
    return loginConfirm;
  }

  public void setLoginConfirm(String loginConfirm) {
    this.loginConfirm = loginConfirm;
  }

  public String getCodeConfirm() {
    return codeConfirm;
  }

  public void setCodeConfirm(String codeConfirm) {
    this.codeConfirm = codeConfirm;
  }

  public String getEmailConfirm() {
    return emailConfirm;
  }

  public void setEmailConfirm(String emailConfirm) {
    this.emailConfirm = emailConfirm;
  }
  
  public String verifierConfirmation() {
    System.out.println("===========Valeurs reçues dans verifierConfirmation() :");
    System.out.println("login = " + loginConfirm);
    System.out.println("email = " + emailConfirm);
    System.out.println("code = " + codeConfirm);
    System.out.println("loginFacade=" + loginFacade);

    Login login2 = loginFacade.find(loginConfirm);
    System.out.println("login2=" + login2);
//    String statutLogin = login.getStatut();
    if (codeConfirm.equals(login2.getCode())
            && loginConfirm.equals(login2.getLogin())
            && emailConfirm.equals(login2.getEmail())
            && login2.getStatut().equals("email")) {
      System.out.println("*****Confirmation OK !!!!");
      login2.setStatut("ok");
      // Tout est OK, on peut le mettre dans le groupe des inscrits.
      // Comme les utilisateurs de ce groupe ne peuvent presque rien faire,
      // un administrateur devra passer l'utilisateur au moins dans le groupe
      // des étudiants pour qu'il puisse au moins passer un QCM.
      Groupe groupeInscrits;
      try {
        groupeInscrits = groupeFacade.findByNom("inscrit");
      } catch (EJBException e) {
        Throwable cause = e.getCause();
        if (cause instanceof NoResultException) {
          // Ajouter le groupe s'il n'existe pas déjà
          groupeInscrits = new Groupe("inscrit");
          groupeFacade.create(groupeInscrits);
        } else {
          throw e;
        }
      }
      login2.addGroupe(groupeInscrits);
      loginFacade.edit(login2);
//      login = login2;
      return "/index";
    } else {
      // Anomalie à enregistrer dans les logs de l'application
      // et message à l'utilisateur (?)
      System.out.println("ERREUR !!");
      System.out.println("login2 : " + login2.getCode() + ";" + login2.getEmail() + ";" + login2.getStatut());
      System.out.println("*****Confirmation PAS OK pour " + loginConfirm + " !!!!");
      return "/login/confirmation-error";
    }
  }

}
