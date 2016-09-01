package eu.uqasar.web.dashboard;

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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetDescriptor;
import ro.fortsoft.wicket.dashboard.WidgetFactory;
import ro.fortsoft.wicket.dashboard.WidgetLocation;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.user.User;
import eu.uqasar.service.DashboardService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.processes.ProcessManagementPage;

public class DashboardEditPage extends BasePage {

	private static final long serialVersionUID = 6315866033652820606L;

	@Inject
	private DashboardService dashboardService;
	@Inject
	private UserService userService;
	private final Form<Dashboard> dashboardForm;
	@SuppressWarnings("unused")
	private final InputBorder<String> titleValidationBorder;
	@SuppressWarnings("unused")
	private final InputBorder<Integer> columnValidationBorder; 
	private final AjaxCheckBox suggestionCheck, allWidgetCheck;	

	private final transient DashboardContext dashboardContext = UQasar.get().getDashboardContext();
	// The dashboard to edit/save
    private DbDashboard dashboard;

	/**
	 * @param parameters
	 */
	public DashboardEditPage(final PageParameters parameters) {
		super(parameters);

		loadDashboard(parameters.get("id"));

		add(dashboardForm = newDashboardForm());
		dashboardForm.add(titleValidationBorder = newTitleTextField());
		dashboardForm.add(columnValidationBorder = newColumnTextField());

		// Checkbox to get a suggested dashboard setup
		suggestionCheck = new AjaxCheckBox("suggestion-check", Model.of(Boolean.TRUE)) {
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// If suggestioncheck is checked and also addallwidget is checked, uncheck it 
				if (suggestionCheck.getModelObject().equals(true) && allWidgetCheck.getModelObject().equals(true)) {
					allWidgetCheck.setModel(Model.of(Boolean.FALSE));
				}
				target.add(suggestionCheck);
				target.add(allWidgetCheck);
			}
		};
		dashboardForm.add(suggestionCheck);

		// Checkbox to create dashboard with all widgets.
		allWidgetCheck = new AjaxCheckBox("allWidget-check", Model.of(Boolean.TRUE)) {
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// If add all widget is checked and also suggestioncheck is checked, uncheck it
				if (allWidgetCheck.getModelObject().equals(true) && suggestionCheck.getModelObject().equals(true)) {
					suggestionCheck.setModel(Model.of(Boolean.FALSE));
				} 
				target.add(suggestionCheck);
				target.add(allWidgetCheck);
			}
		};
		allWidgetCheck.setOutputMarkupId(true);
		dashboardForm.add(allWidgetCheck);

		// add a button to create new dashboard
		dashboardForm.add(newSubmitLink());
		dashboardForm.add(newCancelLink());
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		if (suggestionCheck.getModelObject()) {
			allWidgetCheck.setModelObject(false);
			
		} else {
			suggestionCheck.setModelObject(false);
			allWidgetCheck.setModelObject(true);
		}		
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.markup.html.WebPage#onAfterRender()
	 */
	@Override
	protected void onAfterRender() {
		super.onAfterRender();
		// detach entity to avoid automatic update of changes in form.
		dashboardService.detach(dashboard);
	}

	/**
	 * 
	 * @param idParam
	 */
    private void loadDashboard(final StringValue idParam) {
		// Create a new dashboard, if no ID is provided 
		if (idParam.isEmpty()) {
			setPageTitle(new StringResourceModel("page.create.title", this,
					null));
			add(new Label("header", new StringResourceModel(
					"form.create.header", this, null)));
			String id = String.valueOf(new Date().getTime());
			String title = "Dashboard-" +UQasar.getSession().getLoggedInUser().getUserName() +"-" +id;
			dashboard = new DbDashboard(id, title);
		} else {
			setPageTitle(new StringResourceModel("page.edit.title", this, null));
			add(new Label("header", new StringResourceModel("form.edit.header",
					this, null)));
			// set the item we got from previous page
			try {
				dashboard = dashboardService
						.getById(idParam.toOptionalLong());
			} catch (Exception e) {
				throw new RestartResponseException(AboutPage.class);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private Form<Dashboard> newDashboardForm() {
		Form<Dashboard> form = new InputValidationForm<>("form");
		form.setOutputMarkupId(true);
		return form;
	}

	/**
	 * 
	 * @return
	 */
	private InputBorder<String> newTitleTextField() {
		return new OnEventInputBeanValidationBorder<>(
                "nameValidationBorder", new TextField<>("title",
                new PropertyModel<String>(dashboard, "title"))
                .setRequired(true),
                new StringResourceModel("title.input.label", this, null),
                HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return
	 */
	private InputBorder<Integer> newColumnTextField() {
		return new OnEventInputBeanValidationBorder<>(
                "columnValidationBorder", new TextField<>("columnCount",
                new PropertyModel<Integer>(dashboard, "columnCount"))
                .setRequired(false),
                new StringResourceModel("columncount.input.label", this, null),
                HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return
	 */
	private AjaxSubmitLink newSubmitLink() {
		return new AjaxSubmitLink("submit", dashboardForm) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 8233961185708082469L;

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
	 * 
	 * @return
	 */
	private Link<ProcessManagementPage> newCancelLink() {
		return new Link<ProcessManagementPage>("cancel") {

			private static final long serialVersionUID = -3695465420798817598L;

			@Override
			public void onClick() {
				setResponsePage(AboutPage.class);
			}
		};
	}
	

	/**
	 * 
	 */
    private void save(AjaxRequestTarget target) {
		DbDashboard dbdb = (DbDashboard) saveDashboard();
		User user = userService.getById(UQasar.getSession().getLoggedInUser().getId());
		user.addDashboard(dbdb);
		userService.update(user);
		PageParameters params = new PageParameters();
		params.add("id", dbdb.getId());
		params.add("useSuggestion", this.getSuggestionCheck().getValue());
		params.add("AllWidgets", this.getAllWidgetCheck().getValue());
		setResponsePage(DashboardViewPage.class, params);
	}

	/**
	 * Save the dashboard
	 */
	private Dashboard saveDashboard() {
		
		if(this.getAllWidgetCheck().getModel().getObject()){
			dashboard = getDefaultDashboard(dashboard);
		}  

		return dashboardService.create(dashboard);
	}

	/**
	 * 
	 * @param target
	 */
    private void showErrors(AjaxRequestTarget target) {
		// in case of errors (e.g. validation errors) show error
		// messages in form
		target.add(dashboardForm);
	}
	
	private DbDashboard getDefaultDashboard(DbDashboard dbDash) {
		List<WidgetDescriptor> descr = dashboardContext.getWidgetRegistry().getWidgetDescriptors();
		int col = 0;
		int row = 0;
		for (WidgetDescriptor widgetDescriptor : descr) {
			WidgetFactory widgetFactory = 
					dashboardContext.getWidgetFactory();
			Model<WidgetDescriptor> item =
                    new Model<>(widgetDescriptor);
			Widget widget = widgetFactory.createWidget(item.getObject());
			WidgetLocation location = new WidgetLocation(col, row);
			widget.setLocation(location);
			dbDash.addWidget(widget);           
			// Update the widget location for the next round
			if (col == 0) {
				col = 1;
			} else if (col == 1) {
				col = 0;
				row++;
			}
		}

		return dbDash;
	}

	/**
	 * @return the suggestionCheck
	 */
    private CheckBox getSuggestionCheck() {
		return suggestionCheck;
	}

	
	private CheckBox getAllWidgetCheck() {
		return allWidgetCheck;
	}
}
