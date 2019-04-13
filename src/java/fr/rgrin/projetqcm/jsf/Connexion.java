package fr.rgrin.projetqcm.jsf;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Pour se déconnecter de la session.
 *
 * @author richard
 */
@Named
@RequestScoped
public class Connexion implements Serializable {

  // Juste pour tester l'injection de Principal
  @Inject
  private Principal principal;

  public String deconnect() {
    System.out.println("********DECONNEXION !!!!");
    try {
      ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
    } catch (ServletException ex) {
      Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "/index";
  }

  public String getNomUtilisateur() {
    return principal.getName();
  }

}
