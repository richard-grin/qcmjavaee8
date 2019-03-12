package fr.rgrin.projetqcm.ejb;

import fr.rgrin.projetqcm.util.HashMdp;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.mail.MailSessionDefinition;
import javax.sql.DataSource;

/**
 * Initialise la base de données avec des utilisateurs et des données pour les
 * questions et les questionnaires.
 *
 * @author richard
 */
@DataSourceDefinitions({
  @DataSourceDefinition(
          className = "org.apache.derby.jdbc.ClientDataSource",
          name = "java:app/jdbc/login_javaEE8",
          serverName = "localhost",
          portNumber = 1527,
          user = "toto",
          password = "toto",
          databaseName = "login_javaEE8"
  ),
  @DataSourceDefinition(
          className = "org.apache.derby.jdbc.ClientDataSource",
          name = "java:app/jdbc/qcm_javaEE8",
          serverName = "localhost",
          portNumber = 1527,
          user = "toto",
          password = "toto",
          databaseName = "qcm_javaEE8"
  )
})
@MailSessionDefinition(
        name = "java:app/mail/free",
        host = "smtp.free.fr",
        user = "richard.grin",
        from = " richard.grin@free.fr",
        storeProtocol = "imap",
         transportProtocol = "smtp"
)
@Singleton
@Startup
public class Init {

//  @PersistenceContext(unitName = "loginPU")
//  private EntityManager emLogin;

  @Resource(lookup = "java:app/jdbc/login_javaEE8")
  private DataSource dataSourceLogin;

//  @PersistenceContext(unitName = "qcmPU")
//  private EntityManager emQcm;

  @Resource(lookup = "java:app/jdbc/qcm_javaEE8")
  private DataSource dataSourceQcm;

  @Inject
  // Pour coder le mot de passe
  private HashMdp passwordHash;

  @EJB
  InitQcm init2;

  @EJB
  InitAjoutQcm initAjoutQcm;

  /**
   * Ajoute des logins s'il n'y en a pas déjà.
   */
  @PostConstruct
//  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void init() {
    System.out.println("************************INIT !!!!!!!!!!!!!");
    initLogins();
    init2.initQcms();
    initAjoutQcm.ajoutQcms();
//    initQcms();
//    ajoutDonnees();

  }

//  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  /**
   * Création des tables pour conserver les login et mots de passe. Créer les
   * tables et les données pour les logins et les tables et les données pour les
   * QCM.
   */
