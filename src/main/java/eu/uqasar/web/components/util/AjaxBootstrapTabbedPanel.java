package eu.uqasar.web.components.util;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.util.Attributes;
import static eu.uqasar.web.components.util.AjaxBootstrapTabbedPanel.Direction.ABOVE;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

/**
 *
 *
 * @param <T>
 */
public class AjaxBootstrapTabbedPanel<T extends ITab> extends AjaxTabbedPanel<T> {

    private Direction direction = ABOVE;
    
    public AjaxBootstrapTabbedPanel(String id, List<T> tabs) {
        super(id, tabs);
        commonInit();
    }

    public AjaxBootstrapTabbedPanel(String id, List<T> tabs, IModel<Integer> model) {
        super(id, tabs, model);
        commonInit();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    /**
     * common initializer
     */
    private void commonInit() {
        BootstrapBaseBehavior.addTo(this);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        checkComponentTag(tag, "div");
        Attributes.addClass(tag, "tabbable");
        if(direction != null && direction != ABOVE) {
            Attributes.addClass(tag, "tabs-" + direction.name().toLowerCase());
        }
    }

    @Override
    protected String getSelectedTabCssClass() {
        return "active";
    }

    @Override
    protected String getLastTabCssClass() {
        return "";
    }
    
    public static enum Direction {
        
        LEFT,
        
        RIGHT,
        
        BELOW,
        
        ABOVE
    }
}
