package eu.uqasar.web.pages.tree.quality.indicator.panels;

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
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;
import javax.script.ScriptException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import com.googlecode.wicket.jquery.ui.form.button.Button;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import eu.uqasar.model.formula.Formula;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Type;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.jquerytextcomplete.JQueryTextCompleteEditor;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;
import eu.uqasar.web.pages.tree.panels.thresholds.ThresholdEditor;
import eu.uqasar.web.pages.tree.panels.weight.WeightIndicator;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorEditPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveViewPage;

public class QualityIndicatorEditPanel extends BaseTreePanel<QualityIndicator> {

	private static final long serialVersionUID = -1817961330159870190L;

	private TinyMceBehavior tinyMceBehavior;
	private TextArea<String> description;
	private TextArea<String> hiddenTextField;
	private IModel<Boolean> richEnabledModel = Model.of(Boolean.TRUE);
	private JQueryTextCompleteEditor<String> formulaEditor;
	
	private final Form<QualityIndicator> form;
	private Logger logger = Logger.getLogger(QualityIndicatorEditPanel.class);

	@Inject
	TreeNodeService treeNodeService;

	private CheckBox chkCreateCopy;

	public QualityIndicatorEditPanel(String id, final IModel<QualityIndicator> model, boolean isNew) {
		super(id, model);

		final QualityIndicator qind = model.getObject();
		
		form = new Form<QualityIndicator>("form", model) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				// Set last updated
				getModelObject().setLastUpdated(new Date());
				
				String formulaToEval = Formula.parseFormula(
						model.getObject().getViewFormula());
				Float computedValue = Formula.evalFormula(formulaToEval);
				if (computedValue != null) {
					getModelObject().setValue(computedValue);
				}


				// Update the tree on save
				UQasarUtil.updateTreeWithParticularNode(getModelObject().getProject(),qind);
				
				
				//If "create copy" check is selected
				if (chkCreateCopy.getModelObject()) {
					logger.info("The user has selected to create a new copy of Quality Indicator : " + qind.getName());
					final QualityIndicator ind = new QualityIndicator(qind);
					(qind.getParent()).addChild(ind);

					final TreeNode parent = treeNodeService.update(qind.getParent());
					final Iterator<TreeNode> children = parent.getChildren().iterator();
					boolean found = false;
					TreeNode qin = null;
					while (children.hasNext() && !found){
						qin = (TreeNode)children.next();
						if (qin.equals(ind)){	
							found=true;
						}
					}

					// Update the tree on save
					UQasarUtil.updateTreeWithParticularNode(getModelObject().getProject(),qind);

					final PageParameters params = new PageParameters();
					params.add("id", qin.getId());
					params.add("isNew", true);
					setResponsePage(QualityIndicatorEditPage.class, params);
				} else {

					final PageParameters parameters = BasePage.appendSuccessMessage(
							QualityObjectiveViewPage.forQualityIndicator(getModelObject()),
							new StringResourceModel("treenode.saved.message", this,
									getModel()));
					setResponsePage(QualityIndicatorViewPage.class, parameters);
				}
				
			}
		};

		hiddenTextField = new TextArea<String>("hidden", new PropertyModel<String>(model, "viewFormula"));
		form.add(hiddenTextField);
		
		form.add(new TextField<>("name", new PropertyModel<>(model, "name"))
				.add(new AttributeAppender("maxlength", 255)));

		form.add(new DropDownChoice<Type>("type", new PropertyModel<Type>(
				model, "indicatorType"), Arrays.asList(Type.values())));

		form.add(new DropDownChoice<Purpose>("purpose",
				new PropertyModel<Purpose>(model, "indicatorPurpose"), Arrays
				.asList(Purpose.values())));

		form.add(new DropDownChoice<LifeCycleStage>("lcStage",
				new PropertyModel<LifeCycleStage>(model, "lifeCycleStage"),
				Arrays.asList(LifeCycleStage.values())));

		form.add(description = new TextArea<String>("description",
				new PropertyModel<String>(model, "description")));

		// Threshold indicator
		form.add(new ThresholdEditor<QualityIndicator>("thresholdEditor", model));
		
		// Selector to choose calculation mode
		form.add(new DropDownChoice<>("useFormula", new PropertyModel<>(model, "useFormula"), Arrays.asList(Boolean.TRUE, Boolean.FALSE) ));

		form.add(new TextField<>("targetValue", new PropertyModel<>(model, "targetValue")));

		form.add(new TextField<>("weight", new PropertyModel<>(model, "weight")));
		
		// Weight indicator shows data of weight consumed and available
		form.add(new WeightIndicator<QualityIndicator>("totalWeight",model));

		formulaEditor = new JQueryTextCompleteEditor<>("formulaEditor",  new PropertyModel<String>(model, "viewFormula"));
				
		// URL for obtaining the children of the treenode
		final String url = UQasar.get().getHomePageUrl() + "/rest/treenodes/" +model.getObject().getId();
		formulaEditor.setAutocompleteDataUrl(url);
		form.add(formulaEditor);
		
		description.add(tinyMceBehavior = new TinyMceBehavior(
				DefaultTinyMCESettings.get()));
		form.add(new AjaxCheckBox("toggle.richtext", richEnabledModel) {
			private static final long serialVersionUID = 6814340728713542784L;

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

		if (isNew) {
			Button cancel = new Button("cancel"){

				private static final long serialVersionUID = 9040876722342624880L;

				public void onSubmit() {
					QualityObjective parent = treeNodeService.getQualityObjective(model.getObject().getParent().getId());
					treeNodeService.removeTreeNode(model.getObject().getId());
					setResponsePage(QualityObjectiveViewPage.class, QualityObjectiveViewPage.forQualityObjective(parent));
				}
			};
			cancel.setDefaultFormProcessing(false);
			form.add(cancel);			
		} else {
			form.add(new BootstrapBookmarkablePageLink<QualityIndicatorViewPage>(
					"cancel",
					QualityIndicatorViewPage.class,
					QualityIndicatorViewPage.forQualityIndicator(model.getObject()),
					de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Default)
					.setLabel(new StringResourceModel("button.cancel", this, null)));
		}
		form.add(new Button("save", new StringResourceModel("button.save",
				this, null)));

		//Create additional qi with predefined values
		chkCreateCopy = new CheckBox("checkboxCopy", Model.of(Boolean.FALSE));
		form.add(chkCreateCopy);
		form.add(new Label("checkboxCopyLabel", new StringResourceModel("label.metric.checkboxCopy",this, null)));

		add(form);
		setOutputMarkupId(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptReferenceHeaderItem
				.forReference(TinyMCESettings.javaScriptReference()));
	}		
	
	
	
	
}
