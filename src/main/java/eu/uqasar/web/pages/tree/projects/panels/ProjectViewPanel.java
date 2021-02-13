package eu.uqasar.web.pages.tree.projects.panels;

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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.ProgressBar;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.meta.CustomerType;
import eu.uqasar.model.meta.IssueTrackingTool;
import eu.uqasar.model.meta.ProgrammingLanguage;
import eu.uqasar.model.meta.ProjectType;
import eu.uqasar.model.meta.SoftwareDevelopmentMethodology;
import eu.uqasar.model.meta.SoftwareLicense;
import eu.uqasar.model.meta.SoftwareType;
import eu.uqasar.model.meta.SourceCodeManagementTool;
import eu.uqasar.model.meta.StaticAnalysisTool;
import eu.uqasar.model.meta.TestManagementTool;
import eu.uqasar.model.meta.Topic;
import eu.uqasar.model.process.Process;
import eu.uqasar.model.product.Product;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.historic.HistoricValuesProject;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.User;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.components.historical.SnapshotRecoverConfirmationModal;
import eu.uqasar.web.components.historical.SnapshotSaveConfirmationModal;
import eu.uqasar.web.components.user.LinkableUserBadge;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.historic.project.HistoricProjectPage;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;
import eu.uqasar.web.pages.tree.panels.charts.BaseTrendChartPanel;
import eu.uqasar.web.pages.tree.panels.thresholds.ThresholdIndicator;
import eu.uqasar.web.pages.tree.projects.ProjectEditPage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.snapshot.SnapshotPage;

public class ProjectViewPanel extends BaseTreePanel<Project> {

	private static final long serialVersionUID = 1L;

	@Inject
	private HistoricalDataService historicalDataService;

	private final WebMarkupContainer wmcProgLan, wmcItTool, wmcScmTool, wmcSaTool, wmcTmTool, wmcCiTool, wmcCtype, wmcPtype,
	wmcSwType, wmcSwLicense, wmcTopic, wmcSwDevMeth;

	private final WebMarkupContainer qmodelWebMarkUp,productWebMarkUp,processWebMarkUp, descriptionWebMarkUp;

	private SnapshotSaveConfirmationModal saveSnapshotModal;
	private SnapshotRecoverConfirmationModal recoverSnapshotModal;

	private final Project project;

	public ProjectViewPanel(String id, final IModel<Project> model) {
		super(id, model);

		project = model.getObject();
		QualityStatus qs = project.getQualityStatus();

		add(new Label("name", new PropertyModel<>(model, "name")));

		Label iconLabel = new Label("icon");
		iconLabel.add(new CssClassNameAppender(project.getIconType()
				.cssClassName()));
		iconLabel.add(new AttributeAppender("class", new Model(qs
				.getCssClassName()), " "));
		add(iconLabel);

		// TODO only show if authorized to edit
		BookmarkablePageLink<ProjectEditPage> editLink = new BookmarkablePageLink<>(
				"link.edit", ProjectEditPage.class,
				BaseTreePage.forProject(project));
		add(editLink);

		// add quality status and value
		WebMarkupContainer qualityStatus = new WebMarkupContainer(
				"quality.status");
		qualityStatus.add(new CssClassNameAppender(qs.getCssClassName()));
		Label qualityValue = null;
		// Round the value to the nearest integer if possible
		if (project.getValue() == null || project.getValue().isNaN()
				|| project.getValue().isInfinite()) {
			qualityValue = new Label("quality.value", Model.of(project
					.getValue()));
		} else {
			qualityValue = new Label("quality.value", Model.of(Math
					.round(project.getValue())));
		}
		qualityValue.add(new CssClassNameAppender(qs.getCssClassName()));
		qualityStatus.add(qualityValue);
		add(qualityStatus);

		add(new Label("nodeKey", new PropertyModel<>(model, "nodeKey")));
		SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, Session.get().getLocale());
		PatternDateConverter pdc = new PatternDateConverter(df.toPattern(), true);        
		add(new DateLabel("startDate", new PropertyModel<Date>(model, "startDate"), pdc));
		add(new DateLabel("endDate", new PropertyModel<Date>(model, "endDate"), pdc));

		// QModel WMC
		qmodelWebMarkUp = new WebMarkupContainer("qmodelWebMarkUp");
		add(qmodelWebMarkUp.add(newLabelWithWMC("qmodel", new PropertyModel<Collection<QModel>>(model,
				"qmodel"), qmodelWebMarkUp)));
		// Product WMC
		productWebMarkUp = new WebMarkupContainer("productWebMarkUp");
		add(productWebMarkUp.add(newLabelWithWMC("product", new PropertyModel<Collection<Product>>(model,
				"product"), productWebMarkUp)));
		// Process WMC
		processWebMarkUp = new WebMarkupContainer("processWebMarkUp");
		add(processWebMarkUp.add(newLabelWithWMC("process", new PropertyModel<Collection<Process>>(model,
				"process"), processWebMarkUp)));

