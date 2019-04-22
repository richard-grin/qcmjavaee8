package fr.rgrin.projetqcm.ejb.login;

import fr.rgrin.login.entite.Groupe;
import fr.rgrin.login.entite.Login;
import fr.rgrin.projetqcm.ejb.AbstractFacade;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
   * Recherche un login par son email. Ne marche plus car j'ai enlevé l'unicité
   * pour les emails pour faciliter les tests !!
   *
   * @param email
   * @return
   */
  public Login findByEmail(String email) {
    TypedQuery<Login> query = em.createNamedQuery("Login.findByEmail", Login.class);
    query.setParameter("email", email);
    return query.getSingleResult();
  }

  public Login findByNom(String nom) {
    TypedQuery<Login> query = em.createNamedQuery("Login.findByNom", Login.class);
    query.setParameter("nomLogin", nom);
    return query.getSingleResult();
  }
  
  public List<Login> findAllWithGroups() {
    TypedQuery<Login> query = em.createNamedQuery("Login.getAllWithGroups", Login.class);
    return query.getResultList();
  }
  
  public List<Groupe> findGroupesUtilisateur(String nomLogin) {
    TypedQuery<Groupe> query = em.createNamedQuery("Login.findGroupesUtilisateur", Groupe.class);
    query.setParameter("nomLogin", nomLogin);
    return query.getResultList();
  }

  @Override
  public void edit(Login login) {
    super.edit(login);
  }

}
