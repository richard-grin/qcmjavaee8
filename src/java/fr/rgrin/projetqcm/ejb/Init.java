package fr.rgrin.projetqcm.ejb;

import fr.rgrin.login.entite.Groupe;
import fr.rgrin.login.entite.Login;
import fr.rgrin.projetqcm.util.HashMdp;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

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
@Singleton
@Startup
public class Init {

  @PersistenceContext(unitName = "loginPU")
  private EntityManager em;

  @Inject
  // Pour coder le mot de passe
  private HashMdp passwordHash;

  /**
   * Ajoute des logins s'il n'y en a pas déjà.
   */
  @PostConstruct
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void init() {
//    creationTables();
    ajoutDonnees();
  }
  
//  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  private void creationTables() {
    System.out.println("************************INIT !!!!!!!!!!!!!");
    // Création de la vue qui sert pour le domaine de sécurité
    // pour la table des groupes.

//    String ordreCreaVue =
//            "create view V_LOGIN_GROUPE(LOGIN, NOM_GROUPE) as "
//            + " select LOGIN, GROUPE.NOM "
//            + " from GROUPE join LOGIN_GROUPE "
//            + " on GROUPE.ID = LOGIN_GROUPE.ID_GROUPE ";
//    try {
//      Query q = em.createNativeQuery(ordreCreaVue);
//      q.executeUpdate();
//      System.out.println("********* Vue créée");
//    } catch (Throwable throwable) {
//      System.out.println("********** Vue pas créée :" + throwable);
//    }
    // Création des tables login, groupe et login_groupe
    // Regarde si les tables existent déjà
    String creationTableLoginSql
            = "create table login( "
            + "login varchar(50) primary key, "
            + "mot_de_passe varchar(160), "
            + "email varchar(255))";
    try {
      Query q = em.createNativeQuery(creationTableLoginSql);
      q.executeUpdate();
      String creationTableGroupeSql
              = "create table groupe( "
              + "id integer primary key, "
              + "nom varchar(50))";
      q = em.createNativeQuery(creationTableGroupeSql);
      q.executeUpdate();
      String creationTableLoginGroupeSql
              = "create table login_groupe( "
              + "login varchar(50) references login, "
              + "id_groupe integer references groupe, "
              + "primary key(login, id_groupe))";
      q = em.createNativeQuery(creationTableLoginGroupeSql);
      q.executeUpdate();
      String creationTableSequenceSql
              = "create table sequence( "
              + "seq_name varchar(50) primary key, "
              + "seq_count integer)";
      q = em.createNativeQuery(creationTableSequenceSql);
      q.executeUpdate();
      String ajouterLigneDansSequence =
              "insert into sequence values ('SEQ_GEN', 0)";
      q = em.createNativeQuery(ajouterLigneDansSequence);
      q.executeUpdate();
    } catch (Exception e) {
      // Pour voir quelle exception est lancée si la table existe déjà
//      e.printStackTrace();
      System.out.println("Tables existent déjà !!!!!!");
    }
  }
  
//  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  private void ajoutDonnees() {
    System.out.println("Début ajout des logins===========");
    // Insertion des comptes utilisateur
    // Regarde si des logins existent déjà
    CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
    cq.select(cq.from(Login.class));
    List<Login> l = em.createQuery(cq).getResultList();
    if (l.isEmpty()) {
      System.out.println("la table est vide donc on ajoute+++++");
      Groupe etudiant = new Groupe("etudiant");
      Groupe enseignant = new Groupe("enseignant");
      // Ajoute des logins (toto pour tous les mots de passe, en SHA256 et Hex)
      Login grin = new Login("grin", passwordHash.generate("toto"));
      Login pierre = new Login("pierre", passwordHash.generate("toto"));
      Login jacques = new Login("jacques", passwordHash.generate("toto"));
      grin.addGroupe(enseignant);
      pierre.addGroupe(etudiant);
      jacques.addGroupe(etudiant);
      em.persist(etudiant);
      em.persist(enseignant);
      em.persist(grin);
      em.persist(pierre);
      em.persist(jacques);
      System.out.println("*********Logins et groupes créés");

      // Initialisation pour les QCM
    }
  }
}