		add(new Label("lastUpdated", new PropertyModel<Date>(model,
				"lastUpdated")));

		// Threshold indicator
		add(new ThresholdIndicator<>("thresholdIndicator", model));

		add(new Label("formulaAverage", new PropertyModel<>(model,
				"formulaAverage")));
		add(new Label("lcStage.current", new PropertyModel<LifeCycleStage>(
				model, "lifeCycleStage")).setRenderBodyOnly(true));

		// Save snapshot button and Modal
		add(saveSnapButton());
		add(saveSnapshotModal = newSaveSnapshotModal());
		add(recoverSnapButton());
		add(recoverSnapshotModal = newRecoverSnapshotModal());

        // Description WMC
        descriptionWebMarkUp = new WebMarkupContainer("descriptionWebMarkUp");
        add(descriptionWebMarkUp.add(newLabelWithWMC("description", new PropertyModel<Collection<String>>(model,
            "description"), descriptionWebMarkUp).setEscapeModelStrings(false)));

		StringResourceModel projectProgressText = new StringResourceModel(
				"progress.status.title", null, project.getElapsedDays(),
				project.getDurationInDays(),
				project.getDateProgressInPercent(), project.getRemainingDays());
		ProgressBar progress = new ProgressBar("progress.bar", Model.of(project
				.getDateProgressInPercent()));
		add(progress);
		progress.add(new AttributeModifier("title", projectProgressText));

		add(new Label("progress.text", projectProgressText));

