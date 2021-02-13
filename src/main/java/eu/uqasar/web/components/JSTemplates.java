package eu.uqasar.web.components;

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

import java.io.Serializable;

/**
 *
 * 
 */
public class JSTemplates implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6666017216728726827L;

	/**
	 * JS and CSS file paths
	 */
	// some general stuff
	public static final String MAIN_JS = "assets/js/main.js";
	// jquery table sorter
	public static final String JQUERY_TABLESORT_JS = "assets/js/jquery.tablesorter.min.js";
	// jquery ui with ALL plugins
	public static final String JQUERY_UI_JS = "assets/js/jquery-ui-1.9.1.custom.min.js";
	
	/**
	 * JS IDs
	 */
	// some general stuff
	public static final String MAIN_JS_REF_ID = "js_main";
	// jquery table sorter
	public static final String JQUERY_TABLESORT_JS_REF_ID = "js_tablesort";
	// jquery ui
	public static final String JQUERY_UI_JS_REF_ID = "js_ui";

	/**
	 * JS templates
	 */
	public static final String LOAD_TABLE_SORTER = "activateTableSort('%s');";
	public static final String INIT_COLLAPSE = "initCollapse();";
	public static final String TOGGLE_COLLAPSE = "toggleCollapse('%s');";
	public static final String CLOSE_COLLAPSE = "closeCollapse('%s');";
	public static final String SHOW_COLLAPSE = "showCollapse('%s');";
	public static final String PREPEND_ELEM_TEMPLATE = "notify|prependElemToContainer('%s', '%s', '%s');";
	public static final String APPEND_ELEM_TEMPLATE = "notify|appendElemToContainer('%s', '%s', '%s');";
	public static final String FADE_IN_ELEM_TEMPLATE = "fadeInElem('%s');";
	public static final String FADE_OUT_ELEM_TEMPLATE = "notify|fadeOutElem('%s', notify);";
	public static final String FADE_OUT_AND_REMOVE_ELEM_TEMPLATE = "notify|fadeOutAndRemoveElem('%s', notify);";
	public static final String SLIDE_UP_ELEM_TEMPLATE = "notify|slideUp('%s', notify);";
	public static final String SLIDE_DOWN_ELEM_TEMPLATE = "notify|slideDown('%s', notify);";

}
