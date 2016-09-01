package eu.uqasar.web.pages.tree.metric.panels;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.metric.MetricEditPage;
import eu.uqasar.web.pages.tree.metric.MetricViewPage;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;
import eu.uqasar.web.pages.tree.panels.thresholds.ThresholdEditor;
import eu.uqasar.web.pages.tree.panels.weight.WeightIndicator;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorViewPage;

public class MetricEditPanel extends BaseTreePanel<Metric> {

	private static final long serialVersionUID = 734784522589149171L;

	private TinyMceBehavior tinyMceBehavior;
	private final TextArea<String> description;
	private final IModel<Boolean> richEnabledModel = Model.of(Boolean.TRUE);
	private final Form<Metric> form;
	private static final Logger logger = Logger.getLogger(MetricEditPanel.class);

	@Inject
	private TreeNodeService treeNodeService;
	// map:source of metrics->list of metric types
	private final Map<MetricSource, List<String>> metricsMap = new HashMap<>();
	private final TextField valueField;
	
	
	//TODO: [Manu] maybe this two next attributes could be delete? Are they going to be used?
		
    // Project name used by Sonar and TestLink adapters
	private String adapterProjectName;
    // TestLink plan name
    private String planName;
	
	private CheckBox chkCreateCopy;

	public MetricEditPanel(String id, final IModel<Metric> model, boolean isNew) {
		super(id, model);

		MetricSource metricSource = null;
		List<String> metricTypesUsed = null;
		
		System.out.println(model.getObject().getProject().getAdapterSettings().toString());
		
		// Iterate the adapter settings for the project
		for (AdapterSettings settings : model.getObject().getProject().getAdapterSettings()) {
			metricSource = settings.getMetricSource();
			if (metricSource.equals(MetricSource.IssueTracker)) {
				metricTypesUsed = UQasarUtil.getJiraMetricNames();
			} else if (metricSource.equals(MetricSource.StaticAnalysis)) {
				metricTypesUsed = UQasarUtil.getSonarMetricNames();
				setAdapterProjectName(settings.getAdapterProject());
			} else if (metricSource.equals(MetricSource.TestingFramework)) {
                metricTypesUsed = UQasarUtil.getTestLinkMetricNames();
                setPlanName(settings.getAdapterProject());
            } else if (metricSource.equals(MetricSource.CubeAnalysis)) {
                metricTypesUsed = UQasarUtil.getCubesMetricNames();
            } else if (metricSource.equals(MetricSource.ContinuousIntegration)) {
            	metricTypesUsed = UQasarUtil.getJenkinsMetricNames();
            } else {
				metricTypesUsed = new ArrayList<>();
			}			
			metricsMap.put(metricSource, metricTypesUsed);
		}
		// Add the option for manual data
		metricsMap.put(MetricSource.Manual, null);		
        
		// Metric source choices
		IModel<List<? extends MetricSource>> metricSourceChoices = 
				new AbstractReadOnlyModel<List<? extends MetricSource>>() {

			private static final long serialVersionUID = 2610709400336511400L;

			@Override
			public List<MetricSource> getObject()	{
				logger.info(metricsMap.keySet());
				return new ArrayList<>(metricsMap.keySet());
			}
		};

		// Metric type choices
		IModel<List<? extends String>> metricTypeChoices = 
				new AbstractReadOnlyModel<List<? extends String>>() {

			private static final long serialVersionUID = 785545686808048810L;

			@Override
			public List<String> getObject()	{
				List<String> metricTypes = metricsMap.get(model.getObject().getMetricSource());
				if (metricTypes == null)	{
					metricTypes = Collections.emptyList();
				}
				return metricTypes;
			}
		};

		form = new Form<Metric>("form", model) {
			private static final long serialVersionUID = 6993544095735400633L;

			@Override
			protected void onSubmit() {
				
				// If manual metrics source
//				if (getModelObject().getMetricSource().equals(MetricSource.Manual)) {
//					float value = 0;
//					if (valueField.getValue() != null) 
//						value = Float.valueOf(valueField.getValue());
//					getModelObject().setValue(value);
//				}
				
			    Metric met = model.getObject();
				// Update the tree on save
			    UQasarUtil.updateTreeWithParticularNode(getModelObject().getProject(),met);
				
				// Set last updated
				getModelObject().setLastUpdated(new Date());
				
				//If "create copy" check is selected
				if (chkCreateCopy.getModelObject()) {

					Metric metric = new Metric(met);
					(met.getParent()).addChild(metric);

					TreeNode parent = treeNodeService.update(met.getParent());
					Iterator children = parent.getChildren().iterator();
					boolean found = false;
					TreeNode qmn = null;
					while (children.hasNext() && !found){
						qmn = (TreeNode)children.next();
						if (qmn.equals(metric)){	
							found=true;
						}
					}

					// Update the tree on save
					UQasarUtil.updateTreeWithParticularNode(getModelObject().getProject(),met);

					PageParameters params = new PageParameters();
					params.add("id", qmn.getId());
					params.add("isNew", true);
					setResponsePage(MetricEditPage.class, params);
				} else {

					PageParameters parameters = BasePage.appendSuccessMessage(
							MetricViewPage.forMetric(getModelObject()),
							new StringResourceModel("treenode.saved.message", this,
									getModel()));
					setResponsePage(MetricViewPage.class, parameters);
				}
				
			}
		};

		form.add(new TextField<>("name", new PropertyModel<>(model, "name"))
				.add(new AttributeAppender("maxlength", 255)));

		form.add(new DropDownChoice<>("purpose",
                new PropertyModel<Purpose>(model, "indicatorPurpose"), Arrays
                .asList(Purpose.values())));

		form.add(new DropDownChoice<>("lcStage",
                new PropertyModel<LifeCycleStage>(model, "lifeCycleStage"),
                Arrays.asList(LifeCycleStage.values())));
		
		form.add(new TextField<>("unit", new PropertyModel<>(model,"unit")));

		final DropDownChoice<MetricSource> metricSources =
                new DropDownChoice<>("metricSources",
                        new PropertyModel<MetricSource>(model, "metricSource"),
                        metricSourceChoices);
		form.add(metricSources);

		final DropDownChoice<String> metricTypes =
                new DropDownChoice<>("metricTypes",
                        new PropertyModel<String>(model, "metricType"),
                        metricTypeChoices);
		metricTypes.setOutputMarkupId(true);		
		form.add(metricTypes);

		// Metric value
		valueField = new TextField<>("metricValue", 
				new PropertyModel<>(model, "value")); 
		switchValueField(metricSources.getModelObject());
		valueField.setOutputMarkupId(true);
		form.add(valueField).add(new AttributeAppender("maxlength", 255));

		metricSources.add(new AjaxFormComponentUpdatingBehavior("onchange")	{

			private static final long serialVersionUID = -8410045826567595007L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				target.add(metricTypes);
				target.add(valueField);
				switchValueField(metricSources.getModelObject());
			}
		});

