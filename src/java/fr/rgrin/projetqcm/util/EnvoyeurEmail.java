package fr.rgrin.projetqcm.util;


import javax.mail.Address;
import javax.mail.MailSessionDefinition;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * Envoyeur d'emails.
 * @author richard
 */
//@MailSessionDefinition(
//  name = "java:app/mail/unice",
//  host = "smtp.unice.fr",
//  user = "grin",
//  password = "*********",
//  from = "richard.grin@unice.fr",
//  transportProtocol = "smtps"
//)
@MailSessionDefinition(
        name = "java:app/mail/free",
        host = "smtp.free.fr",
        user = "richard.grin",
        from = " richard.grin@free.fr",
        storeProtocol = "imap",
        transportProtocol = "smtp"
)
public class EnvoyeurEmail {
  // TODO: Tester avec injection de la Session
  private final Session sessionEmail;

  public EnvoyeurEmail(String nomJNDI) throws NamingException {
    InitialContext ic = new InitialContext();
    System.out.println("EnvoyeurEmail : ic=" + ic + "; nomJNDI=" + nomJNDI);
    this.sessionEmail = (Session)ic.lookup(nomJNDI);
  }
  
  public void envoyer(String texte, String sujet, String destinataire) 
          throws AddressException, MessagingException {
    MimeMessage message = new MimeMessage(sessionEmail);
    Address adresseDestinataire = new InternetAddress(destinataire);
    message.setRecipient(RecipientType.TO, adresseDestinataire);
    message.setSubject(sujet);
    message.setText(texte);
    message.saveChanges();
    
    Transport tr = sessionEmail.getTransport();
//    String motDePasse = sessionEmail.getProperty("mail.password");
//    tr.connect(null, motDePasse);
    tr.connect();
    tr.sendMessage(message, message.getAllRecipients());
    System.out.println("*******Email envoyé à " + adresseDestinataire);
    tr.close();
  }
  
}
