/*
 */
package eu.uqasar.web.components.navigation;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuHeader;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import eu.uqasar.web.pages.adapterdata.AdapterManagementPage;
import eu.uqasar.web.pages.admin.companies.CompanyListPage;
import eu.uqasar.web.pages.admin.meta.MetaDataSettingsPage;
import eu.uqasar.web.pages.admin.qmodel.QModelSettingsPage;
import eu.uqasar.web.pages.admin.settings.LdapSettingsPage;
import eu.uqasar.web.pages.admin.settings.MailSettingsPage;
import eu.uqasar.web.pages.admin.settings.platform.PlatformSettingsPage;
import eu.uqasar.web.pages.admin.teams.TeamImportPage;
import eu.uqasar.web.pages.admin.teams.TeamListPage;
import eu.uqasar.web.pages.admin.users.UserImportPage;
import eu.uqasar.web.pages.admin.users.UserListPage;
import eu.uqasar.web.pages.processes.ProcessManagementPage;
import eu.uqasar.web.pages.products.ProductManagementPage;

/**
 *
 *
 */
public class AdminSubMenu extends NavbarDropDownButton {

	public AdminSubMenu(IModel<String> labelModel) {
		super(labelModel);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		get("dropdown-menu").add(new CssClassNameAppender("admin"));
		getParent().add(new AttributeModifier("id", "adminsMenu"));
		setIconType(new IconType("cogs"));
		get("btn").add(new AttributeModifier("title", new StringResourceModel("menu.admin.link.text", this, null)));
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}
	
	@Override
	protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
		final List<AbstractLink> subMenu = new ArrayList<>();

	
		// subMenu.add(new MenuHeader(new StringResourceModel("menu.admin.companies.header.text", this, null)));
		
		
		
		subMenu.add(new MenuHeader(new StringResourceModel("menu.admin.users.header.text", this, null)));
		
		subMenu.add(new MenuBookmarkablePageLink<CompanyListPage>(CompanyListPage.class, 
				new StringResourceModel("menu.admin.companies.list.link.text", this, null))
				.setIconType(new IconType("home")));
//		subMenu.add(new MenuBookmarkablePageLink<CompanyListPage>(CompanyListPage.class, 
//				new StringResourceModel("menu.admin.companies.import.link.text", this, null))
//				.setIconType(new IconType("signin")));		
		subMenu.add(new MenuBookmarkablePageLink<UserListPage>(UserListPage.class, 
				new StringResourceModel("menu.admin.users.list.link.text", this, null))
				.setIconType(new IconType("user")));
		subMenu.add(new MenuBookmarkablePageLink<UserImportPage>(UserImportPage.class, 
				new StringResourceModel("menu.admin.users.import.link.text", this, null))
				.setIconType(new IconType("signin")));
		subMenu.add(new MenuBookmarkablePageLink<TeamListPage>(TeamListPage.class, 
				new StringResourceModel("menu.admin.teams.list.link.text", this, null))
				.setIconType(new IconType("group")));
		subMenu.add(new MenuBookmarkablePageLink<TeamImportPage>(TeamImportPage.class, 
				new StringResourceModel("menu.admin.teams.import.link.text", this, null))
				.setIconType(new IconType("signin")));
		subMenu.add(HeaderNavigationBar.createMenuDevider());
		subMenu.add(new MenuHeader(new StringResourceModel("menu.admin.products_processes.header.text", this, null)));
		subMenu.add(new MenuBookmarkablePageLink<>(ProductManagementPage.class,
				new PageParameters(),
				new StringResourceModel("menu.products.manage.text", this, null)));
		subMenu.add(new MenuBookmarkablePageLink<>(ProcessManagementPage.class,
				new PageParameters(),
				new StringResourceModel("menu.processes.manage.text", this, null)));
		subMenu.add(HeaderNavigationBar.createMenuDevider());
		subMenu.add(new MenuHeader(new StringResourceModel("menu.admin.settings.header.text", this, null)));
		subMenu.add(new MenuBookmarkablePageLink<MetaDataSettingsPage>(MetaDataSettingsPage.class, 
				new StringResourceModel("menu.admin.settings.metadata.link.text", this, null))
				.setIconType(new IconType("puzzle-piece")));
		subMenu.add(new MenuBookmarkablePageLink<LdapSettingsPage>(LdapSettingsPage.class, 
				new StringResourceModel("menu.admin.settings.ldap.link.text", this, null))
				.setIconType(new IconType("sitemap")));
		subMenu.add(new MenuBookmarkablePageLink<MailSettingsPage>(MailSettingsPage.class, 
				new StringResourceModel("menu.admin.settings.mail.link.text", this, null))
				.setIconType(new IconType("envelope")));
		subMenu.add(new MenuBookmarkablePageLink<AdapterManagementPage>(AdapterManagementPage.class,
				new StringResourceModel("menu.admin.settings.adapters.link.text", this, null))
				.setIconType(new IconType("pencil")));
		subMenu.add(new MenuBookmarkablePageLink<QModelSettingsPage>(QModelSettingsPage.class, 
				new StringResourceModel("menu.admin.settings.qmodel.link.text", this, null))
				.setIconType(new IconType("puzzle-piece")));
		subMenu.add(new MenuBookmarkablePageLink<PlatformSettingsPage>(PlatformSettingsPage.class, 
				new StringResourceModel("menu.admin.settings.platform.link.text", this, null))
				.setIconType(IconType.globe));

		return subMenu;
	}
	
}
