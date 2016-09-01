/*
 */
package eu.uqasar.web.pages.admin.rdf;

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

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import com.hp.hpl.jena.ontology.OntModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.pages.admin.AdminBasePage;


/**
 * This class provides functionalities for writing entities in memory (and DB) 
 * to RDF files and vice versa. Essentially this functionality should occur 
 * in the background so that every time the model is changed, the changes are 
 * written to the RDF model file as well. 
 *
 */
public class RDFManagementPage extends AdminBasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;
	
	private final TextArea<String> sparqlEditor;
	private final Label queryResultLabel;

	public RDFManagementPage(PageParameters pageParameters) {
		super(pageParameters);

		add(newWriteModelButton());
		add(newReadModelButton());
		add(newOutputModelButton());
		
		sparqlEditor = new TextArea<>("sparqleditor", Model.of(""));
		sparqlEditor.setModelObject("SELECT ?s WHERE { ?s a <http://eu.uqasar.model.user/User> }");
		add(sparqlEditor);
		add(newExecSparqlQueryButton());		
		
		queryResultLabel = new Label("queryResultLabel", Model.of(""));
		queryResultLabel.setOutputMarkupId(true);
		add(queryResultLabel);
	}

	
	/**
	 * Updates the RDF model file
	 * @return
	 */
	private BootstrapAjaxLink<String> newWriteModelButton() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.write.model", 
				new StringResourceModel(
						"button.write.model", 
						this, null), Buttons.Type.Primary) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				UQasarUtil.writeSemanticModelFiles();
				
			}
		};
		
		link.setOutputMarkupId(true);
		return link;
	}
	
	/**
	 * Read the model from a file
	 * @return
	 */
	private BootstrapAjaxLink<String> newReadModelButton() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.read.model", 
				new StringResourceModel(
						"button.read.model", 
						this, null), Buttons.Type.Info) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				UQasarUtil.readSemanticModelFiles();			
			}
		};
		
		link.setOutputMarkupId(true);
		return link;
	}


	/**
	 * 
	 * @return
	 */
	private BootstrapAjaxLink<String> newOutputModelButton() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.output.model", 
				new StringResourceModel(
						"button.output.model", 
						this, null), Buttons.Type.Default) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				OntModel model = (OntModel) UQasarUtil.getUqModel();
				if (model == null) {
					UQasarUtil.readSemanticModelFiles();
				}
				// Test output to standard output
				RDFDataMgr.write(System.out, model, RDFFormat.RDFXML_PRETTY);
			}
		};
		
		link.setOutputMarkupId(true);
		return link;
	}
	
	
	private BootstrapAjaxLink<String> newExecSparqlQueryButton() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.execSparql", 
				new StringResourceModel(
						"button.execSparql", 
						this, null), Buttons.Type.Primary) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				String query = (String) sparqlEditor.getDefaultModelObject();
				System.out.println("Executing the SparQL Query: " +query);
				String res = UQasarUtil.execSparQLQuery(query);
				queryResultLabel.setDefaultModelObject(res);
				target.add(queryResultLabel);
			}
		};
		
		link.setOutputMarkupId(true);
		return link;
	}

}
