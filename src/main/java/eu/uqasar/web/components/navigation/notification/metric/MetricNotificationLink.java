package eu.uqasar.web.components.navigation.notification.metric;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.metric.MetricNeedsToBeEdited;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.components.navigation.notification.NotificationBookmarkablePageLink;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.metric.MetricViewPage;

public class MetricNotificationLink extends
        NotificationBookmarkablePageLink<MetricNeedsToBeEdited, MetricViewPage> {

    private static final long serialVersionUID = 3310107463188121652L;
    
    public MetricNotificationLink(String id,
            IModel<MetricNeedsToBeEdited> model) {
        this(id, new PageParameters(), model);
      
    }

    public MetricNotificationLink(String id, PageParameters parameters,
            final IModel<MetricNeedsToBeEdited> model) {
       
        super(id, MetricViewPage.class, BaseTreePage.forMetric(model.getObject().getMetric()), model);
        
        add(new Label("metric.name", getMetric().getName()));
        
        // Find ways to get Due date here and print it.
        if (model.getObject().getDueDays() == 0) {
            add(new Label("metric.due", new StringResourceModel("metric.needs.to.initialize", null, (0))));
        } else {
            add(new Label("metric.due", new StringResourceModel("metric.about.to.due", null, (model.getObject().getDueDays()+7))));    
        }
        
        setIcon(new IconType("exclamation-sign"));
        get("notification.container").add(new CssClassNameAppender("project", getMetric().getQualityStatus().getCssClassName()));
        
		final WebMarkupContainer deleteContainer = new WebMarkupContainer("delete");
		final String deleteMessage = new StringResourceModel("delete.message", this, null).getString();
		deleteContainer.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 8973155682310698578L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {				
				UQasarUtil.getNotifications().remove(model.getObject());
				setResponsePage(AboutPage.class, BasePage.appendSuccessMessage(getPageParameters(), deleteMessage));
			}
		});
		deleteContainer.setOutputMarkupId(true);
		add(deleteContainer);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }
    
    public Metric getMetric() {
        return getModelObject() != null ? getModelObject().getMetric() : null;
    }

    @Override
    public void onClick() {
        // TODO Auto-generated method stub
        
    }
}

