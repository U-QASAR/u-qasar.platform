package eu.uqasar.web.pages.analytic;

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

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.analytic.Analysis;
import eu.uqasar.service.AnalyticService;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.analytic.drilldown.AnalysisDrilldown;
import eu.uqasar.web.pages.analytic.editor.AnalysisEditor;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.provider.EntityProvider;

/**
 *
 *
 */

public class AnalyticWorkbench extends BasePage {
	
	private static final long serialVersionUID = 3921608240912068114L;

	@Inject
	AnalyticService analyticService;

	// TODO implement load analysis from a provide ID project 
//	@Inject
//	TreeNodeService treeNodeService;
	
	private final WebMarkupContainer analysisContainer = new WebMarkupContainer("analysisContainer");

	
	// how many adapters do we show per page
	private static final int itemsPerPage = 10;
	
	public AnalyticWorkbench(PageParameters parameters) {
		super(parameters);
		
		// TODO implement load analysis from a provide ID project 
//		// String key = "U-QASAR";
//		String key = getPageParameters().get("project-key").toOptionalString();
//		Project project = treeNodeService.getProjectByKey(key);
		
		// Added button to create new Analysis
		add(newAnalysisButton());
		
		// Add the list of all the Analysis
		DataView<Analysis> analysis = new DataView<Analysis>("analysis", new AnalysisProvider(), itemsPerPage) {

			private static final long serialVersionUID = -2496974746220295976L;

			@Override
			protected void populateItem(Item<Analysis> item) {
				final Analysis analysis = item.getModelObject();
				
				// Analysis title 
				item.add(viewAnalysisLink(analysis));
				
				// Analysis icon edit
				item.add(analysisEditorLink(analysis));
				
				// Analysis icon delete
				item.add(delAnalysis(analysis));
				
				// Analysis belonging Project label
				item.add(new Label("project", new PropertyModel<String>(analysis.getProject(), "name")));
			}
		};
		
		analysisContainer.add(analysis);
		
		analysisContainer.setOutputMarkupId(true);
		add(analysisContainer);
		
	}

	/**
	 * @param analysis
	 * @return the link to the provided ID Analysis view Page
	 */
	private BookmarkablePageLink<AnalysisDrilldown> viewAnalysisLink (final Analysis analysis){
		BookmarkablePageLink<AnalysisDrilldown> link = new BookmarkablePageLink<>("link", AnalysisDrilldown.class, BaseTreePage.forAnalysis(analysis));
		link.add(new Label("name", new PropertyModel<String>(analysis, "name")));
		return link;
	}
	
	/**
	 * @param analysis
	 * @return The link to edit the current Analysis in the Analysis editor
	 */
	private BookmarkablePageLink<AnalysisEditor> analysisEditorLink(final Analysis analysis){
        return new BookmarkablePageLink<>("edit", AnalysisEditor.class, BaseTreePage.forAnalysis(analysis));
	}
	
	
	private AjaxLink<Void> delAnalysis(final Analysis analysis){
		return new AjaxLink<Void>("delete") {
			private static final long serialVersionUID = -2489881869944830108L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				analyticService.delete(analysis);
				target.add(analysisContainer);
			}
		};
	}
	
	/**
	 * @return the button to browse the analytic editor to create a new one
	 */
	private Link<AnalysisEditor> newAnalysisButton(){
		return new Link<AnalysisEditor>("newAnalysis"){
			private static final long serialVersionUID = -3034634884355412337L;

			@Override
			public void onClick() {
				setResponsePage(AnalysisEditor.class, new PageParameters().add("analysis-id", "new"));
			}	
		};
		
	}

	/**
	 * 
	 */
	private final class AnalysisProvider extends EntityProvider<Analysis>{

		private static final long serialVersionUID = 3133883469715616988L;

		@Override
		public Iterator<? extends Analysis> iterator(long first, long count) {
			// TODO get analysis only for the current project key and first and count
			return analyticService.getAllAnalysis().iterator();
		}

		@Override
		public long size() {
			return analyticService.countAll();
		}
		
	}
	
}