//  private void initLogins() {
////    creationTablesAvecJPA();
//    creationTablesEtLoginsAvecJDBC();
//  }
  /**
   * Crée les tables et les données pour les logins, s'ils n'existent pas déjà.
   */
  private void initLogins() {
    // Création des tables login, groupe et login_groupe
    // Regarde si les tables existent déjà
    String creationTableLoginSql
            = "create table login( "
            + "login varchar(50) primary key, "
            + "mot_de_passe varchar(160), "
            + "email varchar(255), "
            + "statut varchar(10), "
            + "code varchar(15))";
    String creationTableGroupeSql
            = "create table groupe( "
            + "id integer primary key, "
            + "nom varchar(50))";
    String creationTableLoginGroupeSql
            = "create table login_groupe( "
            + "login varchar(50) references login, "
            + "id_groupe integer references groupe, "
            + "primary key(login, id_groupe))";
    String creationTableSequenceSql
            = "create table sequence( "
            + "seq_name varchar(50) primary key, "
            + "seq_count integer)";
    try (Connection c = dataSourceLogin.getConnection()) {
      // Si la table des logins n'existe pas déjà, créer les tables
      if (!existe(c, "LOGIN")) { // Attention, la casse compte !!!
        System.out.println("Création des tables");
        // Remarque : la table SEQUENCE est créée automatiquement ; pas besoin de la créer
        execute(c, creationTableLoginSql);
        execute(c, creationTableGroupeSql);
        execute(c, creationTableLoginGroupeSql);
        execute(c, creationTableSequenceSql);
      } else {
        System.out.println("Tables existent déjà");
      }

      // Si la table LOGIN n'est pas vide, ne rien faire
      if (vide(c, "login")) {
        System.out.println("============ Table login vide ; initialisation des données dans ls tables");
        // Le mot de passe haché :
        String hashMdp = passwordHash.generate("toto");
//      System.out.println("******===== TAILLE mot de passe haché : " + hashMdp.length());

        // ric appartient aux groupes enseignant et etudiant (pour pouvoir tester)
        execute(c, "INSERT INTO login (LOGIN, MOT_DE_PASSE, email, statut) VALUES('ric', '"
                + hashMdp + "', 'grin@unice.fr', 'ok')");
        execute(c, "INSERT INTO groupe(id, nom) VALUES(1, 'enseignant')");
        execute(c, "INSERT INTO groupe(id, nom)  VALUES(2, 'etudiant')");
        execute(c, "INSERT INTO login_groupe(login, id_groupe) VALUES('ric', 1)");
        execute(c, "INSERT INTO login_groupe(login, id_groupe) VALUES('ric', 2)");

        // toto appartient seulement au groupe etudiant
//        execute(c, "INSERT INTO personne (id, nom, prenom) VALUES(2, 'Bernard', 'Pierre')");
        execute(c, "INSERT INTO login (LOGIN, MOT_DE_PASSE, email, statut) VALUES('toto', '"
                + hashMdp + "', 'grin@unice.fr', 'ok')");
        execute(c, "INSERT INTO login_groupe(login, id_groupe) VALUES('toto', 2)");
//      execute(c, "INSERT INTO caller_groups VALUES('juneau', 'group2')");
      }
//      if (vide(c, "formation")) {
//        System.out.println("Initialisation des données pour les formations");
//        initQcms();
//      }
    } catch (SQLException e) {
      // Pour les logs du serveur d'application
      e.printStackTrace();
    }
  }

  /**
   * Crée les tables et les données pour les QCM, s'ils n'existent pas déjà.
   */
//  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//  private void initQcms() {
//    try (Connection c = dataSourceQcm.getConnection()) {
//      // Si la table des logins n'existe pas déjà, créer les tables
//      if (!existe(c, "QUESTION")) { // Attention, la casse compte !!!
//        System.out.println("Création des tables pour les QCM");
//        String[] queries = {
//          "CREATE TABLE QUESTION (ID BIGINT PRIMARY KEY, ENONCE VARCHAR(255), MOTSCLES VARCHAR(255), REPONSESMULTIPLES SMALLINT DEFAULT 0)",
//          "CREATE TABLE QUESTIONNAIRE (ID BIGINT PRIMARY KEY, THEME VARCHAR(255), TITRE VARCHAR(255), MOTSCLES VARCHAR(255))",
//          "CREATE TABLE REPONSE (ID BIGINT PRIMARY KEY, INTITULE VARCHAR(255), OK CHAR(1))",
//          "CREATE TABLE REPONSETEST (ID BIGINT PRIMARY KEY, VALEURREPONSE SMALLINT DEFAULT 0, REPONSE_ID BIGINT references reponse, TESTQCM_ID BIGINT references testqcm)",
//          "CREATE TABLE TESTQCM (ID BIGINT PRIMARY KEY, DATE DATE, HEURE TIME, NOTE FLOAT, QUESTIONNAIRE_ID BIGINT references questionnaire)",
//          "CREATE TABLE QUESTION_REPONSE (Question_ID BIGINT NOT NULL references question, reponses_ID BIGINT NOT NULL references reponse, numeroReponse INTEGER, PRIMARY KEY (Question_ID, reponses_ID))",
//          "CREATE TABLE QUESTIONNAIRE_QUESTION (Questionnaire_ID BIGINT NOT NULL references questionnaire, questions_ID BIGINT NOT NULL references question, numeroQuestion INTEGER, PRIMARY KEY (Questionnaire_ID, questions_ID))",
//          "CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL(15), PRIMARY KEY (SEQ_NAME))",
//          "INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0)"
//        };
//        execute(c, Arrays.asList(queries));
//      }
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//  }
//
//  private void ajoutDonnees() {
//    if (nbQuestions() == 0) {
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
//      questionnaire.ajouterQuestion(q1);
//      questionnaire.ajouterQuestion(q2);
//      emQcm.persist(q1);
//      emQcm.persist(q2);
//      emQcm.persist(questionnaire);
//    }
//  }

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
    for (String query : queries) {
      execute(c, query);
    }
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

  /**
   * @param nomTable nom de la table SQL
   * @return true ssi la table est vide.
   */
  private boolean vide(Connection c, String nomTable) throws SQLException {
    Statement stmt = c.createStatement();
    ResultSet rset = stmt.executeQuery("select count(1) from " + nomTable);
    rset.next();
    int nb = rset.getInt(1);
    return nb == 0;
  }

