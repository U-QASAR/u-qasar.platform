package eu.uqasar.web.pages.search;

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


import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.search.resultpanels.CompanySearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.ProcessSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.ProductSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.TeamSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.UserSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.MetricSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.ProjectSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.QMMetricSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.QMQualityIndicatorSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.QMQualityObjectiveSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.QModelSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.QualityIndicatorSearchResultsPanel;
import eu.uqasar.web.pages.search.resultpanels.tree.QualityObjectiveSearchResultsPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class SearchResultsPage extends BasePage {

    private static final String QUERY_PARAM = "query";
    private final TextField<String> searchField;
    private final String query;

    public SearchResultsPage(PageParameters parameters) {
        super(parameters);
        query = parameters.get(QUERY_PARAM).toString(null);
        Form<Void> form = new InputValidationForm<Void>("form") {
            @Override
            protected void onSubmit() {
                setResponsePage(SearchResultsPage.class, searchFor(searchField.getModelObject()));
            }
        };
        searchField = new TextField<>("searchField", new PropertyModel(this, "query"));
        searchField.setRequired(true);
        InputBorder<String> border = new InputBorder<>("searchValidationBorder",
                searchField, new StringResourceModel("label.search.term", this, null));
        form.add(border);
        add(form);
        
        add(new ProjectSearchResultsPanel("projects", query));
        add(new QualityObjectiveSearchResultsPanel("qualityObjectives", query));
        add(new QualityIndicatorSearchResultsPanel("qualityIndicators", query));
        add(new MetricSearchResultsPanel("metrics", query));
        
        add(new ProductSearchResultsPanel("products", query));
        add(new ProcessSearchResultsPanel("processes", query));
        
        add(new UserSearchResultsPanel("user", query));
        add(new CompanySearchResultsPanel("company", query));
        add(new TeamSearchResultsPanel("team", query));
        add(new QModelSearchResultsPanel("qualitymodel", query));
        add(new QMQualityObjectiveSearchResultsPanel("qmqualityObjectives", query));
        add(new QMQualityIndicatorSearchResultsPanel("qmqualityIndicators", query));
        add(new QMMetricSearchResultsPanel("qmmetrics", query));
    }
    
    public static PageParameters searchFor(final String query) {
        return new PageParameters().add(QUERY_PARAM, query == null ? "" : query);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("headline", getPageTitleModel()));
        super.setSearchTerm(query);
    }

    @Override
    protected IModel<String> getPageTitleModel() {
        if (StringUtils.isEmpty(query)) {
            return new StringResourceModel("page.title.empty", this, null);
        } else {
            return new StringResourceModel("page.title.resultsfor", this, null, Model.
                    of(query));
        }
    }

}
