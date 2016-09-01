/*
 * Copyright 2012 Decebal Suiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
 * the License. You may obtain a copy of the License in the LICENSE file, or at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.naming.InitialContext;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.DashboardUtils;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetDescriptor;
import ro.fortsoft.wicket.dashboard.WidgetFactory;
import ro.fortsoft.wicket.dashboard.WidgetLocation;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardEvent;
import ro.fortsoft.wicket.dashboard.web.DashboardPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.User;
import eu.uqasar.service.DashboardService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.dashboard.projectqualitygooglechart.ProjectQualityGoogleChartWidget;
import eu.uqasar.web.dashboard.widget.datadeviation.DataDeviationWidget;
import eu.uqasar.web.dashboard.widget.jenkins.JenkinsWidget;
import eu.uqasar.web.dashboard.widget.projectqualitychart.ProjectQualityChartWidget;
import eu.uqasar.web.dashboard.widget.reportingwidget.ReportingWidget;
import eu.uqasar.web.dashboard.widget.sonarqualitywidget.SonarQualityWidget;
import eu.uqasar.web.dashboard.widget.tech_debt.TechDebtChartWidget;
import eu.uqasar.web.dashboard.widget.testlinkwidget.TestLinkWidget;
import eu.uqasar.web.dashboard.widget.uqasardatavisualization.UqasarDataVisualizationWidget;
import eu.uqasar.web.dashboard.widget.widgetforjira.WidgetForJira;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;


/**
 * @author Decebal Suiu
 */
public class DashboardViewPage extends BasePage {

	private static final long serialVersionUID = 1L;
	private WebMarkupContainer newLinksContainer;
	private final transient DashboardContext dashboardContext =
			UQasar.get().getDashboardContext();
	private Dashboard dashboard;
	private final Modal resetConfirmationModal;
	private final Modal deleteConfirmationModal;
	private final Logger logger = Logger.getLogger(DashboardViewPage.class);

	@Inject
	private DashboardService dashboardService;
	private DashboardPanel dashboardPanel;

