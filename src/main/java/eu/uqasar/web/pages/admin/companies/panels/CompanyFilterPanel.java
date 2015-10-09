package eu.uqasar.web.pages.admin.companies.panels;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import eu.uqasar.service.company.CompanyService;

/**
 * 
 *
 */
public abstract class CompanyFilterPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private final TextField<String> companyName;
	private final TextField<String> companyShortName;
	private final DropDownChoice<String> companyCountry;

	private final Form<Void> form;
	private final IndicatingAjaxButton apply, reset;

	private String name;
	
	private String shortName;
	private String country;
	
	@Inject
	CompanyService companyService;
	
	public CompanyFilterPanel(String id) {
		super(id);

		form = new Form<>("form");
		companyName = new TextField<String>("name", new PropertyModel<String>(this, "name"));
		companyShortName = new TextField<String>("shortName", new PropertyModel<String>(this, "shortName"));
		companyCountry = new DropDownChoice<String>("country", new PropertyModel<String>(this, "country"), Arrays.asList("España","Deutschland","Luxemburg","Norge","Suomi"));
		
		form.add(companyName)
			.add(companyShortName)
			.add(companyCountry)
			.add(apply = new IndicatingAjaxButton("apply") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					CompanyFilterPanel.this.applyClicked(target, form);
				}
			})
			.add(reset = new IndicatingAjaxButton("reset") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					CompanyFilterPanel.this.resetClicked(target, form);
					resetForm();
					target.add(form);
				}
			});
		add(form);

	}

	public CompanyFilterStructure getFilter() {
		return new CompanyFilterStructure(this);
	}

	public String getName() {
		return name;
	}
	public String getShortName() {
		return shortName;
	}
	public String getCountry() {
		return country;
	}

	private void resetForm() {
		companyName.clearInput();
		companyShortName.clearInput();
		companyCountry.clearInput();
		companyName.setModelObject(null);
		companyShortName.setModelObject(null);
		companyCountry.setModelObject(null);

	}

	public abstract void applyClicked(AjaxRequestTarget target, Form<?> form);

	public abstract void resetClicked(AjaxRequestTarget target, Form<?> form);

}
