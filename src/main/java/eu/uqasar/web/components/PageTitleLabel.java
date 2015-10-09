package eu.uqasar.web.components;

import java.io.Serializable;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class PageTitleLabel extends Label {

	private static final long serialVersionUID = 1264343221323288742L;

	public PageTitleLabel(String id) {
		super(id);
	}

	public PageTitleLabel(final String id, String label) {
		this(id, new Model<String>(label));
	}

	public PageTitleLabel(final String id, Serializable label) {
		this(id, Model.of(label));
	}

	public PageTitleLabel(final String id, IModel<?> model) {
		super(id, model);
	}

	@Override
	public void onComponentTagBody(final MarkupStream markupStream,
			final ComponentTag openTag) {
		replaceComponentTagBody(markupStream, openTag,
				getDefaultModelObjectAsString() + " - U-QASAR");
	}
}
