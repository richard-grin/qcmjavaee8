/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rgrin.projetqcm.ejb;

import fr.rgrin.projetqcm.entite.Question;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author richard
 */
@Stateless
public class QuestionFacade extends AbstractFacade<Question> {

  @PersistenceContext(unitName = "qcmPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public QuestionFacade() {
    super(Question.class);
  }

  /**
   * Retourne le nombre de questions enregistrées dans la base de données.
   */
  public long nbQuestions() {
    TypedQuery<Long> q = em.createQuery("select count(q) from Question q", Long.class);
    return q.getSingleResult();
  }

}
