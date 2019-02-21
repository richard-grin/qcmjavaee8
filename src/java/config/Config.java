package config;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

/**
 * Configuration de l'entrepôt d'identité et du mécanisme d'authentification.
 *
 * @author grin
 */
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "java:app/jdbc/login_javaEE8",
        callerQuery = "select mot_de_passe from login where login=?",
        groupsQuery = "select groupe.nom from groupe join login_groupe on groupe.id = login_groupe.id_groupe where login=?")
@CustomFormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/login/login_custom.xhtml", 
                errorPage = ""
        )
)
@ApplicationScoped
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
public class Config {
}
