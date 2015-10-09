package eu.uqasar.web.components;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class ExternalImage extends WebMarkupContainer {

	private static final long serialVersionUID = 595941870504144570L;

	public ExternalImage(String id, IModel<?> model) {
		super(id, model);
		add(AttributeAppender.replace("src", model));
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		checkComponentTag(tag, "img");
	}
}
