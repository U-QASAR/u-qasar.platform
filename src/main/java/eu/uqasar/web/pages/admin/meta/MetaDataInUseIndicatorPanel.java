
package eu.uqasar.web.pages.admin.meta;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 *
 */
public class MetaDataInUseIndicatorPanel extends Panel {
    
    public MetaDataInUseIndicatorPanel(final String id, boolean inUse, final IModel<String> objectName) {
        super(id);
        final String determine = inUse ? "yes" : "no";
        add(new Icon("icon", new IconType(inUse ? "check-sign" : "remove-sign")));
        Label label = new Label("text", new StringResourceModel("label."+ determine, this, null));
        label.add(new CssClassNameAppender(inUse ? "error" : "success"));
        add(label);
    }
}
