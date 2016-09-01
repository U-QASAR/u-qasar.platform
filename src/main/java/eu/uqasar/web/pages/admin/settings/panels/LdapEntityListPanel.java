package eu.uqasar.web.pages.admin.settings.panels;

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


import eu.uqasar.model.settings.ldap.LdapSettings;
import eu.uqasar.util.ldap.LdapEntity;
import eu.uqasar.util.ldap.LdapManager;
import eu.uqasar.web.provider.ldap.LdapEntityProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.naming.NamingException;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 *
 *
 * @param <Entity>
 */
public abstract class LdapEntityListPanel<Entity extends LdapEntity> extends Panel {

	private final LdapEntityProvider<Entity> provider;

	private boolean selectionEnabled;
	private boolean headerFixed = true;

	private int maxEntitiesToShow = Integer.MAX_VALUE;
	private String containerCSSClass = "ldap-table-container-preview";
	private String listHeaderCSSClass = "ldap-table-container-fixed-header";
	private DataView<Entity> dataView;
	private WebMarkupContainer tableContainer;
	private WebMarkupContainer listHeader;
	private CheckGroup<Entity> checkGroup;
	private CheckGroupSelector checkGroupSelector;

	LdapEntityListPanel(String id, LdapEntityProvider<Entity> provider) {
		super(id);
		this.provider = provider;
		setOutputMarkupId(true);
	}

	public boolean isHeaderFixed() {
		return headerFixed;
	}

	public LdapEntityListPanel<Entity> setHeaderFixed(boolean headerFixed) {
		this.headerFixed = headerFixed;
		return this;
	}

	private String getListHeaderCSSClass() {
		return listHeaderCSSClass;
	}

	public LdapEntityListPanel<Entity> setListHeaderCSSClass(String listHeaderCSSClass) {
		this.listHeaderCSSClass = listHeaderCSSClass;
		return this;
	}

	private String getContainerCSSClass() {
		return containerCSSClass;
	}

	public LdapEntityListPanel<Entity> setContainerCSSClass(String containerCSSClass) {
		this.containerCSSClass = containerCSSClass;
		return this;
	}

	boolean isSelectionEnabled() {
		return selectionEnabled;
	}

	public LdapEntityListPanel<Entity> setSelectionEnabled(boolean selectionEnabled) {
		this.selectionEnabled = selectionEnabled;
		return this;
	}

	public int getMaxEntitiesToShow() {
		return maxEntitiesToShow;
	}

	public LdapEntityListPanel<Entity> setMaxEntitiesToShow(int maxEntitiesToShow) {
		this.maxEntitiesToShow = maxEntitiesToShow;
		return this;
	}

	public LdapManager update(LdapSettings settings) throws NamingException {
		provider.setMaximumNoOfEntities(this.maxEntitiesToShow);
		LdapManager instance = LdapManager.getInstance(settings);
		provider.update(instance);
		return instance;
	}

	public void reset() {
		provider.reset();
	}

	public long getNoOfCurrentlyListedEntities() {
		return provider.size();
	}
	
	public Collection<Entity> getCurrentlySelectedEntities() {
		return Collections.unmodifiableCollection(checkGroup.getModelObject());
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		checkGroup = newCheckGroup();
		checkGroupSelector = new CheckGroupSelector("entityGroupSelector", checkGroup);
		provider.setMaximumNoOfEntities(maxEntitiesToShow);
		dataView = new DataView<Entity>("entities", provider) {
			@Override
			protected void populateItem(Item<Entity> item
			) {
				Check<Entity> check = new Check<>("check", item.getModel(), checkGroup);
				check.setVisible(isSelectionEnabled());
				item.add(check);
				LdapEntityListPanel.this.populateItem(item, check);
			}
		};
		tableContainer = new WebMarkupContainer("tableContainer");
		tableContainer.addOrReplace(dataView);
		listHeader = new WebMarkupContainer("listHeader");
		listHeader.addOrReplace(checkGroupSelector);
		tableContainer.addOrReplace(listHeader);
		tableContainer.addOrReplace(new Label("javascript", headerFixed ? getJavaScript() : "").setEscapeModelStrings(false));
		checkGroup.addOrReplace(tableContainer.setOutputMarkupId(true));
		addOrReplace(checkGroup);

		checkGroupSelector.setVisible(this.selectionEnabled);
		tableContainer.setVisible(dataView.getItemCount() > 0);
		if (!StringUtils.isEmpty(getContainerCSSClass())) {
			tableContainer.add(new AttributeAppender("class", getContainerCSSClass()));
		}
		if (!StringUtils.isEmpty(getContainerCSSClass())) {
			listHeader.add(new AttributeAppender("class", getListHeaderCSSClass()));
		}
	}

	protected abstract void populateItem(Item<Entity> item, Check<Entity> check);

	protected abstract void selectionChanged(AjaxRequestTarget target);

	private String getJavaScript() {
        return "	var $table = $('#entityList');\n"
                + "	$table.floatThead({\n"
                + "			scrollContainer: function() {\n"
                + "			return $('#tableContainer');\n"
                + "		}\n"
                + "	});\n";
	}

	private CheckGroup newCheckGroup() {
		CheckGroup<Entity> group = new CheckGroup<>("entityGroup", new ArrayList<Entity>());
		group.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				selectionChanged(target);
			}
		});
		return group;
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forUrl("assets/js/jquery.floatThead.min.js"));
	}
}
