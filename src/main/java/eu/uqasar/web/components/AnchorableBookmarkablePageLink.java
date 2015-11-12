package eu.uqasar.web.components;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