		metricTypes.add(new AjaxFormComponentUpdatingBehavior("onchange")	{
			
			private static final long serialVersionUID = 8702053900619896141L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(metricTypes);
				target.add(valueField);
			}
		});

		// Threshold indicator
		form.add(new ThresholdEditor<>("thresholdEditor", model));
		
		form.add(new TextField<>("targetValue", 
				new PropertyModel<>(model, "targetValue")));
		form.add(new TextField<>("weight", 
				new PropertyModel<>(model, "weight")));
		
		// Weight indicator shows data of weight consumed and available
		form.add(new WeightIndicator<>("totalWeight", model));

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
				target.add(MetricEditPanel.this);
			}
		});

		if (isNew) {
			Button cancel = new Button("cancel"){

				private static final long serialVersionUID = 1892636210626847636L;

				public void onSubmit() {
					QualityIndicator parent = treeNodeService.getQualityIndicator(model.getObject().getParent().getId());
					treeNodeService.removeTreeNode(model.getObject().getId());
					setResponsePage(QualityIndicatorViewPage.class, QualityIndicatorViewPage.forQualityIndicator(parent));
	            }
	        };
			
	        cancel.setDefaultFormProcessing(false);
	        form.add(cancel);

		} else {
			
			form.add(new BootstrapBookmarkablePageLink<>(
                    "cancel",
                    MetricViewPage.class,
                    MetricViewPage.forMetric(model.getObject()),
                    de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Default)
					.setLabel(new StringResourceModel("button.cancel", this, null)));			
		}

		//Create additional metric with predefined values
		chkCreateCopy = new CheckBox("checkboxCopy", Model.of(Boolean.FALSE));
		form.add(chkCreateCopy);
		form.add(new Label("checkboxCopyLabel", new StringResourceModel("label.metric.checkboxCopy",this, null)));

		
		form.add(new Button("save", new StringResourceModel("button.save",
				this, null)));

		add(form);
		setOutputMarkupId(true);
	}

    private void switchValueField(MetricSource metricSource) {
		// TODO Auto-generated method stub
		if(metricSource == null){
			valueField.setEnabled(true);
		}
		else if (metricSource.equals(MetricSource.Manual)){
			valueField.setEnabled(true);			
		}
		else{
			valueField.setEnabled(false);
		}
	}

	/**
     * 
     * @return 
     */
    public String getPlanName() {
        return planName;
    }

    /**
     * 
     * @param planName 
     */
    private void setPlanName(String planName) {
        this.planName = planName;
    }
    
    /**
     * 
     * @return 
     */
    public String getAdapterProjectName() {
        return adapterProjectName;
    }

    /**
     * 
     * @param adapterProjectName 
     */
    private void setAdapterProjectName(String adapterProjectName) {
        this.adapterProjectName = adapterProjectName;
    }

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptReferenceHeaderItem
				.forReference(TinyMCESettings.javaScriptReference()));
	}	
}
