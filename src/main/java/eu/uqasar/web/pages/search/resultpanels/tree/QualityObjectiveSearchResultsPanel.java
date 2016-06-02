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


import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveEditPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class QualityObjectiveSearchResultsPanel extends AbstractTreeNodeSearchResultPanel<QualityObjective> {

    public QualityObjectiveSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QualityObjective.class);
    }

    @Override
    protected void populateDataViewItem(Item<QualityObjective> item) {
        final QualityObjective entity = item.getModelObject();
       BookmarkablePageLink<QualityObjectiveEditPage> link = new BookmarkablePageLink<>("link",
                QualityObjectiveEditPage.class, QualityObjectiveEditPage.forQualityObjective(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);
        
        BookmarkablePageLink<ProjectViewPage> projectLink = new BookmarkablePageLink<>("project",
                ProjectViewPage.class, ProjectViewPage.forProject(entity.getProject()));
        projectLink.add(new Label("projectKey", Model.of(entity.getProject().getNodeKey())));
        item.add(projectLink);
        
        item.add(new Label("purpose", Model.of(entity.getQualityPurpose())));
        item.add(new Label("value", Model.of(entity.getValue())));
    }
}