	public DashboardViewPage(PageParameters parameters) {
		super(parameters);

		// If no dashboard by the id can be loaded, throw an exception
		if (!parameters.get("id").isEmpty()) {

			dashboard = dashboardService.getById(parameters.get("id").toLong());
			if (dashboard == null) {
				throw new EntityNotFoundException(Dashboard.class, 
						parameters.get("id").toOptionalString());
			}
		} 

		if (!parameters.get("useSuggestion").isEmpty() && parameters.get("useSuggestion").toBoolean()) {

			logger.info("Getting a suggestion for recommended dashboard setup.");
			DbDashboard dbDash = (DbDashboard) dashboard;
			dashboard = getSuggestedDashboardSetup(dbDash);

			String message = new StringResourceModel("suggestion.feedback.info", this, null).getString(); 
			getPage().info(message);			
		}

		dashboardPanel = new DashboardPanel("dashboard", new Model<>(dashboard));
		dashboardPanel.setOutputMarkupId(true);
		add(dashboardPanel);
		// Container for links of different types of sample widgets
		newLinksContainer = 
				new WebMarkupContainer("container.dashboard.node.new");
		newLinksContainer.setOutputMarkupId(true);
		BootstrapAjaxLink<String> addProjectQualityChartWidgetLink = 
				getNewProjectQualityChartWidgetLink();
		BootstrapAjaxLink<String> addProjectQualityGoogleChartWidgetLink = 
				getNewProjectQualityGoogleChartWidgetLink();
		BootstrapAjaxLink<String> addWidgetForJIRALink = 
				getNewWidgetForJIRALink();
		BootstrapAjaxLink<String> addSonarQualityWidgetLink = 
				getNewSonarQualityWidgetLink();
		BootstrapAjaxLink<String> addTestLinkWidgetLink = 
				getTestLinkWidgetLink();
		BootstrapAjaxLink<String> addJenkinsLinkWidgetLink = 
				getJenkinsLinkWidgetLink();
		BootstrapAjaxLink<String> addUqasarDataVisualizationWidgetLink = 
				getUqasarDataVisualizationWidgetLink();
		BootstrapAjaxLink<String> addDataDeviationWidgetLink = 
				getDataDeviationWidgetLink();
		BootstrapAjaxLink<String> addTechDebtChartWidgetLink = 
				getTechDebtChartWidgetLink();
		BootstrapAjaxLink<String> addReportingWidgetLink =  
				getNewReportingWidgetLink();
		newLinksContainer.add(addProjectQualityChartWidgetLink);
		newLinksContainer.add(addProjectQualityGoogleChartWidgetLink);
		newLinksContainer.add(addWidgetForJIRALink);
		newLinksContainer.add(addSonarQualityWidgetLink);
		newLinksContainer.add(addTestLinkWidgetLink);
		newLinksContainer.add(addJenkinsLinkWidgetLink);
		newLinksContainer.add(addUqasarDataVisualizationWidgetLink);
		newLinksContainer.add(addDataDeviationWidgetLink);
		newLinksContainer.add(addTechDebtChartWidgetLink);
		newLinksContainer.add(addReportingWidgetLink);
		add(newLinksContainer);

		BootstrapAjaxLink<String> newUpdateDashboardLink 
		= getUpdateDashboardLink();
		newUpdateDashboardLink.setOutputMarkupId(true);
		add(newUpdateDashboardLink);

		BootstrapAjaxLink<String> shareDashboardLink 
		= getShareDashboardLink();
		shareDashboardLink.setOutputMarkupId(true);
		add(shareDashboardLink);

		BootstrapAjaxLink<String> exportDashboardLink 
		= getExportDashboardLink();
		exportDashboardLink.setOutputMarkupId(true);
		add(exportDashboardLink);

		BootstrapAjaxLink<String> resetDashboardLink 
		= getResetDashboardLink();
		resetDashboardLink.setOutputMarkupId(true);
		add(resetDashboardLink);

		BootstrapAjaxLink<String> deleteDashboardLink = 
				getDeleteDashboardLink();
		deleteDashboardLink.setOutputMarkupId(true);
		add(deleteDashboardLink);

		add(resetConfirmationModal = newResetConfirmationModal());
		add(deleteConfirmationModal = newDeleteConfirmationModal());
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptReferenceHeaderItem.forUrl("assets/js/jspdf.min.js"));
		response.render(JavaScriptReferenceHeaderItem.forUrl("assets/js/rgbcolor.js"));
		response.render(JavaScriptReferenceHeaderItem.forUrl("assets/js/canvg.js"));
		response.render(JavaScriptHeaderItem.forScript("(function (H) {"
				+ "H.Chart.prototype.createCanvas = function (divId) {"
				+ "var svg = this.getSVG(),"
				+ "width = parseInt(svg.match(/width='([0-9]+)'/)),"
				+ "height = parseInt(svg.match(/height='([0-9]+)'/)),"
				+ "canvas = document.createElement('canvas');"
				+ "canvas.setAttribute('width', width);"
				+ "canvas.setAttribute('height', height);"
				+ "if (canvas.getContext && canvas.getContext('2d')) {"
				+ "canvg(canvas, svg);"
				+ "return canvas.toDataURL('image/jpeg');"
				+ "}"
				+ "else {"
				+ "alert('Your browser doesnt support this feature, please use a modern browser');"
				+ "return false;"
				+ "}"
				+ "}"
				+ "}(Highcharts));", ""));
		
