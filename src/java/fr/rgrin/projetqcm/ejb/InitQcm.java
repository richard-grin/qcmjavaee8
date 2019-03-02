package fr.rgrin.projetqcm.ejb;

import fr.rgrin.projetqcm.entite.Question;
import fr.rgrin.projetqcm.entite.Questionnaire;
import fr.rgrin.projetqcm.entite.Reponse;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Suite initialisation au démarrage pour pouvoir avoir 2 transactions. Une pour
 * l'unité de persistance liée aux login et l'autre pour celle liée aux QCM.
 *
 * @author grin
 */
@Stateless
public class InitQcm {

  @PersistenceContext(unitName = "qcmPU")
  private EntityManager emQcm;

  @Resource(lookup = "java:app/jdbc/qcm_javaEE8")
  private DataSource dataSourceQcm;

  @EJB
  QuestionFacade questionFacade;

  /**
   * Crée les tables et les données pour les QCM, s'ils n'existent pas déjà.
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void initQcms() {
    try (Connection c = dataSourceQcm.getConnection()) {
      c.setAutoCommit(false);
      // Si la table des logins n'existe pas déjà, créer les tables
      if (!existe(c, "QUESTION")) { // Attention, la casse compte !!!
        System.out.println("Création des tables pour les QCM");
        String[] queries = {
          "CREATE TABLE QUESTION (ID BIGINT PRIMARY KEY, ENONCE VARCHAR(255), MOTSCLES VARCHAR(255), REPONSESMULTIPLES SMALLINT DEFAULT 0)",
          "CREATE TABLE QUESTIONNAIRE (ID BIGINT PRIMARY KEY, THEME VARCHAR(255), TITRE VARCHAR(255), MOTSCLES VARCHAR(255))",
          "CREATE TABLE REPONSE (ID BIGINT PRIMARY KEY, INTITULE VARCHAR(255), OK CHAR(1))",
          "CREATE TABLE TESTQCM (ID BIGINT PRIMARY KEY, DATE DATE, HEURE TIME, NOTE FLOAT, nomlogin varchar(50), QUESTIONNAIRE_ID BIGINT references questionnaire)",
          "CREATE TABLE REPONSETEST (ID BIGINT PRIMARY KEY, VALEURREPONSE SMALLINT DEFAULT 0, REPONSE_ID BIGINT references reponse, TESTQCM_ID BIGINT references testqcm)",
          "CREATE TABLE QUESTION_REPONSE (Question_ID BIGINT NOT NULL references question, reponses_ID BIGINT NOT NULL references reponse, numeroReponse INTEGER, PRIMARY KEY (Question_ID, reponses_ID))",
          "CREATE TABLE QUESTIONNAIRE_QUESTION (Questionnaire_ID BIGINT NOT NULL references questionnaire, questions_ID BIGINT NOT NULL references question, numeroQuestion INTEGER, PRIMARY KEY (Questionnaire_ID, questions_ID))",
          "CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL(15), PRIMARY KEY (SEQ_NAME))",
          "INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0)"
        };
        execute(c, Arrays.asList(queries));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

//    System.out.println("Ajout des données pour les QCM");
//    if (questionFacade.nbQuestions() == 0) {
//      Questionnaire questionnaire = new Questionnaire("Bases de Java 1");
//      Question q1 = new Question("Java est un langage", false);
//      q1.ajouterReponse(new Reponse("objet", true));
//      q1.ajouterReponse(new Reponse("procédural", false));
//      q1.ajouterReponse(new Reponse("fonctionnel", false));
//      Question q2 = new Question("Java permet d'écrire des applications");
//      q1.ajouterReponse(new Reponse("Web", true));
//      q1.ajouterReponse(new Reponse("desktop", true));
//      q1.ajouterReponse(new Reponse("système", false));
//      q1.ajouterReponse(new Reponse("embarquées", true));
//      emQcm.persist(q1);
//      emQcm.persist(q2);
//      emQcm.persist(questionnaire);
//    }
  }

  /**
   * Exécute une requête SQL de création ou suppression de table ou d'insertion
   * de données.
   *
   * @param c connexion à la base de données
   * @param query texte de la requête SQL
   */
  private void execute(Connection c, String query) {
    try (PreparedStatement stmt = c.prepareStatement(query)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      // Pour les logs du serveur d'application
      e.printStackTrace();
    }
  }

  /**
   * Exécute des requêtes SQL de création ou suppression de table ou d'insertion
   * de données.
   *
   * @param c connexion à la base de données
   * @param query texte de la requête SQL
   */
  private void execute(Connection c, List<String> queries) {
    queries.forEach((query) -> {
      execute(c, query);
    });
  }

  /**
   * Teste si une table existe déjà.
   *
   * @param connection
   * @param nomTable nom de la table (attention, la casse compte).
   * @return true ssi la table existe.
   * @throws SQLException
   */
  private static boolean existe(Connection connection, String nomTable)
          throws SQLException {
    boolean existe;
    DatabaseMetaData dmd = connection.getMetaData();
    try (ResultSet tables = dmd.getTables(connection.getCatalog(), null, nomTable, null)) {
      existe = tables.next();
    }
    return existe;
  }

}
