package eu.uqasar.web;

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


import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import lombok.Getter;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.bean.validation.BeanValidationConfiguration;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.util.time.Duration;
import org.jboss.solder.logging.Logger;
import org.jboss.vfs.VirtualFile;
import org.reflections.ReflectionsException;
import org.reflections.vfs.SystemDir;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;
import org.wicketstuff.javaee.naming.global.ModuleJndiNamingStrategy;

import ro.fortsoft.wicket.dashboard.DashboardContextInitializer;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import eu.uqasar.auth.strategies.UQasarAuthorizationStrategy;
import eu.uqasar.web.components.resources.UserPictureResource;
import eu.uqasar.web.dashboard.DashboardEditPage;
import eu.uqasar.web.dashboard.DashboardViewPage;
import eu.uqasar.web.dashboard.DemoWidgetActionsFactory;
import eu.uqasar.web.dashboard.projectqualitygooglechart.ProjectQualityGoogleChartWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.datadeviation.DataDeviationWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.jenkins.JenkinsWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.projectqualitychart.ProjectQualityChartWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.reportingwidget.ReportingWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.sonarqualitywidget.SonarQualityWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.tech_debt.TechDebtChartWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.testlinkwidget.TestLinkWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.uqasardatavisualization.UqasarDataVisualizationWidgetDescriptor;
import eu.uqasar.web.dashboard.widget.widgetforjira.WidgetForJIRADescriptor;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.adapterdata.AdapterAddEditPage;
import eu.uqasar.web.pages.adapterdata.AdapterManagementPage;
import eu.uqasar.web.pages.admin.companies.CompanyEditPage;
import eu.uqasar.web.pages.admin.companies.CompanyListPage;
import eu.uqasar.web.pages.admin.meta.MetaDataSettingsPage;
import eu.uqasar.web.pages.admin.qmodel.QModelSettingsPage;
import eu.uqasar.web.pages.admin.rdf.RDFManagementPage;
import eu.uqasar.web.pages.admin.settings.LdapSettingsPage;
import eu.uqasar.web.pages.admin.settings.MailSettingsPage;
import eu.uqasar.web.pages.admin.settings.platform.PlatformSettingsPage;
import eu.uqasar.web.pages.admin.teams.TeamEditPage;
import eu.uqasar.web.pages.admin.teams.TeamImportPage;
import eu.uqasar.web.pages.admin.teams.TeamListPage;
import eu.uqasar.web.pages.admin.users.UserEditPage;
import eu.uqasar.web.pages.admin.users.UserImportPage;
import eu.uqasar.web.pages.admin.users.UserListPage;
import eu.uqasar.web.pages.analytic.AnalyticWorkbench;
import eu.uqasar.web.pages.analytic.drilldown.AnalysisDrilldown;
import eu.uqasar.web.pages.analytic.editor.AnalysisEditor;
import eu.uqasar.web.pages.auth.InfoPage;
import eu.uqasar.web.pages.auth.LogoutPage;
import eu.uqasar.web.pages.auth.login.LoginPage;
import eu.uqasar.web.pages.auth.register.CancelRegistrationPage;
import eu.uqasar.web.pages.auth.register.ConfirmRegistrationPage;
import eu.uqasar.web.pages.auth.register.RegisterPage;
import eu.uqasar.web.pages.auth.reset.RecoverPasswordPage;
import eu.uqasar.web.pages.auth.reset.ResetPasswordPage;
import eu.uqasar.web.pages.errors.ErrorPage;
import eu.uqasar.web.pages.processes.ProcessAddEditPage;
import eu.uqasar.web.pages.processes.ProcessManagementPage;
import eu.uqasar.web.pages.products.ProductAddEditPage;
import eu.uqasar.web.pages.products.ProductManagementPage;
import eu.uqasar.web.pages.qmodel.QModelImportPage;
import eu.uqasar.web.pages.qmtree.metric.QMMetricEditPage;
import eu.uqasar.web.pages.qmtree.metric.QMMetricViewPage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelEditPage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorEditPage;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorViewPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveEditPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveViewPage;
import eu.uqasar.web.pages.search.SearchResultsPage;
import eu.uqasar.web.pages.tree.historic.baseindicator.HistoricBaseIndicatorPage;
import eu.uqasar.web.pages.tree.historic.project.HistoricProjectPage;
import eu.uqasar.web.pages.tree.metric.MetricEditPage;
import eu.uqasar.web.pages.tree.metric.MetricViewPage;
import eu.uqasar.web.pages.tree.projects.ProjectEditPage;
import eu.uqasar.web.pages.tree.projects.ProjectExportPage;
import eu.uqasar.web.pages.tree.projects.ProjectImportPage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.projects.ProjectWizardPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorEditPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveEditPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveViewPage;
import eu.uqasar.web.pages.tree.snapshot.SnapshotPage;
import eu.uqasar.web.pages.tree.subset.SubsetProposalPage;
import eu.uqasar.web.pages.tree.visual.VisualPage;
import eu.uqasar.web.pages.user.ProfilePage;
import eu.uqasar.web.pages.user.UserPage;
import eu.uqasar.web.provider.UrlProvider;

