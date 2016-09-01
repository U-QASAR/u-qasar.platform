package eu.uqasar.web.pages.reporting;

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


import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.web.pages.BasePage;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ReportingPage extends BasePage {

    private static final long serialVersionUID = 5843562261447267086L;
    private String cubeurl=null;
    
    public ReportingPage(PageParameters parameters) {
        super(parameters);        
        final HttpServletRequest request = (HttpServletRequest ) getRequest().getContainerRequest();
        if (request.getParameter("cubeToLoad")!=null)  cubeurl = request.getParameter("cubeToLoad");
        //TODO the URL should be retrieved by the database
        String dynamicHtml = eu.uqasar.util.reporting.Util.getAvailableCubes("http://uqasar.pythonanywhere.com");
        Label selectcubelabel = new Label("selectcube",dynamicHtml );  
        selectcubelabel.setEscapeModelStrings(false);
        add(selectcubelabel);                
    }//EoM

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);        
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/css/reporting/morris.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/css/reporting/bootstrap.min.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/css/reporting/docs.min.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/css/reporting/style.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/css/reporting/datepicker3.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/css/reporting/selectize.default.css")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/css/reporting/query-builder.min.css")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/jquery.min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/bootstrap.min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/docs.min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/jput.min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/raphael-min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/morris.min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/jquery.blockUI.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/xepOnline.jqPlugin.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/moment.min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/bootstrap-datepicker.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/selectize.min.js")));                        
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ReportingPage.class, "assets/js/reporting/query-builder.standalone.min.js"))); 
        
        response.render(OnDomReadyHeaderItem.forScript( eu.uqasar.util.reporting.Util.createExpressionEditor( cubeurl,"http://uqasar.pythonanywhere.com" ) ));
        
        //response.render(JavaScriptHeaderItem.forScript( eu.uqasar.util.reporting.Util.createExpressionEditorInputExperiment() , "dynamicfilters.js") );
        
    }//renderHead
    
}//EoC
