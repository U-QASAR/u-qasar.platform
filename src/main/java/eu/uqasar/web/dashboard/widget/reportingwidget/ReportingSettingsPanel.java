package eu.uqasar.web.dashboard.widget.reportingwidget;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardContextAware;
import ro.fortsoft.wicket.dashboard.web.DashboardPanel;
import ro.fortsoft.wicket.dashboard.web.WidgetPanel;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.service.dataadapter.SonarDataService;
import eu.uqasar.web.dashboard.DashboardViewPage;

/**
 * 
 *
 *
 */
public class ReportingSettingsPanel extends GenericPanel<ReportingWidget> implements DashboardContextAware {
    // public class ReportingSettingsPanel extends BasePage{
    private static final long serialVersionUID = 1L;

    private transient DashboardContext dashboardContext;
    private String cube;
    private SonarDataService dataService;
    private List<String> projects = new ArrayList<>();

    private Map<String, List<String>> rulesMap = new HashMap<>(); // map:rule->additionalRules
    private final String selectedRule;
    private final String selectedAdditionalRule;
    private String chartType;

    private ListView<Rule> rulesView;

    private String urlToLoad;
    private final WebMarkupContainer ruleWebMrkUpContainer;
    private final List<Rule> proposedRules = new ArrayList<>();

    private DropDownChoice<String> rules;
    private DropDownChoice<String> additionalRules;

