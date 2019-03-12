package fr.rgrin.projetqcm.ejb;

import fr.rgrin.login.entite.Login;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * EJB qui gère les logins.
 * 
 * @author richard
 */
@Stateless
public class LoginFacade extends AbstractFacade<Login> {
  @PersistenceContext(unitName = "loginPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public LoginFacade() {
    super(Login.class);
  }
  
  /**
   * Recherche un login par son email.
   * Ne marche plus car j'ai enlevé l'unicité pour les emails pour faciliter
   * les tests !!
   * @param email
   * @return 
   */
  public Login findByEmail(String email) {
    Query query = em.createNamedQuery("Login.findByEmail");
    query.setParameter("email", email);
    return (Login)query.getSingleResult();
  }
  
  public Login findByNom(String nom) {
    Query query = em.createNamedQuery("Login.findByNom");
    query.setParameter("nomLogin", nom);
    return (Login)query.getSingleResult();
  }

  @Override
  public void edit(Login login) {
    super.edit(login);
  }
  
//  /**
//   * Pas utilisé pour le moment.
//   * @param login
//   * @return 
//   */
//  public Login edit2(Login login) {
//    login = getEntityManager().merge(login);
////    refresh(login);
//    return login;
//  }
  
}
