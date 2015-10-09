package eu.uqasar.web.pages.admin.meta;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.meta.MetaData;
import eu.uqasar.service.meta.MetaDataServiceBroker;
import eu.uqasar.web.components.util.AjaxBootstrapTabbedPanel;
import eu.uqasar.web.components.util.AjaxBootstrapTabbedPillsPanel;
import eu.uqasar.web.pages.admin.AdminBasePage;

/**
 *
 *
 */
public class MetaDataSettingsPage extends AdminBasePage {

    @Inject
    MetaDataServiceBroker serviceBroker;
    
    private final AjaxBootstrapTabbedPanel<ITab> tabPanel;

    public MetaDataSettingsPage(PageParameters pageParameters) {
        super(pageParameters);
        List<ITab> tabs = new ArrayList<>();
        for (final Class clazz : MetaData.getAllClasses()) {
            tabs.add(new AbstractTab(Model.of(MetaData.getLabel(clazz))) {
                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    return new MetaDataEditPanel<>(panelId, clazz, serviceBroker.getService(clazz), feedbackPanel);
                }
            });
        }
        tabPanel = new AjaxBootstrapTabbedPillsPanel<>("tabs", tabs).setAdjustPillsForMobile(true);
        add(tabPanel);
    }
}