    public ReportingSettingsPanel(String id, IModel<ReportingWidget> model) {
        super(id, model);

        setOutputMarkupPlaceholderTag(true);

        final ReportingWidget qualityWidget = model.getObject();

        Form<Widget> form = new Form<>("form");

        cube = getModelObject().getSettings().get("cube");
        if (cube == null) {
            cube = "jira";
        }
        selectedAdditionalRule = getModelObject().getSettings().get("selectedAdditionalRule");
        selectedRule = getModelObject().getSettings().get("selectedRule");

        chartType = getModelObject().getSettings().get("chartType");
        if (chartType == null) {
            chartType = ReportingWidget.COLUMN_TYPE;
        }

        try {
            InitialContext ic = new InitialContext();
            dataService = (SonarDataService) ic.lookup("java:module/SonarDataService");
            projects = dataService.getSonarProjects();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        // Add Rules and Additional Rules as DropDownList
        rulesMap = qualityWidget.getRulesMap(projects);

        // //Add selection of cubes for report generation.
        List<String> cubes = Arrays.asList("jira", "sonarcube");
        final DropDownChoice<String> selectedCubes = new DropDownChoice<>("cube",
                new PropertyModel<String>(this, "cube"), cubes);
        selectedCubes.setRequired(true);
        form.add(selectedCubes);

        // Field for the chart type
        chartType = getModelObject().getSettings().get("chartType");
        DropDownChoice<String> choice = new DropDownChoice<>("chartType", new PropertyModel<String>(this, "chartType"),
                ReportingWidget.TYPES);
        form.add(choice);

        // Create a void form for ListView and WebMarkupContainer
        Form<Void> formVoid = new Form<>("formVoid");
        ruleWebMrkUpContainer = new WebMarkupContainer("ruleContainer", new Model<Rule>());
        ruleWebMrkUpContainer.setOutputMarkupId(true);
        formVoid.add(ruleWebMrkUpContainer);

        ruleWebMrkUpContainer.add(rulesView = new ListView<Rule>("rulesListView", Model.ofList(proposedRules)) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onConfigure() {
                super.onConfigure();
                // update model
                rulesView.setModelObject(proposedRules);
            }

            @Override
            protected void populateItem(ListItem<Rule> item) {

                final Rule proposedRule = item.getModelObject();

                // //get dropdown list method will give two different lists..
                IModel<List<? extends String>> ruleChoices = new AbstractReadOnlyModel<List<? extends String>>() {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public List<String> getObject() {
                        return new ArrayList<>(rulesMap.keySet());
                    }

                };

                IModel<List<? extends String>> additionalRuleChoices = new AbstractReadOnlyModel<List<? extends String>>() {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public List<String> getObject() {
                        List<String> models = rulesMap.get(proposedRule.getSelectedRule()); // very important
                        // System.out.println("selectedRule : " + proposedUser.getSelectedRule());
                        if (models == null) {
                            models = Collections.emptyList();
                        }
                        return models;
                    }

                };

                item.add(rules = new DropDownChoice<>("rules", new PropertyModel<String>(proposedRule, "selectedRule"),
                        ruleChoices));
                rules.setOutputMarkupId(true);
                rules.setNullValid(true);
                rules.setRequired(true);
                rules.setMarkupId("rules" + item.getIndex()); // very important

                item.add(additionalRules = new DropDownChoice<>("additionalRules", new PropertyModel<String>(
                        proposedRule, "selectedAdditionalRule"), additionalRuleChoices));
                additionalRules.setOutputMarkupId(true);
                additionalRules.setMarkupId("additionalRules" + item.getIndex()); // very important
                additionalRules.setNullValid(true);
                additionalRules.setRequired(true);
                rules.add(new AjaxFormComponentUpdatingBehavior("onchange") { // very important
                        /**
                     * 
                     */
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            target.add(additionalRules);
                            target.add(rules);
                        }
                    });

                additionalRules.add(new AjaxFormComponentUpdatingBehavior("onchange") { // very important
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            target.add(additionalRules);
                            target.add(rules);
                        }
                    });
            }
        });

        AjaxSubmitLink addRuleButton = new AjaxSubmitLink("add.rule", formVoid) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> formVoid) {
                // target.add(feedbackPanel);
                target.add(formVoid);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> formVoid) {

                addNewRuleToList(target, formVoid);
            }
        };
        addRuleButton.add(new Label("button.add.save",
            new StringResourceModel("button.add.save", this, Model.of(proposedRules))));
        formVoid.add(addRuleButton);

        rulesView.setOutputMarkupId(true);
        form.add(formVoid);

        form.add(new AjaxSubmitLink("submit") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Dashboard dashboard = findParent(DashboardPanel.class).getDashboard();
                if (dashboard != null && dashboardContext != null) {

                    // Here, create a url query based on selected Rule and Additional Rule from dynamic dropdown lists..
                    urlToLoad = createRule();
                    System.out.println(urlToLoad);
                    getModelObject().getSettings().put("urlToLoad", urlToLoad);

                    getModelObject().getSettings().put("cube", cube);
                    getModelObject().getSettings().put("selectedAdditionalRule", selectedAdditionalRule);
                    getModelObject().getSettings().put("selectedRule", selectedRule);
                    getModelObject().getSettings().put("chartType", chartType);

                    System.out.print("dashboard : " + dashboard);
                    dashboardContext.getDashboardPersiter().save(dashboard);
                    hideSettingPanel(target);

                    WidgetPanel widgetPanel = findParent(WidgetPanel.class);
                    ReportingWidget tasksWidget = (ReportingWidget) widgetPanel.getModelObject();
                    tasksWidget.setTitle("Reporting Widget For " + cube + " cube");
                    ReportingWidgetView widgetView = (ReportingWidgetView) widgetPanel.getWidgetView();
                    target.add(widgetView);

                    PageParameters params = new PageParameters();
                    DbDashboard dbdb = (DbDashboard) dashboard;
                    params.add("id", dbdb.getId());
                    setResponsePage(DashboardViewPage.class, params);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }

        });

        form.add(new AjaxLink<Void>("cancel") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                hideSettingPanel(target);
            }
        });

        add(form);

    }

    @Override
    public void setDashboardContext(DashboardContext dashboardContext) {
        this.dashboardContext = dashboardContext;
    }

    private void hideSettingPanel(AjaxRequestTarget target) {
        setVisible(false);
        target.add(this);
    }

    /**
     * 
     * @param target
     * @param form
     */
    private void addNewRuleToList(AjaxRequestTarget target, Form<?> form) {

        proposedRules.add(new Rule("", ""));
        target.add(ruleWebMrkUpContainer);
        target.add(form);
    }

    private String createRule() {
        String urlToLoad = "/aggregate?";
        // for all proposed Rules from dynamic dropdown lists
        for (Rule rule : proposedRules) {
            if (rule.getSelectedRule().equalsIgnoreCase("drilldown") && !rule.getSelectedRule().equalsIgnoreCase("")) {
                // both selectedrules and Additionalrules can not be empty
                if (!rule.getSelectedAdditionalRule().equalsIgnoreCase("")) {
                    urlToLoad += "&drilldown=" + rule.getSelectedAdditionalRule();
                }
            } else {
                // both selectedrules and Additionalrules can not be empty
                if (!rule.getSelectedAdditionalRule().equalsIgnoreCase("") && !rule.getSelectedRule().equalsIgnoreCase("")) {
                    urlToLoad += "&cut=" + rule.getSelectedRule() + ":" + rule.getSelectedAdditionalRule();
                }
            }
        }

        return urlToLoad;

    }// EOM

}// EOC
