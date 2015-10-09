package eu.uqasar.web.components.navigation;

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
