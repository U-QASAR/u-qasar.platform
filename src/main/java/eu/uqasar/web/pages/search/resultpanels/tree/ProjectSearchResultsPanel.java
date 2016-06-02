package eu.uqasar.web.pages.search.resultpanels.tree;

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


import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

import eu.uqasar.model.tree.Project;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.tree.ProjectSearchService;
import eu.uqasar.web.pages.search.resultpanels.AbstractSearchResultsPanel;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;

/**
 *
 *
 */
public class ProjectSearchResultsPanel extends AbstractSearchResultsPanel<Project> {

	@Inject 
	ProjectSearchService service;
	
    public ProjectSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, Project.class);
    }

    @Override
    protected void populateDataViewItem(Item<Project> item) {
        // TODO add quality indicator icon!
        final Project entity = item.getModelObject();
        BookmarkablePageLink<ProjectViewPage> link = new BookmarkablePageLink<>("link",
                ProjectViewPage.class, ProjectViewPage.forProject(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);

        item.add(new Label("startdate", Model.of(entity.getStartDate())));
        item.add(new Label("enddate", Model.of(entity.getEndDate())));
        item.add(new Label("progress", Model.of(entity.
                getElapsedVsRemainingWithPercentageString())));
    }

	@Override
	protected <S extends AbstractService<Project>> S getService() {
		return (S) service;
	}
}
