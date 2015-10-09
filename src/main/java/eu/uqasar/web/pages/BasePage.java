/**
 *
 */
package eu.uqasar.web.pages;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapResourcesBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.ChromeFrameMetaTag;
import de.agilecoders.wicket.themes.settings.BootswatchThemeProvider;
import eu.uqasar.model.monitoring.UserAction;
import eu.uqasar.model.monitoring.UserActionLog;
import eu.uqasar.service.monitoring.UserActionLoggingService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.MainBehavior;
import eu.uqasar.web.components.PageTitleLabel;
import eu.uqasar.web.components.StyledFeedbackPanel;
import eu.uqasar.web.components.navigation.HeaderNavigationBar;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Session;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.jboss.solder.logging.Logger;
import org.joda.time.LocalDate;

import com.jcabi.manifests.Manifests;

/**
 *
 *
 */
@RequireHttps
public abstract class BasePage extends WebPage {

	/**
	 *
	 */
	private static final long serialVersionUID = 6323920767950541998L;

	public static final String MESSAGE_PARAM = "message";

	public static final String LEVEL_PARAM = "level";

	protected static final String FEEDBACKPANEL_ID = "feedbackPanel";

    private HeaderNavigationBar navbar;
    
	// inject the conversation
	@Inject
	protected Conversation conversation;
	
	// inject a logger
	@Inject
	Logger logger;
	
	@Inject
	private UserActionLoggingService loggingService;

	protected final StyledFeedbackPanel feedbackPanel;
	
	public static String SERVERNAMEANDPORT;

	/**
	 *
	 * @param parameters
	 */
	public BasePage(final PageParameters parameters) {
		
		// add global feedback panel
		feedbackPanel = new StyledFeedbackPanel(FEEDBACKPANEL_ID, 
                new ComponentFeedbackMessageFilter(this));
		add(feedbackPanel.setOutputMarkupId(true));

		// display any message that may exist in the parameters
		displayMessage(parameters);
		// for IE compatibility ???
		add(new ChromeFrameMetaTag("chrome-frame"));

		// always add the bootstrap resources (css, js, icons)
		add(new BootstrapResourcesBehavior());

		// add js and css for tags input
		add(new MainBehavior());

		Bootstrap.getSettings().setThemeProvider(new BootswatchThemeProvider());
		Bootstrap.getSettings().getActiveThemeProvider()
				.setActiveTheme("cosmo");

		// begin conversation
		if (conversation.isTransient()) {
			conversation.begin();
		}

		// add the menu at the top
		add(navbar = new HeaderNavigationBar("navbar", this));
		setFooterYear();
		add(newBuildNumberLabel());
		add(newBranchLabel());
		
		SERVERNAMEANDPORT = getServerNameAndPort();
		System.out.println("Current Server: "+SERVERNAMEANDPORT);
	}
	
	private String getServerNameAndPort(){
		HttpServletRequest req = (HttpServletRequest) getRequestCycle().getRequest().getContainerRequest();
		String serverNameAndPort = req.getServerName() + ":" + req.getServerPort();
		return serverNameAndPort;
	}
	
	/**
	 * 
	 * @return the SVN version number 
	 */
	private Label newBuildNumberLabel() {
		String buildNumber = "";
		try {
			Manifests.append(WebApplication.get().getServletContext());
			buildNumber = Manifests.read("SCM-Revision");

			logger.info("build number is: " + buildNumber);
		} catch (Exception ex) {
			// ignore if we can't get any version number
			if (logger.isDebugEnabled()) {
				logger.error("Could not read SCM-Revision from Manifest!", ex);
			}
		}
		return new Label("buildNumber", buildNumber);
	}

	/**
	 * @return the SVN branch 
	 */
	private Label newBranchLabel() {
		String scmBranch = "";
		try {
			Manifests.append(WebApplication.get().getServletContext());
			scmBranch = Manifests.read("SCM-Branch");
			
			logger.info("branch: " + scmBranch);
		} catch (Exception ex) {
			// ignore if we can't get any version number
			if (logger.isDebugEnabled()) {
				logger.error("Could not read SCM-Branch from Manifest!", ex);
			}
		}
		return new Label("scmBranch", scmBranch);
	}
	
    protected void setSearchTerm(final String query) {
        navbar.setSearchTerm(query);
    }
    
	public StyledFeedbackPanel getFeedbackPanel() {
		return feedbackPanel;
	}
	
