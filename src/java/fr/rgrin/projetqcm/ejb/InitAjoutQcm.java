package fr.rgrin.projetqcm.ejb;

import fr.rgrin.projetqcm.entite.Question;
import fr.rgrin.projetqcm.entite.Questionnaire;
import fr.rgrin.projetqcm.entite.Reponse;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Ajouter des QCMs à l'initialisation de l'application.
 *
 * @author grin
 */
@Stateless
public class InitAjoutQcm {

  @PersistenceContext(unitName = "qcmPU")
  private EntityManager emQcm;

  @EJB
  QuestionFacade questionFacade;

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void ajoutQcms() {
    System.out.println("Ajout des données pour les QCM");
    if (questionFacade.nbQuestions() == 0) {
      Questionnaire questionnaire = new Questionnaire("Bases de Java 1");
      Question q1 = new Question("Java est un langage", false);
      q1.ajouterReponse(new Reponse("objet", true));
      q1.ajouterReponse(new Reponse("procédural", false));
      q1.ajouterReponse(new Reponse("fonctionnel", false));
      Question q2 = new Question("Java permet d'écrire des applications");
      q2.ajouterReponse(new Reponse("Web", true));
      q2.ajouterReponse(new Reponse("desktop", true));
      q2.ajouterReponse(new Reponse("système", false));
      q2.ajouterReponse(new Reponse("embarquées", true));
      questionnaire.ajouterQuestion(q1);
      questionnaire.ajouterQuestion(q2);
      emQcm.persist(q1);
      emQcm.persist(q2);
      emQcm.persist(questionnaire);
    }
  }
}
