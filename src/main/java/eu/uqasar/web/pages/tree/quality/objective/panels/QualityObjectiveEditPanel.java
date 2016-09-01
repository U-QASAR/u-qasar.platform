package eu.uqasar.web.pages.tree.quality.objective.panels;

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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

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
import eu.uqasar.model.tree.Project;
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
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveEditPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveViewPage;

public class QualityObjectiveEditPanel extends BaseTreePanel<QualityObjective> {

	private static final long serialVersionUID = -2819662546551975700L;

	private TinyMceBehavior tinyMceBehavior;
	private final TextArea<String> description;
	private final IModel<Boolean> richEnabledModel = Model.of(Boolean.TRUE);
	private final List<QualityIndicator> indicators = new ArrayList<>();
	private final Logger logger = Logger.getLogger(QualityObjectiveEditPanel.class);
	private final TextArea<String> hiddenTextField;

	private final Form<QualityObjective> form;
	
	@Inject
	TreeNodeService treeNodeService;
	
	private CheckBox chkCreateCopy;

	public QualityObjectiveEditPanel(final String id, final IModel<QualityObjective> model,  final boolean isNew) {
		super(id, model);
		
		// Get the children
		final List<TreeNode> nodes = model.getObject().getChildren();
		for (final TreeNode node : nodes) {
			if (node instanceof QualityIndicator) {
				final QualityIndicator indicator = (QualityIndicator) node;
				indicators.add(indicator);				
			}
		}
		
		form = new Form<QualityObjective>("form", model) {

			private static final long serialVersionUID = 7686399398380752909L;

			@Override
			protected void onSubmit() {
				
				String formulaToEval = Formula.parseFormula(model.getObject().getViewFormula());
				final Float computedValue = Formula.evalFormula(formulaToEval);
				if (computedValue != null) {
					getModelObject().setValue(computedValue);
				}

				// Update the tree on save
				final QualityObjective qobj = model.getObject();
				UQasarUtil.updateTreeWithParticularNode(getModelObject().getProject(),qobj);

				//If "create copy" check is selected
				if (chkCreateCopy.getModelObject()) {
					logger.info("The user has selected to create a new copy of Quality Objective : " + qobj.getName());

					final QualityObjective obj = new QualityObjective(qobj);
					(qobj.getParent()).addChild(obj);

					final TreeNode parent = treeNodeService.update(qobj.getParent());
					final Iterator<TreeNode> children = parent.getChildren().iterator();
					boolean found = false;
					TreeNode qon = null;
					while (children.hasNext() && !found){
						qon = children.next();
						if (qon.equals(obj)){	
							found=true;
						}
					}

					// Update the tree on save
					UQasarUtil.updateTreeWithParticularNode(getModelObject().getProject(),qon);

					final PageParameters params = new PageParameters();
					params.add("id", qon.getId());
					params.add("isNew", true);
					setResponsePage(QualityObjectiveEditPage.class, params);
				} else {

					final PageParameters parameters = BasePage.appendSuccessMessage(
							QualityObjectiveViewPage.forQualityObjective(getModelObject()),
							new StringResourceModel("treenode.saved.message", this,
									getModel()));
					setResponsePage(QualityObjectiveViewPage.class, parameters);
				}
			}
		};

		hiddenTextField = new TextArea<>("hidden", new PropertyModel<String>(model, "viewFormula"));
		form.add(hiddenTextField);
		
		form.add(new TextField<>("name", new PropertyModel<>(model, "name"))
				.add(new AttributeAppender("maxlength", 255)));

		form.add(description = new TextArea<>("description",
                new PropertyModel<String>(model, "description")));

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
				target.add(QualityObjectiveEditPanel.this);
			}
		});

		// Threshold indicator
		form.add(new ThresholdEditor<>("thresholdEditor", model));

		// Selector to choose calculation mode
		form.add(new DropDownChoice<>("useFormula", new PropertyModel<>(model, "useFormula"), Arrays.asList(Boolean.TRUE, Boolean.FALSE) ));

		form.add(new TextField<>("targetValue",  new PropertyModel<>(model, "targetValue")));

		form.add(new TextField<>("weight", new PropertyModel<>(model, "weight")));
		
		// Weight indicator shows data of weight consumed and available
		form.add(new WeightIndicator<>("totalWeight", model));
		
		final JQueryTextCompleteEditor<Object> formulaEditor = new JQueryTextCompleteEditor<>("autocomp-text", new PropertyModel<>(model, "viewFormula"));
		
		// URL for obtaining the children of the treenode
		final String url = UQasar.get().getHomePageUrl() + "/rest/treenodes/" +model.getObject().getId();
		
		formulaEditor.setAutocompleteDataUrl(url);
		form.add(formulaEditor);

		if (isNew) {
			final Button cancel = new Button("cancel"){

				private static final long serialVersionUID = 2853864840969260600L;

				public void onSubmit() {
					final Project parent = treeNodeService.getProject(model.getObject().getParent().getId());
					treeNodeService.removeTreeNode(model.getObject().getId());
					setResponsePage(ProjectViewPage.class, ProjectViewPage.forProject(parent));
	            }
	        };
	        cancel.setDefaultFormProcessing(false);
	        form.add(cancel);			
		} else {			
			form.add(new BootstrapBookmarkablePageLink<>(
                    "cancel",
                    QualityObjectiveViewPage.class,
                    QualityObjectiveViewPage.forQualityObjective(model.getObject()),
                    de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Default)
					.setLabel(new StringResourceModel("button.cancel", this, null)));			
		}

		form.add(new Button("save", new StringResourceModel("button.save",
				this, null)));

		//Create additional qo with predefined values
		chkCreateCopy = new CheckBox("checkboxCopy", Model.of(Boolean.FALSE));
		form.add(chkCreateCopy);
		form.add(new Label("checkboxCopyLabel", new StringResourceModel("label.metric.checkboxCopy",this, null)));

		add(form);
		setOutputMarkupId(true);
	}

	@Override
	public void renderHead(final IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptReferenceHeaderItem
				.forReference(TinyMCESettings.javaScriptReference()));
	}		
}
