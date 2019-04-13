package fr.rgrin.projetqcm.jsf.questionnaire;

import fr.rgrin.projetqcm.ejb.QuestionnaireFacade;
import fr.rgrin.projetqcm.entite.Questionnaire;
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
@FacesConverter(forClass = Questionnaire.class, managed = true)
public class QuestionnaireConverter implements Converter<Questionnaire> {

  @EJB
  QuestionnaireFacade ejb;

  @Override
  public Questionnaire getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    }
    System.out.println("QuestionnaireConverter.getAsObject");
    return ejb.find(Long.parseLong(value));
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Questionnaire value) {
    System.out.println("QuestionnaireConverter.getAsString");
    Long id = ((Questionnaire) value).getId();
    if (id == null) {
      return null;
    } else {
      return id.toString();
    }
  }
}
