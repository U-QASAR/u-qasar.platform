package eu.uqasar.web.pages.qmtree.quality.indicator.panels;

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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
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

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.vaynberg.wicket.select2.Select2MultiChoice;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.lifecycle.RupStage;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.quality.indicator.Paradigm;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Version;
import eu.uqasar.model.role.Role;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.web.components.StringResourceModelPlaceholderDelegate;
import eu.uqasar.web.components.qmtree.TagsSelectionModal;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.qmtree.panels.QMBaseTreePanel;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorEditPage;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorFormValidator;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorViewPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveViewPage;
import eu.uqasar.web.provider.meta.MetaDataChoiceProvider;


public class QualityIndicatorEditPanel extends QMBaseTreePanel<QMQualityIndicator> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2213926772560599402L;

	private TinyMceBehavior tinyMceBehavior;
	private final TextArea<String> description;
	private final IModel<Boolean> richEnabledModel = Model.of(Boolean.TRUE);
	
	//container for purpose and its attributes
	private final WebMarkupContainer purposeAtt = new WebMarkupContainer("purposeAtt");

	@Inject
	private QMTreeNodeService qmodelService;
	

    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;
	
    private final TagsSelectionModal tagsModal;

	private final Form<QMQualityIndicator> form;
	
	private final FormComponent name, lowerLimit, upperLimit, weight, targetValue;
	private final ComponentFeedbackPanel feedbackName;
	private final ComponentFeedbackPanel feedbackLimits;
	private final ComponentFeedbackPanel feedbackLow;
	private final AjaxButton save;
	private CheckBox chkCreateCopy;

	private final List<Class> metaDataClasses;
	
	private MetaDataChoiceProvider metaDataProvider;
	
	private final Select2MultiChoice select;
	
	public QualityIndicatorEditPanel(String id, final IModel<QMQualityIndicator> model, final boolean isNew) {
		super(id, model);
	
		QMQualityIndicator indicator = model.getObject();

		metaDataClasses = (List<Class>)(List<?>)(MetaData.getAllClasses());

		form = new Form<QMQualityIndicator>("form", model) {
			

			/**
			 * 
			 */
			private static final long serialVersionUID = -5887857218309554465L;

			@Override
			protected void onSubmit() {
				
				QMQualityIndicator qi = model.getObject();
				qi = checkCompleted(qi);
				qmodelService.update(qi);

				//If "create copy" check is selected
				if (chkCreateCopy.getModelObject()) {
					if (qi.getQModel() != null) {
						
						QMQualityIndicator ind = new QMQualityIndicator(qi);
						(qi.getParent()).addChild(ind);
						
						QMTreeNode parent = qmodelService.update(qi.getParent());
						Iterator children = parent.getChildren().iterator();
						boolean found = false;
						QMTreeNode qmn = null;
						while (children.hasNext() && !found){
							qmn = (QMTreeNode)children.next();
							if (qmn.equals(ind)){	
								found=true;
							}
						}

						PageParameters params = new PageParameters();
						params.add("qmodel-key", qi.getQModel().getNodeKey());
							params.add("id", qmn.getId());
							params.add("isNew", true);
							setResponsePage(QMQualityIndicatorEditPage.class, params);
					}
				} else {
					PageParameters parameters = BasePage.appendSuccessMessage(
						QMQualityIndicatorViewPage.forQualityIndicator(getModelObject()),
						new StringResourceModel("treenode.saved.message", this,
								getModel()));
					setResponsePage(QMQualityIndicatorViewPage.class, parameters);
				}
			}
		};
		
		//Name
		form.add(name = new TextField("name", new PropertyModel<>(model, "name")));
		form.add(feedbackName = new ComponentFeedbackPanel("feedbackName", name));
		feedbackName.setOutputMarkupId(true);
				
		// Purpose
		final FormComponent<Purpose> purpose =
				new DropDownChoice<>("purpose", new PropertyModel<Purpose>(indicator, "indicatorPurpose"), Purpose.getAllPurposes());
		purposeAtt.add(purpose);
		// Purpose attributes: process -> paradigm
		final DropDownChoice<Paradigm> paradigm =
				new DropDownChoice<>("attContent", new PropertyModel<Paradigm>(indicator, "paradigm"), Paradigm.getAllParadigms());		
		final Label paradigmLabel = new Label("attLabel", new StringResourceModel("label.indicator.paradigm",this, null));

		// Purpose attributes: product -> version
		final FormComponent<Version> version =
				new DropDownChoice<>("attContent", new PropertyModel<Version>(indicator, "version"), Version.getAllVersions());
		final Label versionLabel = new Label("attLabel", new StringResourceModel("label.indicator.version",this, null));

		final Label stageLabel = new Label("attLabel2", new StringResourceModel("label.indicator.lcStage",this, null));
		final DropDownChoice<LifeCycleStage> lcStage = new DropDownChoice<>("attContent2", new PropertyModel<LifeCycleStage>(indicator, "lifeCycleStage"), LifeCycleStage.getAllLifeCycleStages());
		final DropDownChoice<RupStage> rupStage = new DropDownChoice<>("attContent2", new PropertyModel<RupStage>(indicator, "rupStage"), RupStage.getAllRupStages());
		
		final Label emptyLabel2 = new Label("attLabel2");
		final FormComponent<Purpose> emptyContent2 = new DropDownChoice("attContent2");
		emptyContent2.setVisible(false);
		
		
		
		if (indicator.getIndicatorPurpose().equals(Purpose.Process)) {
			purposeAtt.add(paradigm);
			purposeAtt.add(paradigmLabel);
			purposeAtt.add(stageLabel);
			if (indicator.getParadigm().equals(Paradigm.Waterfall)){
				//Waterfall process
				lcStage.setOutputMarkupId(true);
				purposeAtt.add(lcStage);
			} else {
				//RUP Process
				rupStage.setOutputMarkupId(true);
				purposeAtt.add(rupStage);
			}
		} else {
			purposeAtt.add(version);
			purposeAtt.add(versionLabel);
			purposeAtt.add(emptyLabel2);
			purposeAtt.add(emptyContent2);
		}
		
		version.setOutputMarkupId(true);
		paradigm.setOutputMarkupId(true);

		purpose.setOutputMarkupId(true);
		purpose.setOutputMarkupPlaceholderTag(true);
		purposeAtt.setOutputMarkupId(true);		
		purposeAtt.setOutputMarkupPlaceholderTag(true);		

		form.add(purposeAtt);
		
		purpose.add(new AjaxFormComponentUpdatingBehavior("onchange")
		{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				if (getFormComponent().getConvertedInput().equals(Purpose.Process)){
					purposeAtt.remove(versionLabel);
					purposeAtt.remove(version);
					purposeAtt.remove(emptyLabel2);
					purposeAtt.remove(emptyContent2);
					
					purposeAtt.add(paradigmLabel);
					paradigm.setDefaultModelObject(Paradigm.Waterfall);
					purposeAtt.add(paradigm);
					purposeAtt.add(stageLabel);
					purposeAtt.add(lcStage);
				} else {
					purposeAtt.remove(paradigmLabel);
					purposeAtt.remove(paradigm);
					purposeAtt.remove(stageLabel);
					purposeAtt.remove(rupStage);
					purposeAtt.remove(lcStage);
					
					purposeAtt.add(versionLabel);
					purposeAtt.add(version);
					purposeAtt.add(emptyLabel2);
					purposeAtt.add(emptyContent2);
				}
				if (target != null) {
					target.add(purposeAtt);
				}
			}
		});
		
		paradigm.add(new AjaxFormComponentUpdatingBehavior("onchange")
		{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				if (getFormComponent().getConvertedInput().equals(Paradigm.Waterfall)){
					purposeAtt.remove(rupStage);
					purposeAtt.add(lcStage);
				} else {
					purposeAtt.remove(lcStage);
					purposeAtt.add(rupStage);
				}
				if (target != null) {
					target.add(purposeAtt);
				}
			}
		});
		

		// Description
		form.add(description = new TextArea<>("description",
				new PropertyModel<String>(model, "description")));
		description.add(tinyMceBehavior = new TinyMceBehavior(
				DefaultTinyMCESettings.get()));
		form.add(new AjaxCheckBox("toggle.richtext", richEnabledModel) {
			
		
			/**
			 * 
			 */
			private static final long serialVersionUID = -3568270438060786890L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (getModelObject()) {
					description.add(tinyMceBehavior);
				} else {
					description.remove(tinyMceBehavior);
					tinyMceBehavior = new TinyMceBehavior(
							DefaultTinyMCESettings.get());
				}
						target.add(QualityIndicatorEditPanel.this);
			}
		});
		
		//roles
		final CheckBoxMultipleChoice<Role> targetAudience = 
                new CheckBoxMultipleChoice<>("targetAudience", new PropertyModel<List<Role>>(indicator, "targetAudience"), Role.getAllRoles());
		form.add(targetAudience);
		
		
		//lower and upper limits
		form.add(new Label("limitsLabel", new StringResourceModel("label.indicator.limits",this, null)));
		form.add(new Label("lowerLabel", new StringResourceModel("label.indicator.lower.limit",this, null)));
		form.add(new Label("upperLabel", new StringResourceModel("label.indicator.upper.limit",this, null)));
		
		form.add(lowerLimit = new TextField("lowerLimit", new PropertyModel<>(model, "lowerLimit")));
		lowerLimit.setRequired(false);
		form.add(feedbackLow = new ComponentFeedbackPanel("feedbackLow", lowerLimit));
		feedbackLow.setOutputMarkupId(true);
		
		if (Double.MAX_VALUE == indicator.getUpperLimit()) {
			//TODO empty field
			indicator.setUpperLimit(Double.MIN_VALUE);
		}
		form.add(upperLimit = new TextField("upperLimit", new PropertyModel<>(model, "upperLimit")).setRequired(false));
		
		form.add(feedbackLimits = new ComponentFeedbackPanel("feedbackLimits", upperLimit));
		feedbackLimits.setOutputMarkupId(true);
			
		//Sprint 2. Added QProject attributes
		//targetValue
		form.add(targetValue = new TextField("targetValue", new PropertyModel<>(model, "targetValue")).setRequired(false));
		//Weight
		form.add(weight = new TextField("weight", new PropertyModel<>(model, "weight")).setRequired(false));
		
		
		// add  modal for adding tags
		tagsModal = newTagsSelectionModal();
		tagsModal.setOutputMarkupId(true);
		form.add(tagsModal);

		select = newSelect2("qModelTagData", QModelTagData.class,
				new PropertyModel<Collection<QModelTagData>>(model.getObject(), "qModelTagData"), form);
		form.add(select);		

			if (isNew){
				Button cancel = new Button("cancel"){
					private static final long serialVersionUID = -6733104400111745683L;

					@Override
					public void onSubmit() {
						QMQualityObjective parent = qmodelService.getQualityObjective(model.getObject().getParent().getId());
						qmodelService.removeTreeNode(model.getObject().getId());
						setResponsePage(QMQualityObjectiveViewPage.class,  QMQualityObjectiveViewPage.forQualityObjective(parent));
					}
				};

				cancel.setDefaultFormProcessing(false);
				form.add(cancel);

			} else {

				form.add(new BootstrapBookmarkablePageLink<QMQualityIndicator>(
                        "cancel",
                        QMQualityIndicatorViewPage.class,
                        QMQualityIndicatorViewPage.forQualityIndicator(model.getObject()),
                        de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Default)
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
					form.add(feedbackLimits);
					form.add(feedbackLow);
					target.add(form);
				} 
			});
			save.add(new TinyMceAjaxSubmitModifier());
	        
			//Create additional qi with predefined values
			chkCreateCopy = new CheckBox("checkboxCopy", Model.of(Boolean.FALSE));
			form.add(chkCreateCopy);
			form.add(new Label("checkboxCopyLabel", new StringResourceModel("label.indicator.checkboxCopy",this, null)));
			
			form.setOutputMarkupId(true);
			setOutputMarkupId(true);
			form.add(new QMQualityIndicatorFormValidator(name, lowerLimit, upperLimit));
			add(form);

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
	
	
	private QMQualityIndicator checkCompleted(QMQualityIndicator node) {
		if (node.getChildren().isEmpty()){
			node.setCompleted(false);
			//update quality objective
			node.getParent().setCompleted(false);
			qmodelService.update(node.getParent());
			
			//update quality model
			node.getParent().getParent().setCompleted(false);
			qmodelService.update(node.getParent().getParent());
		}
		return node;
	}
	
	
	private <T extends MetaData> Select2MultiChoice<T> newSelect2(final String id,
            Class<T> clazz, PropertyModel<Collection<T>> model, final Form<?> form) {
        
		metaDataProvider = new MetaDataChoiceProvider<>(metaDataService.getAll(clazz), clazz);
		
		Select2MultiChoice<T> select2MultiChoice = new Select2MultiChoice<>(id, model, metaDataProvider);
		
		select2MultiChoice.add(new AjaxEventBehavior("onchange") { //oninput doesntwork
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				String input = getWebRequest().getRequestParameters().getParameterValue("qModelTagData").toString();
				try {
					if (input!=null && !input.equals("")){
						metaDataProvider.toChoices((Arrays.asList(input.split(","))));
					}
				} catch (EntityNotFoundException ex) {
					//SelectionModal
					tagsModal.setInputSelection(input);
					tagsModal.setTagName(ex.getMessage());
					tagsModal.setVisible(true);
					tagsModal.add(new CssClassNameAppender(""));
					tagsModal.setFadeIn(false);
					tagsModal.show(true);
					target.add(tagsModal);
				}
			}
	});
		
		final IModel<String> placeHolder = new StringResourceModelPlaceholderDelegate("placeholder.meta.input", this, null, MetaData.getLabelModel(clazz));
        select2MultiChoice.getSettings().setCloseOnSelect(false);
        select2MultiChoice.getSettings().setPlaceholder(placeHolder);
        select2MultiChoice.getSettings().setTokenSeparators(new String[]{","});
        select2MultiChoice.getSettings().setCreateSearchChoice(
                "function(term) { if (term.length > 1) { return { id: term, text: term }; } }");
        form.add(new Label("label." + id, new StringResourceModel("label.meta.known", this, null, MetaData.getLabelModel(clazz))));
        form.add(new Label("help." + id, new StringResourceModel("help.meta.input", this, null, MetaData.getLabelModel(clazz))));
        return select2MultiChoice;
    }

	/**
	 * newTagsSelectionModal create a modal window with a dropdown to select the type of metadata to add.
	 * @return TagsSelectionModal
	 */
	private TagsSelectionModal newTagsSelectionModal() {
		final TagsSelectionModal modal = new TagsSelectionModal("tagsModal", metaDataClasses){
			
			@Override
			public boolean onConfirmed(AjaxRequestTarget target) {
				boolean confirmed = false;
				try {
					if (this.getTypeSelected()!=null && !this.getTypeSelected().equals("")){
						Class<?> clazz = Class.forName("eu.uqasar.model.meta."+this.getTypeSelected());

						MetaData created = metaDataService.getByMetaDataOrCreate(clazz, this.getTagName());
						QModelTagData qmtg = metaDataService.getByQMTagData(created.getId());
						
						String input = this.getInputSelection();
						Collection<QModelTagData> ch;
						Set<QModelTagData> set = new HashSet<>();
						if (input!=null){
							input = input.replaceAll(this.getTagName(),String.valueOf(qmtg.getId()));
							ch = metaDataProvider.toChoices((Arrays.asList(input.split(","))));
							for (QModelTagData aCh : ch) {
								set.add(aCh);
							}
						} 
						
						select.setModelObject(set);
						target.add(select);
						confirmed = true;
						this.setInputSelection("");
						this.setTypeSelected("");
						this.setTagName("");
					}
				} catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return confirmed;
			}
		};
		
		modal.setHeaderVisible(false);
		modal.setUseCloseHandler(true);
		return modal;
		}
}
