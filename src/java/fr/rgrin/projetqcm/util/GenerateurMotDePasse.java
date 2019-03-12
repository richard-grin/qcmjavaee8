package fr.rgrin.projetqcm.util;

import java.util.Random;

/**
 * Génère un mot de passe aléatoire.
 * @author richard
 */
public class GenerateurMotDePasse {

  /**
   * Les caractères acceptés dans le mot de passe.
   */
  private String alphabet =
          "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-@#&'(!?)$%?:;/.?,";
  /**
   * Longueur du mot de passe.
   */
  private int longueur = 6;

  public GenerateurMotDePasse() {
  }

  public GenerateurMotDePasse(String alphabet, int longueur) {
    this.longueur = longueur;
    this.alphabet = alphabet;
  }

  public GenerateurMotDePasse(int longueur) {
    this.longueur = longueur;
  }

  public String genererMotDePasse() {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder(longueur);
    for (int i = 0; i < longueur; i++) {
      sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
    }
    return sb.toString();
  }
  
  public static void main(String[] args) {
    GenerateurMotDePasse g = new GenerateurMotDePasse(8);
    for (int i = 0; i < 10; i++) {
      System.out.println(g.genererMotDePasse());
    }
  }
}
