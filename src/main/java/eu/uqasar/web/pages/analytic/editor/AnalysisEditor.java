package eu.uqasar.web.pages.analytic.editor;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2MultiChoice;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import eu.uqasar.model.analytic.Analysis;
import eu.uqasar.model.analytic.Dimensions;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.AnalyticService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.analytic.AnalyticWorkbench;

/**
 * 
 *
 * TODO: add validation for compulsory values such name
 */

public class AnalysisEditor extends BasePage {

	private static final long serialVersionUID = -4164700955387356669L;

	@Inject 
	AnalyticService analyticService;
	
	@Inject 
	TreeNodeService treeNodeService;
	
	private final Analysis analysis;

	private final Form<Analysis> analysisForm = new Form<>("form");
	
	public AnalysisEditor(PageParameters parameters) {
		super(parameters);
		
		// Check if the passed value is a new analysis or an existing one 
		if (parameters != null && parameters.get("analysis-id") != null
				&& parameters.get("analysis-id").toOptionalString().equals("new")) {
			analysis = new Analysis();
		} else {
			analysis = analyticService.getById(parameters.get("analysis-id").toOptionalLong());
		}
		
		
		// Analysis name
//		form.add(new TextField<String>("name", new PropertyModel<String>(analysis, "name")));
		analysisForm.add(newNameTextField());
		
		// Analysis description
		analysisForm.add(new TextArea<>("description", new PropertyModel<String>(analysis, "description")));
		
		// Dropdown with all the projects stored in platform
//		form.add(newProjectDropDownChoice());
		analysisForm.add(projectDropDown());
		
		
		// Button to submit
		analysisForm.add(newSubmitLink());

		// Button to cancel
		analysisForm.add(newCancelLink());
		
		// Adds the form with all its content 
		add(analysisForm);
		
		// Add the dimension selector 
		analysisForm.add(new Select2MultiChoice<>("dimensionSelector",
                new PropertyModel<Collection<Dimensions>>(analysis, "dimensions"), new DimensionProvider()));
	}
	
	/**
	 * @return Returns a field form to type Name and has validation 
	 */
	private InputBorder<String> newNameTextField() {
		  return new OnEventInputBeanValidationBorder<>(
		      "nameValidationBorder", new TextField<>("name",
		          new PropertyModel<String>(analysis, "name"))
		          .setRequired(true),
		      new StringResourceModel("label.name", this, null),
		      HtmlEvent.ONCHANGE);
		}
	

	/**
	 * @return Returns a project DropDownChoice with all the projects on platforms
	 */
	private InputBorder<Project> projectDropDown(){
		return new OnEventInputBeanValidationBorder<>(
				"projectValidatorBorder", 
				new DropDownChoice<>("project",
						new PropertyModel<Project>(analysis,"project"), treeNodeService.getAllProjects()), 
				new StringResourceModel("label.project", this, null),
				HtmlEvent.ONBLUR);
	}
	
	/**
	 * 
	 * @return Returns the button to submit and save the current edited analysis
	 */
	private AjaxSubmitLink newSubmitLink() {
		return new AjaxSubmitLink("submit", analysisForm) {

			private static final long serialVersionUID = 6099483467114314555L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				save(target);

			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				showErrors(target);
			}
		};
	}
	

	/**
	 * @param target Saves the current edited Analysis
	 */
    private void save(AjaxRequestTarget target) {
		analyticService.update(analysis);
		Session.get().success(
				new StringResourceModel("saved.message", this, Model
						.of(analysis)).getString());
		setResponsePage(
				AnalyticWorkbench.class,
				new PageParameters().set(
						MESSAGE_PARAM,
						new StringResourceModel("saved.message", this, Model
								.of(analysis)).getString()).set(LEVEL_PARAM,
						FeedbackMessage.SUCCESS));
	}
	
	
	/**
	 * 
	 * @param target
	 */
    private void showErrors(AjaxRequestTarget target) {
		// in case of errors (e.g. validation errors) show error
		// messages in form
		target.add(analysisForm);
	}
	
	/**
	 * 
	 * @return Returns a button to Cancel action and returns to main list of Analysis  
	 */
	private Link<AnalysisEditor> newCancelLink() {
		return new Link<AnalysisEditor>("cancel") {
			private static final long serialVersionUID = -5073631551981490752L;

			@Override
			public void onClick() {
				setResponsePage(AnalyticWorkbench.class);
			}
		};
	}
	

	/**
	 * 
	 * Provides dimensions values from Enum to be used in Queries.
	 * TODO: Enum will be replaced with Cubes provided dimensions values 
	 *
	 */
	
	public class DimensionProvider extends TextChoiceProvider<Dimensions> {

		private static final long serialVersionUID = -5378166419263242594L;

		@Override
		protected String getDisplayText(Dimensions choice) {
			return choice.name();
		}

		@Override
		protected Object getId(Dimensions choice) {
			return choice ;
		}

		@Override
		public void query(String arg0, int arg1, Response<Dimensions> response) {
			response.addAll(Dimensions.getAllDimensions());
		}

		@Override
		public Collection<Dimensions> toChoices(Collection<String> ids) {
	    	ArrayList<Dimensions> dimensions = new ArrayList<>();
	    	
	    	for(String id : ids){
	    		dimensions.add(Dimensions.valueOf(id));
	    	} 	

	    	return dimensions;
		}

	}

}