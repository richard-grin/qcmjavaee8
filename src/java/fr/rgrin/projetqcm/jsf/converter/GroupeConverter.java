package fr.rgrin.projetqcm.jsf.converter;

import fr.rgrin.login.entite.Groupe;
import fr.rgrin.projetqcm.ejb.login.GroupeFacade;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

/**
 * Convertisseur pour les groupes d'utilisateurs
 * @author grin
 */
@FacesConverter(value="groupeConverter", managed = true)
public class GroupeConverter implements javax.faces.convert.Converter<Groupe> {
  @EJB
  private GroupeFacade groupeFacade;

  @Override
  public Groupe getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    }
    return groupeFacade.find(Long.parseLong(value));
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Groupe groupe) {
    if (groupe == null) {
      return "";
    }
    return groupe.getId().toString();
  }

 
  
}
