package eu.uqasar.web.components.navigation;

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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import eu.uqasar.web.i18n.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LanguageSubMenu extends NavbarDropDownButton {

	private static final long serialVersionUID = 3888693104220442599L;

	private final Page containingPage;

	public LanguageSubMenu(Page containingPage, IModel<String> labelModel) {
		super(labelModel);
		this.containingPage = containingPage;
		setIconType(IconType.globe);
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}

	@Override
	protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
		final List<AbstractLink> links = new ArrayList<>();
		for (Language language : Language.values()) {
			links.add(getLocaleChangeLink(language));
		}
		return links;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		getParent().add(new AttributeModifier("id", "languagesMenu"));
		get("dropdown-menu").add(new CssClassNameAppender("languages"));
	}

	private AbstractLink getLocaleChangeLink(final Language language) {
		Locale locale = language.getLocale();
		PageParameters params = new PageParameters(
				containingPage.getPageParameters());
		params.remove("lang");
		params.add("lang", language.getLocale().getLanguage());
		BootstrapBookmarkablePageLink<Page> link = new MenuBookmarkablePageLink<Page>(
                containingPage.getPageClass(), params, Model.of(locale
                .getDisplayLanguage(locale))).setIconType(new IconType(
								"please-ignore-me"));
		link.add(new CssClassNameAppender("language ", locale.getLanguage()));
		if (Language.fromSession().equals(language)) {
			link.add(new CssClassNameAppender("active"));
		}
		return link;
	}
}