//  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//  private void ajoutDonnees() {
//    System.out.println("Début ajout des logins===========");
//    // Insertion des comptes utilisateur
//    // Regarde si des logins existent déjà
//    CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//    cq.select(cq.from(Login.class));
//    List<Login> l = em.createQuery(cq).getResultList();
//    if (l.isEmpty()) {
//      System.out.println("la table est vide donc on ajoute+++++");
//      Groupe etudiant = new Groupe("etudiant");
//      Groupe enseignant = new Groupe("enseignant");
//      // Ajoute des logins (toto pour tous les mots de passe, en SHA256 et Hex)
//      Login grin = new Login("grin", passwordHash.generate("toto"));
//      Login pierre = new Login("pierre", passwordHash.generate("toto"));
//      Login jacques = new Login("jacques", passwordHash.generate("toto"));
//      grin.addGroupe(enseignant);
//      pierre.addGroupe(etudiant);
//      jacques.addGroupe(etudiant);
//      em.persist(etudiant);
//      em.persist(enseignant);
//      em.persist(grin);
//      em.persist(pierre);
//      em.persist(jacques);
//      System.out.println("*********Logins et groupes créés");
//
//      // Initialisation pour les QCM
//    }
//  }
  /**
   * Avec JPA ça ne marche pas bien car on ne peut tester facilement si les
   * tables existent déjà. Il vaut mieux le faire avec JDBC. Si les tables
   * existent déjà, l'exception interne provoque l'arrêt de l'initialisation et
   * les ajouts de données ne sont pas prises en compte, je ne comprends pas
   * pourquoi ! Il faudra voir ça plus tranquillement...
   */
//  private void creationTablesAvecJPA() {
//    String creationTableLoginSql
//            = "create table login( "
//            + "login varchar(50) primary key, "
//            + "mot_de_passe varchar(160), "
//            + "email varchar(255))";
//    String creationTableGroupeSql
//            = "create table groupe( "
//            + "id integer primary key, "
//            + "nom varchar(50))";
//    String creationTableLoginGroupeSql
//            = "create table login_groupe( "
//            + "login varchar(50) references login, "
//            + "id_groupe integer references groupe, "
//            + "primary key(login, id_groupe))";
//    String creationTableSequenceSql
//            = "create table sequence( "
//            + "seq_name varchar(50) primary key, "
//            + "seq_count integer)";
//    try {
//      Query q = em.createNativeQuery(creationTableLoginSql);
//      q.executeUpdate();
//
//      q = em.createNativeQuery(creationTableGroupeSql);
//      q.executeUpdate();
//
//      q = em.createNativeQuery(creationTableLoginGroupeSql);
//      q.executeUpdate();
//
//      q = em.createNativeQuery(creationTableSequenceSql);
//      q.executeUpdate();
//      String ajouterLigneDansSequence
//              = "insert into sequence values ('SEQ_GEN', 0)";
//      q = em.createNativeQuery(ajouterLigneDansSequence);
//      q.executeUpdate();
//    } catch (Exception e) {
//      // Pour voir quelle exception est lancée si la table existe déjà
////      e.printStackTrace();
//      System.out.println("Tables existent déjà !!!!!!");
//    }
//  }
}
