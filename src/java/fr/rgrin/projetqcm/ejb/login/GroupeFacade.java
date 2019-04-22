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
    TypedQuery<Groupe> query = 
            em.createNamedQuery("Groupe.findByNom", Groupe.class);
    query.setParameter("nomGroupe", nomGroupe);
    return query.getSingleResult();
  }
  
  public List<Groupe> findGroupeUtilisateur(Login login) {
    TypedQuery<Groupe> query = 
            em.createNamedQuery("Login.findGroupesUtilisateur", Groupe.class);
    query.setParameter("nomLogin", login.getLogin());
    return query.getResultList();
  }
  
}
