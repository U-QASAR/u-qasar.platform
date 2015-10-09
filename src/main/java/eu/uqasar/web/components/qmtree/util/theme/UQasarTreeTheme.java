package eu.uqasar.web.components.qmtree.util.theme;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class UQasarTreeTheme extends Behavior {
	private static final long serialVersionUID = 1L;

	private static final ResourceReference CSS = new CssResourceReference(
			UQasarTreeTheme.class, "theme.css");

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		tag.append("class", "tree-theme-uqasar", " ");
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(CSS));
	}
}