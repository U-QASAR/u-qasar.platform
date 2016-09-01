package eu.uqasar.web.components.jquerytextcomplete;

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


import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.settings.IJavaScriptLibrarySettings;


@Setter
@Getter
public class JQueryTextCompleteEditor<T> extends WebMarkupContainer {

	private static final long serialVersionUID = 1L;
	// URL for RESTful service where the list of autocomplete items can be 
	// fetched
	private String autocompleteDataUrl;
	
	public JQueryTextCompleteEditor(String id) {
		super(id);
	}

	public JQueryTextCompleteEditor(String id, IModel<T> model) {
		super(id, model);
		setOutputMarkupId(true);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		setOutputMarkupId(true);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		//if component is disabled we don't have to load the JQueryUI datepicker
		if(!isEnabledInHierarchy())
			return;
		//add bundled JQuery
		IJavaScriptLibrarySettings javaScriptSettings =          
	                      getApplication().getJavaScriptLibrarySettings();
		response.render(JavaScriptHeaderItem.
				forReference(javaScriptSettings.getJQueryReference()));
		//add package resources
		response.render(JavaScriptHeaderItem.
				forReference(new PackageResourceReference(getClass(), "jquery-1.10.2.js")));
		response.render(JavaScriptHeaderItem.
				forReference(new PackageResourceReference(getClass(), "jquery.overlay.js")));
		response.render(JavaScriptHeaderItem.
			      forReference(new PackageResourceReference(getClass(), "jquery.textcomplete.js")));
		response.render(JavaScriptHeaderItem.
			      forReference(new PackageResourceReference(getClass(), "shCore.js")));
		response.render(JavaScriptHeaderItem.
			      forReference(new PackageResourceReference(getClass(), "shBrushJScript.js")));
		response.render(JavaScriptHeaderItem.forScript(getMainJs(), "main.js"));
		response.render(JavaScriptHeaderItem.forScript(getCompleteJs(), "complete.js"));
		response.render(JavaScriptHeaderItem.forScript(setContentEditableTextToTextArea(), "setContentEditableTextToTextArea.js"));
	}

	private CharSequence setContentEditableTextToTextArea(){
		return  "$(document).ready(function() {"
					+"$('#" + getMarkupId() + "').keyup(function() {"
						+ "$(\'#hiddenField\').text($(this).text().trim());"
						+ "console.log('Formula: ', $(\'#hiddenField\').text());"
					+ "});"
					+ "var split = $(\'#hiddenField\').text().split(' ');"
					+ "var htmlArray = [];"
					+ "for(var i=0; i<split.length; i++){"
					// sourround with <span> and parse html
						+ "if(split[i].length>5){"
							+ "htmlArray[i] = '<span class=\"contentEdit\"contenteditable=\"false\">' + split[i] + ' ' + '</span>' + '&nbsp';"
						+ "} else{"
							+ "htmlArray[i] = split[i] + ' ';"
						+ "}"
					+ "}"
					+ "$('#" + getMarkupId() + "').html(htmlArray);"
				+"});";
	}
	
	/**
	 * JavaScript for text completion feature
	 * @return
	 */
	private CharSequence getMainJs() {
		return 
			"$(function () {"
			  +"$(\'.script\').each(function () {"	
			    +"eval($(this).text());"
			  +"});"

			  +"var setText = function ($textarea, text) {"
			    +"var range, textarea = $textarea.get(0);"
			    +"textarea.focus();"
			    +"if (typeof textarea.selectionStart === \'number\') {"
			      +"textarea.value = text;"
			      +"textarea.selectionStart = textarea.selectionEnd = text.length;"
			      +"return;"
			    +"}"
			    +"range = textarea.createTextRange();"
			    +"range.text = text"
			    +"range.select();"
			  +"}"
			    
			  
			  // hiddenField
			  +"var $textarea = $(\'#hiddenField\');"
			  +"var textarea = $textarea.get(0);"
			  +"textarea.focus();"
			  +"if (typeof textarea.selectionStart === \'number\') {"
			    +"textarea.selectionStart = textarea.selectionEnd = $textarea.val().length;"
			  +"} else {"
			    +"var range = textarea.createTextRange();"
			    +"range.select();"
			  +"}"
			  +"$textarea.keyup();"
			  +"SyntaxHighlighter.all();"
	
			+"});";
	}
	
	
	/**
	 * Script complete.js for completing word items 
	 * @return
	 */
	private CharSequence getCompleteJs() {
		return "$(function() { "
				+ "var url = \'" +getAutocompleteDataUrl() +"\';"
				+ "var textCompleteWord  = ''; "
		+"$.getJSON(url)"
			+".success(function(data) {" 
					+"$(\'#" +getMarkupId() +"\').textcomplete([ {"
						+"mentions: data,"
						+"match: /\\b(\\w{2,})$/,"
						+"search: function (term, callback) {"
							+"callback($.map(this.mentions, function (mention) {"
								+"return mention.indexOf(term) === 0 ? mention : null;"
							+"}));"
						+"},"
						+"index: 0,"
						+"replace: function (mention) {"
							// Set word to HTML span obj with
							+"textCompleteWord =  '<span class=\"contentEdit\"contenteditable=\"false\">' + mention + ' ' + '</span>' + '&nbsp';" 
							// do not return the <span> obj (not supported by framework for now)
							+"return  '';" 
						+"}"
					+"}"
					+"])"
					+ ".on({  "
						+ "'textComplete:select': function (e, value, strategy) {  "
								// append <span> manually
						 		+ "$('#" +getMarkupId() +"').append(textCompleteWord);"
								// set caret to end position of contenteditable div
								+ "var div = document.getElementById('" +getMarkupId() +"');"
								+ "window.setTimeout(function() {"
							    	+ "var sel, range;"
								    + "if (window.getSelection && document.createRange) {"
								    	+ "range = document.createRange();"
									    + "range.selectNodeContents(div);"
									    + "range.collapse(false);"
									    + "sel = window.getSelection();"
									    + "sel.removeAllRanges();"
									    + "sel.addRange(range);"
								    +"} else if (document.body.createTextRange) {"
									    + "range = document.body.createTextRange();"
									    + "range.moveToElementText(div);"
									    + "range.collapse(false);"
									    + "range.select();"
								    +"}"
								+"}, 1);"
								    
								// close dropdown-menu
							    + "$('.textcomplete-item').parent().hide();" 
						+ "}, "
						+ " 'textComplete:show': function (e) {"
								+ "$('.dropdown-menu').css('left', 'auto');"
						+ "}"
					+ "});"
				+"})"
			+".error(function(jqXHR, textStatus, errorThrown) {"
				+"alert(\"error \" + textStatus);"
				+"alert(\"incoming Text \" + jqXHR.responseText);"
			+"});"	
		+"});";
	}

}
