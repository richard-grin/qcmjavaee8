package fr.rgrin.projetqcm.jsf.question;

import fr.rgrin.projetqcm.ejb.QuestionFacade;
import fr.rgrin.projetqcm.entite.Question;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Convertisseur pour une question.
 *
 * @author richard
 */
@FacesConverter(forClass = Question.class, managed = true)
public class QuestionConverter implements Converter<Question> {
  @EJB
  QuestionFacade ejb;

  @Override
  public Question getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    }
    System.out.println("Converter.getAsObject de QuestionConverter");
    return ejb.find(Long.parseLong(value));
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Question value) {
    Long id = ((Question) value).getId();
    if (id == null) {
      return null;
    } else {
      return id.toString();
    }
  }
}
