package eu.uqasar.web.pages.qmtree.quality.objective;

import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Objects;

public class QMQualityObjectiveFormValidator extends AbstractFormValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5087173050403582944L;
	/** form components to be validated. */
   	private final FormComponent[] components;

   	public QMQualityObjectiveFormValidator(FormComponent f1, FormComponent f2, FormComponent f3) {
  		if (f1 == null) {
     			throw new IllegalArgumentException(
     				"FormComponent1 cannot be null");
  		}
  		if (f2 == null) {
  			f2 = new TextField("lowerLimit", new PropertyModel<>(Double.MIN_VALUE, "lowerLimit")).setRequired(false);
  		}
  		if (f3 == null) {
  			f3 = new TextField("upperLimit", new PropertyModel<>(Double.MIN_VALUE, "upperLimit")).setRequired(false);  			
  		}
  		components = new FormComponent[] { f1, f2, f3 };
  		
	}
   	
	@Override
	public FormComponent<?>[] getDependentFormComponents() {
		return components;
	}

	@Override
	public void validate(Form<?> form) {
		final FormComponent name = components[0];
   		final FormComponent<Double> lowerLimit = components[1];
   		final FormComponent<Double> upperLimit = components[2];
		
   		
		Localizer loc = getLocalizer(form);
   		String nameValue = Objects.stringValue(name.getInput(), true);
   		
   		if ("".equals(nameValue)) {
   			name.error(loc.getString("form.name.required", name));
   		} else if (nameValue.length()>255){
   			name.error(loc.getString("form.name.max", name));
   		}
   		
//   		if (Double.valueOf(lowerLimit.getValue()) > Double.valueOf(upperLimit.getValue())){
//   			upperLimit.error(loc.getString("form.upperLimit.low", upperLimit));
//   		}
	}

	public final Localizer getLocalizer(Form<?> form)
	{
		return form.getApplication().getResourceSettings().getLocalizer();
	}	

}
