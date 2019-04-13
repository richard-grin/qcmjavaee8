/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rgrin.projetqcm.ejb;

import fr.rgrin.projetqcm.entite.Questionnaire;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author richard
 */
@Stateless
public class QuestionnaireFacade extends AbstractFacade<Questionnaire> {

  @PersistenceContext(unitName = "qcmPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public QuestionnaireFacade() {
    super(Questionnaire.class);
  }

  public Questionnaire findAvecQuestions(long id) {
    TypedQuery<Questionnaire> query = em.createQuery("select q from Questionnaire q join fetch q.questions where q.id = :id", Questionnaire.class);
    query.setParameter("id", id);
    Questionnaire questionnaire = query.getSingleResult();
    System.out.println("FACADE --- le questionnaire a " + questionnaire.getQuestions().size() + " questions !!!!!!!");
    return questionnaire;
  }

}
