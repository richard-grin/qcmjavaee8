package fr.rgrin.projetqcm.util;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

/**
 * Pour générer un mot de passe haché.
 * Avec l'initialisation actuelle, le mot de passe haché est de longueur 159.
 * @author grin
 */
@ApplicationScoped
public class HashMdp {

  @Inject
  private Pbkdf2PasswordHash passwordHash;

  @PostConstruct
  public void init() {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
    parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
    parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
    passwordHash.initialize(parameters);
    
  }
  
  /**
   * Retourne le mot de passe crypté.
   * @param mdp le mot de passe non crypté.
   * @return 
   */
  public String generate(String mdp) {
    return passwordHash.generate(mdp.toCharArray());
  }

}
