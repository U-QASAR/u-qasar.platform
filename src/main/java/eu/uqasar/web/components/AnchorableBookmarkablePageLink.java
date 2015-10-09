package eu.uqasar.web.components;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 * @param <T>
 */
public class AnchorableBookmarkablePageLink<T> extends BookmarkablePageLink<T> {

	private final String anchor;
	
	public <C extends Page> AnchorableBookmarkablePageLink(final String id, final Class<C> pageClass, final String anchor) {
		super(id, pageClass);
		this.anchor = anchor;
	}

	public <C extends Page> AnchorableBookmarkablePageLink(final String id, final Class<C> pageClass, final PageParameters parameters, final String anchor) {
		super(id, pageClass, parameters);
		this.anchor = anchor;
	}

	@Override
	protected CharSequence getURL() {
		return super.getURL() + (StringUtils.isEmpty(anchor) ? "" : "#" + anchor);
	}

}
