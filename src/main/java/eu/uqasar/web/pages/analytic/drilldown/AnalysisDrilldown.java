package eu.uqasar.web.pages.analytic.drilldown;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2MultiChoice;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import eu.uqasar.model.analytic.Analysis;
import eu.uqasar.model.analytic.Dimensions;
import eu.uqasar.service.AnalyticService;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.BaseTreePage;

/**
 * 
 *
 *
 */

public class AnalysisDrilldown extends BasePage {

	@Inject
	AnalyticService analyticService;
	
	private static final long serialVersionUID = -8550092382808118552L;
	
	private final List<String> headerLabel;
	private final List<String> dataResults;
	
	private final String cubesJSON;
	private final Form<Analysis> form;
	private final Analysis analysis;

	private final String url = "http://uqasar.pythonanywhere.com/cube/jira/aggregate?";
	
	private String query = url;
			
	// sample of cuts to be added 
//	private String cuts = "&cut=Type:Bug" + "&cut=Assignee:ATB%20Team";
	
	
	public AnalysisDrilldown(PageParameters parameters) {
		super(parameters);
		
		//TODO: add support for new Analysis
		StringValue id = parameters.get("analysis-id");
		analysis = analyticService.getById(id.toOptionalLong());
		
		// Prepares the String for the REST Query
		prepareQuery();
		
		// The query is single sent to Cubes Server and JSON is stored into String to be parsed
		cubesJSON = getJSON(query);
		
		// Add the title of the headers
		headerLabel = getHeaders();
		
		// Add the data to be shown in the table
		dataResults = getDataFromDimension(cubesJSON);
		
		// Analysis name
		add(new Label("name", new PropertyModel<String>(analysis, "name")));
		
		// Analysis description
		add(new Label("description", new PropertyModel<String>(analysis, "description")));

		// Add a link to JSON in order to test the Query
		add(linkToJSON());

		
		// Add the form to add/remove drilldown elements
		form = new Form<>("form");
		
		// Add the dimension selector 
		form.add(new Select2MultiChoice<>("dimensionSelector",
                new PropertyModel<Collection<Dimensions>>(analysis, "dimensions"), new DimensionProvider()));
		
		// Add the button to Submit the form with the Dimension changes
		Button saveButton = new Button("save", new StringResourceModel(
				"button.save", this, null));

		saveButton.add(new AjaxFormSubmitBehavior(form, "onclick") {
			private static final long serialVersionUID = 4973315405621990914L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				super.onEvent(target);
				setResponsePage(AnalysisDrilldown.class, BaseTreePage.forAnalysis(analysis));
			}

		});
		form.add(saveButton);
		
		add(form);
		
		// // TableOfResults, add header for the table
		RepeatingView headers = new RepeatingView("headers");
		for (String label : headerLabel) {
			headers.add(new Label(headers.newChildId(), label));
		}
		add(headers);
		
		// TableOfResults, add the part of the table that show data
		add(new ListView<String>("contentlist", dataResults){
			private static final long serialVersionUID = -9149319929661254397L;

			@Override
			protected void populateItem(ListItem<String> colData) {
				final String string = colData.getModelObject();
				RepeatingView repeatingView = new RepeatingView("data");
				
				for (String label : headerLabel) {
				try {
					JSONObject json = new JSONObject(string);
					repeatingView.add(new Label(repeatingView.newChildId(),json.getString(label)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
				colData.add(repeatingView);
			}
		});
				
	}
	
	
	/**
	 *  Prepare the query to be passed to REST
	 */
	private void prepareQuery() {
		for (Dimensions dim : analysis.getDimensions()) {
			query +="&drilldown=" + dim.name(); 
		}
	}


	/**
	 * @param url
	 * @return Returns the JSON 
	 */
	private String getJSON(String url){
		JSONResource res = null;
		JSONObject obj = null;
		Resty resty = new Resty();
		
		try {
			res = resty.json(url);
			obj = res.toObject();
			
		} catch (IOException | JSONException e) {
			// TODO: not my favorite way but does the trick
//			e.printStackTrace();
			return getJSON(url);
		}
		
		return obj.toString();
	}
	
	
	/**
	 * @param jsonObject
	 * @return Return a List with all the data from JSON 
	 */
	private List<String> getDataFromDimension(String jsonObject) {
		List<String> data = new ArrayList<>();
		try {
			JSONObject jobj = new JSONObject(jsonObject);
			us.monoid.json.JSONArray jsonArray = jobj.getJSONArray("cells");
			
			for (int i = 0; i < jsonArray.length() ; i++) {
				JSONObject row = jsonArray.getJSONObject(i);
				
				data.add(row.toString());
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return data;
	}

	
	/**
	 * @return Return as headers the dimensions stored on Analysis
	 */
	private List<String> getHeaders() {
		List<String> headers = new ArrayList<>();

		for (Dimensions dim : analysis.getDimensions()) {
			headers.add(dim.name());
		}

		headers.add("count");

		return headers;
	}

//	/**
//	 * @param jsonObj
//	 * @return Return the headers from JSON
//	 */
//	private List<String> getHeadersFromJSON(String jsonObj){
//		List<String> headers = new ArrayList<String>();
//		
//		try {
//			JSONObject json =  new  JSONObject(jsonObj);
//			json = json.getJSONObject("levels");
//			
//			Iterator <String> header =  json.keys();
//			while(header.hasNext()){
//				headers.add(header.next());
//			}
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		headers.add("count");
//		
//		return headers;
//	} 
	
    /**
     * @return a link to JSON response in order to test the query
     */
    private ExternalLink linkToJSON() {
      ExternalLink link = new ExternalLink("linkCubesQuery", query) {
        private static final long serialVersionUID = 1L;

        @Override
        protected void onComponentTag(ComponentTag tag) {
          super.onComponentTag(tag);
          tag.put("target", "_blank");
        }
      };

      link.add(new Label("query", query));

      return link;
    }


	/**
	 *  
	 * Provides dimensions values to be used in Queries
	 *
	 */
	public class DimensionProvider extends TextChoiceProvider<Dimensions> {

		private static final long serialVersionUID = -5378166419263242594L;

		@Override
		protected String getDisplayText(Dimensions choice) {
			return choice.name();
		}

		@Override
		protected Object getId(Dimensions choice) {
			return choice ;
		}

		@Override
		public void query(String arg0, int arg1, Response<Dimensions> response) {
			response.addAll(Dimensions.getAllDimensions());
		}

		@Override
		public Collection<Dimensions> toChoices(Collection<String> ids) {
	    	ArrayList<Dimensions> dimensions = new ArrayList<>();
	    	
	    	for(String id : ids){
	    		dimensions.add(Dimensions.valueOf(id));
	    	} 	

	    	return dimensions;
		}

	}

}