	protected void logUserAction(UserActionLog logEntry) {
		loggingService.create(logEntry);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(OnDomReadyHeaderItem
				.forScript("if ('ontouchstart' in document.documentElement) { $('.dropdown-submenu > a').click(function(e){e.stopPropagation(); var menu = $(this).parent().children('.dropdown-menu').first(); return false;}) }"));
		
		if(UQasar.getSession().getLoggedInUser() != null){
			response.render(JavaScriptHeaderItem.
					forUrl("assets/js/uqasarTour.js"));
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		// If the title is not set in any sub-page this will be the default
		setPageTitle(getPageTitleModel());
		logUserAction(UserAction.View.generate(this));
	}

	private void setFooterYear() {
		LocalDate date = LocalDate.now();
		Label footer = new Label("footer", new StringResourceModel("footer.text", this, null, date.getYear()));
		footer.setEscapeModelStrings(false);
		add(footer);
	}

	/**
	 *
	 * @param titleModel
	 */
	protected final void setPageTitle(final IModel<String> titleModel) {
		addOrReplace(new PageTitleLabel("pageTitle", titleModel));
	}

	/**
	 *
	 * @return
	 */
	protected IModel<String> getPageTitleModel() {
		return new StringResourceModel("page.title", this, null);
	}
	
	public String getPageTitle() {
		return getPageTitleModel().getObject();
	}

	public static PageParameters getSuccessMessage(final Serializable message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.SUCCESS, message);
	}

	public static PageParameters getInfoMessage(final Serializable message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.INFO, message);
	}

	public static PageParameters getWarnMessage(final Serializable message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.WARNING, message);
	}

	public static PageParameters getErrorMessage(final Serializable message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.ERROR, message);
	}

	public static PageParameters appendSuccessMessage(PageParameters params, final Serializable message) {
		return appendFeedbackMessage(params, FeedbackMessage.SUCCESS, message);
	}

	public static PageParameters appendInfoMessage(PageParameters params, final Serializable message) {
		return appendFeedbackMessage(params, FeedbackMessage.INFO, message);
	}

	public static PageParameters appendWarnMessage(PageParameters params, final Serializable message) {
		return appendFeedbackMessage(params, FeedbackMessage.WARNING, message);
	}

	public static PageParameters appendErrorMessage(PageParameters params, final Serializable message) {
		return appendFeedbackMessage(params, FeedbackMessage.ERROR, message);
	}

	public static PageParameters getFeedbackMessage(final int level, Serializable message) {
		return appendFeedbackMessage(new PageParameters(), level, message);
	}

	public static PageParameters getSuccessMessage(final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.SUCCESS, message.getObject());
	}

	public static PageParameters getInfoMessage(final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.INFO, message.getObject());
	}

	public static PageParameters getWarnMessage(final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.WARNING, message.getObject());
	}

	public static PageParameters getErrorMessage(final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(new PageParameters(), FeedbackMessage.ERROR, message.getObject());
	}

	public static PageParameters appendSuccessMessage(PageParameters params, final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(params, FeedbackMessage.SUCCESS, message.getObject());
	}

	public static PageParameters appendInfoMessage(PageParameters params, final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(params, FeedbackMessage.INFO, message.getObject());
	}

	public static PageParameters appendWarnMessage(PageParameters params, final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(params, FeedbackMessage.WARNING, message.getObject());
	}

	public static PageParameters appendErrorMessage(PageParameters params, final IModel<? extends Serializable> message) {
		return appendFeedbackMessage(params, FeedbackMessage.ERROR, message.getObject());
	}

	public static PageParameters getFeedbackMessage(final int level, IModel<? extends Serializable> message) {
		return appendFeedbackMessage(new PageParameters(), level, message.getObject());
	}

	public static PageParameters appendFeedbackMessage(PageParameters params, final int level, Serializable message) {
		params.add(LEVEL_PARAM, level);
		params.add(MESSAGE_PARAM, message);
		return params;
	}

	/**
	 *
	 * @param parameters
	 */
	private void displayMessage(final PageParameters parameters) {
		String message = parameters.get(MESSAGE_PARAM).toString(null);
		int messageLevel = parameters.get(LEVEL_PARAM).toInt(-1);
		if (message != null && messageLevel != -1) {
			switch (messageLevel) {
				case FeedbackMessage.SUCCESS:
					success(message);
					break;
				case FeedbackMessage.INFO:
					info(message);
					break;
				case FeedbackMessage.WARNING:
					warn(message);
					break;
				case FeedbackMessage.ERROR:
					error(message);
					break;
			}
		}
	}

	private void configureLanguage(PageParameters pageParameters) {
		StringValue language = pageParameters.get("lang");
		if (!language.isEmpty()) {
			final String currentLanguage = Session.get().getLocale().getLanguage();
			final String newLanguage = language.toString();
			if (!currentLanguage.equalsIgnoreCase(newLanguage)) {
				final Locale newLocale = new Locale(language.toString());
				ResourceBundle.clearCache();
				UQSession.get().updateUserLocale(newLocale);
			}
		}
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		configureLanguage(getPageParameters());
	}

}
