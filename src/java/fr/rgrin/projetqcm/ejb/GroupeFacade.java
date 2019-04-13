package fr.rgrin.projetqcm.ejb;

import fr.rgrin.login.entite.Groupe;
import fr.rgrin.login.entite.Login;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Gère les groupes dans la base de données des logins.
 * @author grin
 */
@Stateless
public class GroupeFacade extends AbstractFacade<Groupe> {

  @PersistenceContext(unitName = "loginPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public GroupeFacade() {
    super(Groupe.class);
  }
  
  public Groupe findByNom(String nomGroupe) {
    TypedQuery<Groupe> query = em.createNamedQuery("Groupe.findByNom", Groupe.class);
    query.setParameter("nomGroupe", nomGroupe);
    return query.getSingleResult();
  }
  
}
