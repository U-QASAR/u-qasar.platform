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


import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorEditPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class QualityIndicatorSearchResultsPanel extends AbstractTreeNodeSearchResultPanel<QualityIndicator> {

    public QualityIndicatorSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QualityIndicator.class);
    }

    @Override
    protected void populateDataViewItem(Item<QualityIndicator> item) {
        final QualityIndicator entity = item.getModelObject();
        BookmarkablePageLink<QualityIndicatorEditPage> link = new BookmarkablePageLink<>("link",
                QualityIndicatorEditPage.class, QualityIndicatorEditPage.forQualityIndicator(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);
        
        BookmarkablePageLink<ProjectViewPage> projectLink = new BookmarkablePageLink<>("project",
                ProjectViewPage.class, ProjectViewPage.forProject(entity.getProject()));
        projectLink.add(new Label("projectKey", Model.of(entity.getProject().getNodeKey())));
        item.add(projectLink);
        
        item.add(new Label("value", Model.of(entity.getValue())));
    }
}
