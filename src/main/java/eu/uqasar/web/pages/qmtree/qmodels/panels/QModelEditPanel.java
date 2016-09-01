package eu.uqasar.web.pages.qmtree.qmodels.panels;

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


import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import eu.uqasar.model.company.Company;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.qmtree.QModelStatus;
import eu.uqasar.model.user.User;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.qmtree.panels.QMBaseTreePanel;
import eu.uqasar.web.pages.qmtree.qmodels.QModelFormValidator;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;

public class QModelEditPanel extends QMBaseTreePanel<QModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6029113750413291859L;
	
	
	@Inject
	private QMTreeNodeService qmodelService;

	private TinyMceBehavior tinyMceBehavior;
	private TextArea<String> description;
	private final IModel<Boolean> richEnabledModel = Model.of(Boolean.TRUE);
	private final Form<QModel> form;

	private final FormComponent name, key;
	private FormComponent<Company> companySelect;
	private final ComponentFeedbackPanel feedbackName;
    private final ComponentFeedbackPanel feedbackKey;
	private final AjaxButton save;
	private final User user;
	//private Company company;
	private CheckBox chkActive;

	
	@Inject
	private CompanyService companyService;
	
	public QModelEditPanel(String id, final IModel<QModel> model, final boolean isNew) {
		super(id, model);

		QModel qmodel = model.getObject();
		
		// set company to qmodel 
		user = UQasar.getSession().getLoggedInUser();

		form = new Form<QModel>("form", model) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 490359842107825248L;

			
			@Override
		    protected void onError() {
				super.onError();
				this.updateFormComponentModels();
			}
			
			@Override
			protected void onSubmit() {
					QModel qm = model.getObject();
					qm.setUpdateDate(DateTime.now().toDate());
					qm.setDescription(description.getModelObject());
					qm.setCompany(companySelect.getConvertedInput());
					//If "create copy" check is selected
					if (!chkActive.getModelObject()) {
						if (qm.getId()!=null){
							QModel old = qmodelService.getQModel(qm.getId());
							if (old.getIsActive().equals(QModelStatus.Active)){
								qm.setIsActive(QModelStatus.OldActive);
							} else {
								qm.setIsActive(QModelStatus.NotActive);
							}
						}
					} else {
						qm.setIsActive(QModelStatus.Active);
					}
					
					qmodelService.update(qm);
					PageParameters parameters = BasePage.appendSuccessMessage(
						QModelViewPage.forQModel(getModelObject()),
						new StringResourceModel("treenode.saved.message", this,
								getModel()));
					setResponsePage(QModelViewPage.class, parameters);
			}
		};
		
		form.add(name = new TextField("name", new PropertyModel<>(model, "name")));
		form.add(feedbackName = new ComponentFeedbackPanel("feedbackName", name));
		feedbackName.setOutputMarkupId(true);
		
		
		form.add(key = new TextField("nodeKey", new PropertyModel<>(model, "nodeKey")));
		form.add(feedbackKey = new ComponentFeedbackPanel("feedbackKey", key));
		feedbackKey.setOutputMarkupId(true);
		
		//TODO auto-increment, not editable
		form.add(new Label("edition", new PropertyModel<>(model, "edition")));
		
		//It is allowed to have more than one active qmodels
		if (qmodel.getIsActive().equals(QModelStatus.Active)){
			chkActive = new CheckBox("checkActive", Model.of(true));
		} else {
			chkActive = new CheckBox("checkActive", Model.of(false));
		}
		
		form.add(chkActive);
		form.add(description = new TextArea<>("description",
				new PropertyModel<String>(model, "description")));
		
		description.add(tinyMceBehavior = new TinyMceBehavior(
				DefaultTinyMCESettings.get()));

		form.add(new AjaxCheckBox("toggle.richtext", richEnabledModel) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4664262448273798008L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (getModelObject()) {
					description.add(tinyMceBehavior);
				} else {
					description.remove(tinyMceBehavior);
					tinyMceBehavior = new TinyMceBehavior(
							DefaultTinyMCESettings.get());
				}
				target.add(QModelEditPanel.this);
			}
		});

		
		// Company
		if (qmodel.getCompany() == null) {
			if (user.getCompany()!=null){
				qmodel.setCompany(user.getCompany());
			}
		}
		companySelect = new DropDownChoice<>("company", Model.of(qmodel.getCompany()), companyService.getAll());
		form.add(companySelect);
		
		if (isNew){
			Button cancel = new Button("cancel"){
				private static final long serialVersionUID = -6733104400111745683L;

				@Override
				public void onSubmit() {
					qmodelService.delete(model.getObject());
					List<QModel> qmodels = qmodelService.getAllQModels();
					if (qmodels == null || qmodels.size() == 0) { 
						setResponsePage(AboutPage.class);
					}
					else { 
						setResponsePage(QModelViewPage.class, 
								QModelViewPage.forQModel(qmodels.get(0)));
					}
	            }
	        };
			cancel.setDefaultFormProcessing(false);
	        form.add(cancel);
			
		} else {
			form.add(new BootstrapBookmarkablePageLink<>("cancel",
                    QModelViewPage.class, QModelViewPage.forQModel(model
                    .getObject()), Type.Default)
					.setLabel(new StringResourceModel("button.cancel", this, null)));
		}
		
		form.add(save = new AjaxButton("save", new StringResourceModel("button.save",
				this, null)){
			/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
					
			@Override 
			protected void onError( AjaxRequestTarget target, Form<?> form ){ 
				form.add(feedbackName);
				form.add(feedbackKey);
				target.add(form);
			} 
		});
		
		
		
		
		save.add(new TinyMceAjaxSubmitModifier());
        
		form.add(new QModelFormValidator(name, key, (model.getObject()).getNodeKey(), isNew));
		add(form);
		
		setOutputMarkupId(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptReferenceHeaderItem
				.forReference(TinyMCESettings.javaScriptReference()));
		  
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}
	
}