		// check for Team tag.
		Label teamLabel;
		List<User> users = getUsersOfAllTeamsPerProject(project);
		if (users.size() == 0) {
			teamLabel = new Label("teamLabelId", "Team:") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible() {
					return false;
				}
			};
		} else {
			teamLabel = new Label("teamLabelId", "Team:");
		}
		add(teamLabel);
		add(new DataView<User>("users", new ListDataProvider<>(users)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<User> item) {
				item.add(new LinkableUserBadge("userPanel", item.getModel()).setRenderBodyOnly(true));
			}
		});

		// metadata
		wmcProgLan = new WebMarkupContainer("wmcProgLan");
		wmcItTool = new WebMarkupContainer("wmcItTool");
		wmcScmTool = new WebMarkupContainer("wmcScmTool");
		wmcSaTool = new WebMarkupContainer("wmcSaTool");
		wmcTmTool = new WebMarkupContainer("wmcTmTool");
		wmcCiTool = new WebMarkupContainer("wmcCiTool");
		wmcCtype = new WebMarkupContainer("wmcCtype");
		wmcPtype = new WebMarkupContainer("wmcPtype");
		wmcSwType = new WebMarkupContainer("wmcSwType");
		wmcSwLicense = new WebMarkupContainer("wmcSwLicense");
		wmcTopic = new WebMarkupContainer("wmcTopic");
		wmcSwDevMeth = new WebMarkupContainer("wmcSwDevMeth");

		add(wmcProgLan.add(newLabelWithWMC("progLan", new PropertyModel<Collection<ProgrammingLanguage>>(model,
				"programmingLanguages"), wmcProgLan)), wmcItTool.add(newLabelWithWMC("itTool",
						new PropertyModel<Collection<IssueTrackingTool>>(model, "issueTrackingTools"), wmcItTool)),
				wmcScmTool.add(newLabelWithWMC("scmTool", new PropertyModel<Collection<SourceCodeManagementTool>>(model,
						"sourceCodeManagementTools"), wmcScmTool)), wmcSaTool.add(newLabelWithWMC("saTool",
								new PropertyModel<Collection<StaticAnalysisTool>>(model, "staticAnalysisTools"), wmcSaTool)),
				wmcTmTool.add(newLabelWithWMC("tmTool", new PropertyModel<Collection<TestManagementTool>>(model,
						"testManagementTools"), wmcTmTool)), wmcCiTool.add(newLabelWithWMC("ciTool",
								new PropertyModel<Collection<ContinuousIntegrationTool>>(model, "continuousIntegrationTools"), wmcCiTool)),
				wmcCtype.add(newLabelWithWMC("customerType", new PropertyModel<Collection<CustomerType>>(model, "customerTypes"),
						wmcCtype)), wmcPtype.add(newLabelWithWMC("projectType", new PropertyModel<Collection<ProjectType>>(model,
								"projectTypes"), wmcPtype)), wmcSwType.add(newLabelWithWMC("softwareType",
										new PropertyModel<Collection<SoftwareType>>(model, "softwareTypes"), wmcSwType)),
				wmcSwLicense.add(newLabelWithWMC("softwareLicense", new PropertyModel<Collection<SoftwareLicense>>(model,
						"softwareLicenses"), wmcSwLicense)), wmcTopic.add(newLabelWithWMC("topic",
								new PropertyModel<Collection<Topic>>(model, "topics"), wmcTopic)), wmcSwDevMeth.add(newLabelWithWMC(
										"softwareDevelopmentMethodology", new PropertyModel<Collection<SoftwareDevelopmentMethodology>>(model,
												"softwareDevelopmentMethodologies"), wmcSwDevMeth)));

		// check for metadata tag.
		Label metaDataLabel;
		if(model.getObject().getProgrammingLanguages().size() == 0 && model.getObject().getIssueTrackingTools().size() == 0 &&
				model.getObject().getSourceCodeManagementTools().size() == 0 && model.getObject().getStaticAnalysisTools().size() == 0 &&
				model.getObject().getTestManagementTools().size() == 0 && model.getObject().getContinuousIntegrationTools().size() == 0 &&
				model.getObject().getCustomerTypes().size() == 0 && model.getObject().getProjectTypes().size() == 0 &&
				model.getObject().getSoftwareTypes().size() == 0 && model.getObject().getSoftwareLicenses().size() == 0 &&
				model.getObject().getTopics().size() == 0 && model.getObject().getSoftwareDevelopmentMethodologies().size() == 0){

			metaDataLabel = new Label("metadataId", "Metadata:") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible() {
					return false;
				}
			};
		} else {
			metaDataLabel = new Label("metadataId", "Metadata:");
		}
		add(metaDataLabel);                  

		// Historical data panel with the last 10 values
		// TODO: Panelize this!!
		RepeatingView listOfHistValues = new RepeatingView("listOfHistValues");
		List<HistoricValuesProject> historicValues = historicalDataService
				.getAllHistValuesForProject(project.getId(), 0, 10);
		add(new Label(
				"noOfRegisters",
				historicalDataService.countHistValuesForProject(project.getId())));
		add(new BookmarkablePageLink<HistoricProjectPage>("historical",
				HistoricProjectPage.class, BaseTreePage.forProject(project)));

		for (HistoricValuesProject value : historicValues) {
			listOfHistValues.add(new Label(listOfHistValues.newChildId(), value
					.getValue() + " / " + value.getDate().toString()));
		}

		add(listOfHistValues);

		// add quality trend chart
		add(new BaseTrendChartPanel<>("trend", model));

	}

	private SnapshotSaveConfirmationModal newSaveSnapshotModal() {
		// TODO: this problably could removed, see the example below
		final IModel<String> projectNameModel = Model.of("");

		saveSnapshotModal = new SnapshotSaveConfirmationModal(
				"newSaveSnapshootModal", projectNameModel) {
			private static final long serialVersionUID = -5489628601177981259L;

			@Override
			public void onConfirmed(AjaxRequestTarget target) {

				// If there is no historic values update before storing the
				// snapshot
				if (historicalDataService.countHistValuesForProject(project
						.getId()) <= 0) {
					UQasarUtil.updateTree(project);
				}

				project.addSnapshot(projectNameModel.getObject());

				PageParameters parameters = BasePage.appendSuccessMessage(
						ProjectViewPage.forProject(project),
						new StringResourceModel("snapshot.saved.message", this,
								Model.of(project)));
				setResponsePage(ProjectViewPage.class, parameters);
			}
		};
		return saveSnapshotModal;
	}

	private SnapshotRecoverConfirmationModal newRecoverSnapshotModal() {
		recoverSnapshotModal = new SnapshotRecoverConfirmationModal(
				"newRecoverSnapshootModal", project) {
			private static final long serialVersionUID = 6587408723933701759L;

			@Override
			public void onConfirmed(AjaxRequestTarget target) {
				setResponsePage(SnapshotPage.class,
						ProjectViewPage.forSnapshot(this.getSnapshot()));
			}
		};
		return recoverSnapshotModal;
	}

	private <T> Label newLabelWithWMC(String markUpId,
			PropertyModel<Collection<T>> model, final WebMarkupContainer wmc) {
		return new Label(markUpId, model) {
			@Override
			public void onConfigure() {
				super.onConfigure();
				setDefaultModel(replaceBracketsModel(this));
				if (this.getDefaultModelObjectAsString().isEmpty()) {
					wmc.setVisible(false);
				}
			}
		};
	}

	private IModel<?> replaceBracketsModel(Label label) {
		return Model.of(label.getDefaultModelObjectAsString()
				.replace("[", "").replace("]", ""));
	}

	private List<User> getUsersOfAllTeamsPerProject(Project p) {
		List<User> users = new ArrayList<>();

		if (p.getTeams() != null && p.getTeams().size() > 0) {
			for (Team t : p.getTeams()) {
				users.addAll(t.getAllUsers());
			}
		}

		return users;
	}

	private AjaxLink saveSnapButton() {
		return new AjaxLink("saveSnapButton") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				saveSnapshotModal.appendShowDialogJavaScript(target);
			}
		};
	}

	private AjaxLink recoverSnapButton() {
		return new AjaxLink("recoverSnapButton") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				recoverSnapshotModal.appendShowDialogJavaScript(target);
			}
		};
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

}