@Getter
public class UQasar extends WebApplication {

	@Inject
	private UrlProvider urlProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return AboutPage.class;
	}

	public String getHomePageUrl() {
		return urlProvider.urlFor(getHomePage());
	}

	public static UQasar get() {
		return (UQasar) WebApplication.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.protocol.http.WebApplication#init()
	 */
	@Override
	public void init() {
		super.init();

		// wicketstuff-javaee-inject configuration for injecting EJBs into
		// Wicket pages
		configureJavaEEInject();

		// CDI configuration for injection CDI components into Wicket pages
		configureCDIInjecttion();

		// Bean Validation configuration
		configureBeanValidation();

		// add authorization strategy
		getSecuritySettings().setAuthorizationStrategy(new UQasarAuthorizationStrategy());

		// add bootstrap stuff
		configureBootstrap();

		// mount pages
		mountPages();

		// mount resources
		mountResources();

		// markup settings
		getMarkupSettings().setStripWicketTags(true);
		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

		// exception settings
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		if(usesDeploymentConfig()) {
			getApplicationSettings().setInternalErrorPage(ErrorPage.class);
			getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
		}
		getRequestCycleListeners().add(new UQasarExceptionRequestCycleListener(usesDeploymentConfig()));
		
		// gather client (browser) properties
		getRequestCycleSettings().setGatherExtendedBrowserInfo(true);
		getApplicationSettings().setUploadProgressUpdatesEnabled(true);
		
		// init dashboard from context
		initDashboard();
		
		// Redirect to the start page on page expiration
		getApplicationSettings().setPageExpiredErrorPage(AboutPage.class);
		
		getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
	}

	public DashboardContext getDashboardContext() {
		return getMetaData(DashboardContextInitializer.DASHBOARD_CONTEXT_KEY);
	}

	private void initDashboard() {

		// register some widgets
		DashboardContext dashboardContext = getDashboardContext();
		dashboardContext.getWidgetRegistry()
				.registerWidget(new ProjectQualityChartWidgetDescriptor())
				.registerWidget(new ProjectQualityGoogleChartWidgetDescriptor())
				.registerWidget(new UqasarDataVisualizationWidgetDescriptor())
				.registerWidget(new DataDeviationWidgetDescriptor())
				.registerWidget(new WidgetForJIRADescriptor())
				.registerWidget(new SonarQualityWidgetDescriptor())
				.registerWidget(new ReportingWidgetDescriptor())
				.registerWidget(new TestLinkWidgetDescriptor())
				.registerWidget(new JenkinsWidgetDescriptor())
				.registerWidget(new TechDebtChartWidgetDescriptor());

		// add a custom action for all widgets
		dashboardContext.setWidgetActionsFactory(new DemoWidgetActionsFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.protocol.http.WebApplication#newSession(org.apache.
	 * wicket.request.Request, org.apache.wicket.request.Response)
	 */
	@Override
	public Session newSession(Request request, Response response) {
		return new UQSession(request);
	}

	public static UQSession getSession() {
		return (UQSession) Session.get();
	}

	private void mountResources() {
		mountResource("/resources/users/picture/${userId}/${dim}", UserPictureResource.asReference());
	}

	/**
	 * Mounts certain pages to user-friendly, bookmarkable links.
	 */
	private void mountPages() {

		// User Dashboard(s) and their configuration
		mountPage("/dashboards/${id}", DashboardViewPage.class);
		mountPage("/dashboards", DashboardViewPage.class);
		mountPage("/dashboards/create", DashboardEditPage.class);
		mountPage("/dashboards/${id}/edit", DashboardEditPage.class);

		// Project related
		mountPage("/project/create", ProjectWizardPage.class);
		mountPage("/project/import", ProjectImportPage.class);
		mountPage("/project/export", ProjectExportPage.class);
		mountPage("/projects/${project-key}", ProjectViewPage.class);
		mountPage("/projects/${project-key}/edit", ProjectEditPage.class);
		mountPage("/subset",SubsetProposalPage.class);
		
		// Analysis related 
		mountPage("/analytics",AnalyticWorkbench.class);
		mountPage("/analytic/${analysis-id}", AnalysisDrilldown.class);
		mountPage("/analytic/${analysis-id}/edit", AnalysisEditor.class);
		
		// Quality objective related
		mountPage("/projects/${project-key}/objective/${id}",
				QualityObjectiveViewPage.class);
		mountPage("/projects/${project-key}/objective/${id}/edit",
				QualityObjectiveEditPage.class);

		// Quality indicator related
		mountPage("/projects/${project-key}/indicator/${id}",
				QualityIndicatorViewPage.class);
		mountPage("/projects/${project-key}/indicator/${id}/edit",
				QualityIndicatorEditPage.class);

		// Metric related
		mountPage("/projects/${project-key}/metric/${id}", MetricViewPage.class);
		mountPage("/projects/${project-key}/metric/${id}/edit",
				MetricEditPage.class);
		
		// Historical Data
		mountPage("/projects/${project-key}/historic/${id}",
				HistoricBaseIndicatorPage.class);
		mountPage("/projects/${project-key}/historic",
				HistoricProjectPage.class);
		
		// Visualization
		mountPage("/visual", VisualPage.class);
		mountPage("/snapshot/${snap-id}", SnapshotPage.class);

		// User related
		mountPage("/users/${userName}", UserPage.class);
		mountPage("/profile", ProfilePage.class);

		// Auth related
		mountPage("/auth/login", LoginPage.class);
		mountPage("/auth/logout", LogoutPage.class);
		mountPage("/auth/register", RegisterPage.class);
		mountPage("/auth/recover", RecoverPasswordPage.class);
		mountPage("/auth/reset", ResetPasswordPage.class);
		mountPage("/auth/info", InfoPage.class);
		mountPage("/auth/confirm/${token}", ConfirmRegistrationPage.class);
		mountPage("/auth/cancel/${token}", CancelRegistrationPage.class);

		// QModel related
		mountPage("/qmodel/import", QModelImportPage.class);
		mountPage("/qmodel/create", QModelEditPage.class);
		mountPage("/qmodels/${qmodel-key}", QModelViewPage.class);
		mountPage("/qmodels/${qmodel-key}/edit", QModelEditPage.class);

		// Quality objective related
		mountPage("/qmodels/${qmodel-key}/objective/${id}",
				QMQualityObjectiveViewPage.class);
		mountPage("/qmodels/${qmodel-key}/objective/${id}/edit",
				QMQualityObjectiveEditPage.class);

		// Quality indicator related
		mountPage("/qmodels/${qmodel-key}/indicator/${id}",
				QMQualityIndicatorViewPage.class);
		mountPage("/qmodels/${qmodel-key}/indicator/${id}/edit",
				QMQualityIndicatorEditPage.class);

		// Metric related
		mountPage("/qmodels/${qmodel-key}/metric/${id}", QMMetricViewPage.class);
		mountPage("/qmodels/${qmodel-key}/metric/${id}/edit",
				QMMetricEditPage.class);

		// Product related
		mountPage("/products", ProductManagementPage.class);
		mountPage("/products/${id}", ProductAddEditPage.class);

		// Process related
		mountPage("/processes", ProcessManagementPage.class);
		mountPage("/processes/${id}", ProcessAddEditPage.class);

        // Search related
        mountPage("/search/#{query}", SearchResultsPage.class);
        
		// Admin related
        mountPage("/admin/companies/list", CompanyListPage.class);
        mountPage("/admin/companies/edit/#{id}", CompanyEditPage.class);
		mountPage("/admin/users/list", UserListPage.class);
		mountPage("/admin/users/edit/#{id}", UserEditPage.class);
		mountPage("/admin/users/import", UserImportPage.class);
		mountPage("/admin/teams/list", TeamListPage.class);
		mountPage("/admin/teams/edit/#{id}", TeamEditPage.class);
		mountPage("/admin/teams/import", TeamImportPage.class);
		mountPage("/admin/settings/ldap", LdapSettingsPage.class);
		mountPage("/admin/settings/mail", MailSettingsPage.class);
		mountPage("/admin/settings/meta", MetaDataSettingsPage.class);
		mountPage("/admin/settings/qmodel", QModelSettingsPage.class);
		mountPage("/admin/settings/platform", PlatformSettingsPage.class);
		mountPage("/admin/rdf", RDFManagementPage.class);
		
		// Data adapter related
		mountPage("/adapters", AdapterManagementPage.class);
		mountPage("/adapters/${id}", AdapterAddEditPage.class);
	}

	/**
	 * Register vfs:// file type handler. These are required for dynamically
	 * loading the bootstrap javascript, icons and default css.
	 */
	private void configureVfsURLHandler() {
		Vfs.addDefaultURLTypes(new Vfs.UrlType() {
			@Override
			public boolean matches(URL url) {
				return url.getProtocol().equals("vfs");
			}

			@Override
			public Vfs.Dir createDir(URL url) {
				VirtualFile content;
				try {
					content = (VirtualFile) url.openConnection().getContent();
				} catch (IOException e) {
					throw new ReflectionsException(
							"could not open url connection as VirtualFile ["
							+ url + "]", e);
				}

				Vfs.Dir dir = null;
				try {
					dir = createDir(new java.io.File(content.getPhysicalFile()
							.getParentFile(), content.getName()));
				} catch (IOException e) { /* continue */

				}
				if (dir == null) {
					try {
						dir = createDir(content.getPhysicalFile());
					} catch (IOException e) { /* continue */

					}
				}
				return dir;
			}

			Vfs.Dir createDir(java.io.File file) {
				try {
					return file.exists() && file.canRead() ? file.isDirectory() ? new SystemDir(
							file) : new ZipDir(new JarFile(file))
							: null;
				} catch (IOException e) {
					Logger.getLogger(UQasar.class).error(e.getMessage(), e);
				}
				return null;
			}
		});
	}

	/**
	 * Configures wicket-bootstrap.
	 */
	private void configureBootstrap() {

		// configure default bootstrap libs loading wrt. vfs urls
		configureVfsURLHandler();

		// Remove Wicket markup as it may lead to strange UI problems because
		// CSS selectors may not match anymore.
		getMarkupSettings().setStripWicketTags(true);

		BootstrapSettings settings = new BootstrapSettings();
		Bootstrap.install(this, settings);
	}

	/**
	 *
	 */
	private void configureJavaEEInject() {
		getComponentInstantiationListeners().add(
				new JavaEEComponentInjector(this,
						new ModuleJndiNamingStrategy()));
	}

	/**
	 *
	 */
	private void configureCDIInjecttion() {
		BeanManager manager = null;
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			// Standard JNDI binding
			manager = (BeanManager) ic.lookup("java:comp/BeanManager");
		} catch (NameNotFoundException e) {
			if (ic == null) {
				throw new RuntimeException("No InitialContext");
			}

			// Weld/Tomcat
			try {
				manager = (BeanManager) ic.lookup("java:comp/env/BeanManager");
			} catch (NamingException e1) {
				// JBoss 5/6 (maybe obsolete in Weld 1.0+)
				try {
					manager = (BeanManager) ic.lookup("java:app/BeanManager");
				} catch (NamingException e2) {
					throw new RuntimeException("Could not find Bean Manager",
							e2);
				}
			}
		} catch (NamingException e) {
			throw new RuntimeException("Could not find Bean Manager", e);
		}

		new CdiConfiguration(manager).configure(this);
	}

	/**
	 *
	 */
	private void configureBeanValidation() {
		new BeanValidationConfiguration().configure(this);
	}

}