		// overwrite default dashboard CSS classes
		response.render(CssReferenceHeaderItem.forCSS(""
				+ ".dragbox-header{ margin: 0;padding: 5px;background: #445; color: #fff; border-bottom: 1px solid #eee; height: 1.2em;cursor: move;}"
				+ ".dragbox{ margin: 5px 2px 20px; background: #fff; position: relative; border: 1px solid #ddd; -moz-border-radius: 0px;-webkit-border-radius: 0px;}", 
				"overwriteDefaultDashboadDragboxHeader"));
	}

	/**
	 * 
	 * @return
	 */
	private NotificationModal newResetConfirmationModal() {
		final NotificationModal notificationModal = new NotificationModal(
				"resetConfirmationModal", new StringResourceModel(
						"reset.confirmation.modal.header", this, null),
				new StringResourceModel("reset.confirmation.modal.message",
						this, null), false);
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Primary, new StringResourceModel(
						"reset.confirmation.modal.submit.text", this, null),
				true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				List<Widget> widgets = dashboard.getWidgets();
				for (Iterator<Widget> iter = widgets.iterator(); 
						iter.hasNext(); ) {
					Widget obj = iter.next();	
					iter.remove();
				}
				dashboardContext.getDashboardPersiter().save(dashboard);
				target.add(dashboardPanel);

				// close modal
				closeConfirmationModal(notificationModal, target);
			}
		});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"reset.confirmation.modal.cancel.text", this, null),
				true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// Cancel clicked --> do nothing, close modal
				closeConfirmationModal(notificationModal, target);
			}
		});
		return notificationModal;
	}

	/**
	 * 
	 * @return
	 */
	private NotificationModal newDeleteConfirmationModal() {
		final NotificationModal notificationModal = new NotificationModal(
				"deleteConfirmationModal", new StringResourceModel(
						"delete.confirmation.modal.header", this, null),
				new StringResourceModel("delete.confirmation.modal.message",
						this, null), false);
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Primary, new StringResourceModel(
						"delete.confirmation.modal.submit.text", this, null),
				true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// TODO
				if (dashboard != null) {
					List<DbDashboard> dashboards = dashboardService.getDashboardByTitle(dashboard.getTitle());
					if (dashboards != null && dashboards.size() > 0) {

						DbDashboard dbdb = dashboards.get(0); 

						try {
							InitialContext ic = new InitialContext();
							UserService userService = (UserService) ic.lookup("java:module/UserService");
							User user = userService.getById(UQasar.getSession().getLoggedInUser().getId());
							user.getDashboards().remove(dbdb);
							userService.update(user);
							dashboardService.delete(dbdb);												
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				setResponsePage(AboutPage.class);
				// close modal
				closeConfirmationModal(notificationModal, target);
			}
		});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"delete.confirmation.modal.cancel.text", this, null),
				true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// Cancel clicked --> do nothing, close modal
				closeConfirmationModal(notificationModal, target);
			}
		});
		return notificationModal;
	}


	/**
	 * 
	 * @param modal
	 * @param target
	 */
	private void closeConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		modal.appendCloseDialogJavaScript(target);
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getNewProjectQualityChartWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.projectquality", 
				new StringResourceModel(
						"button.dashboard.new.projectqualitywidget", 
						this, null), Buttons.Type.Link) {
			private static final long serialVersionUID = -3891261827414844194L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								ProjectQualityChartWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);


				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}


	
	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getNewProjectQualityGoogleChartWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.projectqualitygooglechart", 
				new StringResourceModel(
						"button.dashboard.new.projectqualitygooglechartwidget", 
						this, null), Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7142515721241176452L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								ProjectQualityGoogleChartWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);

				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}

	
	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getNewWidgetForJIRALink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.widgetforjira", new StringResourceModel(
						"button.dashboard.new.widgetforjira", 
						this, null), Buttons.Type.Link) {

			private static final long serialVersionUID = -6558356540387024146L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								WidgetForJira.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);

				// Update the panel
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getNewSonarQualityWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.sonarquality", new StringResourceModel(
						"button.dashboard.new.sonarqualitywidget", this, null),
				Buttons.Type.Link) {

			private static final long serialVersionUID = 1106818355620419798L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								SonarQualityWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);

				// Update the dashboard
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getTestLinkWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.testlink", new StringResourceModel(
						"button.dashboard.new.testlinkwidget", this, null),
				Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3058511727860549437L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								TestLinkWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);

				// Update the dashboard
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}
	
	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getJenkinsLinkWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.jenkins", new StringResourceModel(
						"button.dashboard.new.jenkins", this, null),
						Buttons.Type.Link) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -3058511727860549437L;
			
			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								JenkinsWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);
				
				// Update the dashboard
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getUqasarDataVisualizationWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.uqasardata", new StringResourceModel(
						"button.dashboard.new.uqasardata", this, null),
				Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3058511727860549437L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								UqasarDataVisualizationWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);
				// Update the dashboard
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}

	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getDataDeviationWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.datadeviation", new StringResourceModel(
						"button.dashboard.new.datadeviation", this, null),
				Buttons.Type.Link) {

			private static final long serialVersionUID = -3058511727860549437L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(DataDeviationWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);

				Widget widget = widgetFactory.createWidget(item.getObject());

				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getTechDebtChartWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.techdebt", new StringResourceModel(
						"button.dashboard.new.techdebt", this, null),
				Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7733112957905895418L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(TechDebtChartWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);

				Widget widget = widgetFactory.createWidget(item.getObject());

				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getNewReportingWidgetLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.node.new.reporting", new StringResourceModel(
						"button.dashboard.new.reporting", this, null),
				Buttons.Type.Link) {

			private static final long serialVersionUID = 1106818355620419798L;

			@Override 
			public void onClick(final AjaxRequestTarget target) {
				WidgetDescriptor descriptor = 
						dashboardContext.getWidgetRegistry().
						getWidgetDescriptorByClassName(
								ReportingWidget.class.getName());
				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(descriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				send(getPage(), Broadcast.BREADTH, new DashboardEvent(target, 
						DashboardEvent.EventType.WIDGET_ADDED, widget));
				DashboardUtils.updateWidgetLocations(dashboard, 
						new DashboardEvent(target, 
								DashboardEvent.EventType.WIDGET_ADDED, widget));
				dashboard.addWidget(widget);
				dashboardContext.getDashboardPersiter().save(dashboard);

				// Update the dashboard
				target.add(dashboardPanel); 
			}
		};
		link.setOutputMarkupId(true);
		return link;
	}
	
	
	/**
	 * Enables sharing the dashboard (create a new copy) with the selected 
	 * users.
	 * @return
	 */
	private BootstrapAjaxLink<String> getExportDashboardLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.export", 
				new StringResourceModel("button.dashboard.export", this, null), Buttons.Type.Warning) {


			/**
			 * 
			 */
			private static final long serialVersionUID = 2945536479877680702L;

			@Override
			public void onClick(final AjaxRequestTarget target) {	
				target.appendJavaScript(
						"var doc = new jsPDF();"
								+ "var chartHeight = 70;"
								+ "doc.setFontSize(35);"
								+ "doc.text(35, 25, 'U-QASAR Dashboard Export');"
								+ "$('.exportClass').each(function (index) {"
								+ " var imageData = $(this).highcharts().createCanvas();"
								+ "var y = 40;"
								+ "if (index >0 && index % 2){"
								+ "y=110;"
								+ "} else{"
								+ "if(index>0){"
								+ "doc.addPage();"
								+ "}"
								+ "}"
								+ "console.log(y);"

							+ "doc.addImage(imageData, 'JPEG', 45, y, 120, chartHeight);"

						+ "});"
						+ "doc.save('DashboardReport.pdf');"
						);
			}
		};

		link.setOutputMarkupId(true);
		link.setIconType(IconType.eject);
		return link;
	}
	/**
	 * Enables sharing the dashboard (create a new copy) with the selected 
	 * users.
	 * @return
	 */
	private BootstrapAjaxLink<String> getShareDashboardLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.share", 
				new StringResourceModel("button.dashboard.share", 
						this, null), Buttons.Type.Info) {


			/**
			 * 
			 */
			private static final long serialVersionUID = 2945536479877680702L;

			@Override
			public void onClick(final AjaxRequestTarget target) {	

				PageParameters params = new PageParameters();
				DbDashboard dbdb = (DbDashboard) dashboard;
				params.add("id", dbdb.getId());
				setResponsePage(DashboardSharePage.class, params);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
			}
		};
		link.setOutputMarkupId(true);
		link.setIconType(IconType.share);
		return link;
	}


	/**
	 * Resets the dashboard (removes all the widgets)
	 * @return
	 */
	private BootstrapAjaxLink<String> getResetDashboardLink() {

		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.reset", 
				new StringResourceModel("button.dashboard.reset", 
						this, null), Buttons.Type.Danger) {

			private static final long serialVersionUID = -3058511727860549437L;

			@Override
			public void onClick(final AjaxRequestTarget target) {			
				// confirmation
				if(!dashboard.getWidgets().isEmpty()){
					resetConfirmationModal.appendShowDialogJavaScript(target);
				}

			}
		};
		link.setOutputMarkupId(true);
		link.setIconType(IconType.removesign);
		return link;
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getUpdateDashboardLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.update", 
				new StringResourceModel("button.dashboard.update", 
						this, null), Buttons.Type.Primary) {

			private static final long serialVersionUID = -2851834962189387988L;

			@Override
			public void onClick(final AjaxRequestTarget target) {	
				target.add(dashboardPanel);
			}
		};
		link.setOutputMarkupId(true);
		link.setIconType(IconType.refresh);
		return link;
	}

	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> getDeleteDashboardLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.dashboard.delete", 
				new StringResourceModel("button.dashboard.delete", 
						this, null), Buttons.Type.Danger) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1106818355620419798L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				deleteConfirmationModal.appendShowDialogJavaScript(target);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
			}
		};
		link.setOutputMarkupId(true);
		link.setIconType(IconType.remove);
		return link;
	}


	/**
	 * Get a suggested dashboard configuration 
	 * @return
	 */
	private DbDashboard getSuggestedDashboardSetup(DbDashboard dbDash) {

		// Get a list of recommended widgets 
		List<WidgetDescriptor> recommendedWidgets = getRecommendedWidgets();
		// Get all the widgets 
		List<WidgetDescriptor> descr = dashboardContext.getWidgetRegistry().getWidgetDescriptors();
		int specifiedColsNumber = dbDash.getColumnCount();
		int col = 0;
		int row = 0;
		logger.info("Specified columns' count: " +specifiedColsNumber);
		for (WidgetDescriptor widgetDescriptor : descr) {

			// Add the recommended widgets to the dashboard 
			if (recommendedWidgets.contains(widgetDescriptor)) {
				logger.info("Adding the " +widgetDescriptor.getWidgetClassName() + " to the dashboard. Location [row: " +row +", col: " +col +"]");

				WidgetFactory widgetFactory = 
						dashboardContext.getWidgetFactory();
				Model<WidgetDescriptor> item =
                        new Model<>(widgetDescriptor);
				Widget widget = widgetFactory.createWidget(item.getObject());
				WidgetLocation location = new WidgetLocation(col, row);
				widget.setLocation(location);
				dbDash.addWidget(widget);			
				// Update the widget location for the next round
				if (col < specifiedColsNumber - 1) {
					col++;
				} else {
					col = 0;
					row++;
				}
			}			
		}

		return dbDash;
	}


	/**
	 * Get a list of suggested widgets based on which the desired dashboard can be created.
	 * At this stage this is purely related to the user role, but fine-tuning of this is TBD. 
	 * @return
	 */
	private List<WidgetDescriptor> getRecommendedWidgets() {

		List<WidgetDescriptor> widgets = new ArrayList<>();

		// Get the user
		User user = UQasar.getSession().getLoggedInUser();
		Role userRole = user.getRole();

		logger.info("User role: " + userRole + ", obtaining a recommended dashboard configuration...");

		// The defined basic set of widgets for different groups of users
		// ---------------------------------------------------------------
		// Developer/User:
		// Project quality widget
		// WidgetForJira
		// TechDebtChartWidget
		// SonarWidget
		// JenkinsWidget
		if (userRole.equals(Role.Developer) || userRole.equals(Role.User) ) {
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.projectqualitychart.ProjectQualityChartWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.widgetforjira.WidgetForJIRA"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.tech_debt.TechDebtChartWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.sonarqualitywidget.SonarQualityWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.jenkins.JenkinsWidget"));		
		}

		// Tester:
		// Project quality widget
		// TestlinkWidget
		// WidgetForJira
		// SonarWidget
		// JenkinsWidget
		else if (userRole.equals(Role.Tester)) {
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.projectqualitychart.ProjectQualityChartWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.testlinkwidget.TestLinkWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.widgetforjira.WidgetForJIRA"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.tech_debt.TechDebtChartWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.sonarqualitywidget.SonarQualityWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.jenkins.JenkinsWidget"));		
		}

		// ProductManager/ProcessManager/ScrumMaster:
		// Project quality widget
		// UqasarDataVisualizationWidget
		// DataDeviationWidget
		// JenkinsWidget
		else if (userRole.equals(Role.ProductManager) || userRole.equals(Role.ProcessManager) || userRole.equals(Role.ScrumMaster)) {
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.projectqualitychart.ProjectQualityChartWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.uqasardatavisualization.UqasarDataVisualizationWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.datadeviation.DataDeviationWidget"));
			widgets.add(dashboardContext.getWidgetRegistry().getWidgetDescriptorByClassName("eu.uqasar.web.dashboard.widget.jenkins.JenkinsWidget"));		
		}		

		// Administrator
		// All widgets :) 
		else if (userRole.equals(Role.Administrator)) {
			widgets.addAll(dashboardContext.getWidgetRegistry().getWidgetDescriptors());
		}
		// otherwise there are no recommendations at the moment

		return widgets;
	}
}
