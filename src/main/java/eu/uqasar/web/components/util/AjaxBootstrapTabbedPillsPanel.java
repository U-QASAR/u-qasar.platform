
package eu.uqasar.web.components.util;

import java.util.List;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 *
 *
 * @param <T>
 */
public class AjaxBootstrapTabbedPillsPanel<T extends ITab> extends AjaxBootstrapTabbedPanel<T> {
    
    private boolean adjustPillsForMobile = false;

    public AjaxBootstrapTabbedPillsPanel(String id, List<T> tabs) {
        super(id, tabs);
    }

    public AjaxBootstrapTabbedPillsPanel(String id, List<T> tabs, IModel<Integer> model) {
        super(id, tabs, model);
    }

    public boolean getAdjustPillsForMobile() {
        return adjustPillsForMobile;
    }

    public AjaxBootstrapTabbedPillsPanel<T> setAdjustPillsForMobile(boolean adjustPillsForMobile) {
        this.adjustPillsForMobile = adjustPillsForMobile;
        return this;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer script = new WebMarkupContainer("script");
        script.setVisible(adjustPillsForMobile);
        add(script);
    }
}
