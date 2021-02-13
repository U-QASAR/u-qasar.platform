package eu.uqasar.rest;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.uqasar.model.measure.CubesMetricMeasurement;
import eu.uqasar.model.measure.GitlabMetricMeasurement;
import eu.uqasar.model.measure.JenkinsMetricMeasurement;
import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.measure.SonarMetricMeasurement;
import eu.uqasar.model.measure.TestLinkMetricMeasurement;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.qualifier.Conversational;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.dataadapter.CubesDataService;
import eu.uqasar.service.dataadapter.GitlabDataService;
import eu.uqasar.service.dataadapter.JenkinsDataService;
import eu.uqasar.service.dataadapter.JiraDataService;
import eu.uqasar.service.dataadapter.SonarDataService;
import eu.uqasar.service.dataadapter.TestLinkDataService;
import eu.uqasar.service.tree.TreeNodeService;

/**
 * U-QASAR REST API
 */
@Path("/api")
@Conversational
public class UQasarRestService implements Serializable {

	private static final long serialVersionUID = 1466280785737772722L;

	@Inject
	private TreeNodeService treeNodeService;
	@Inject
	private QMTreeNodeService qmTreeNodeService; 
	@Inject
	private AdapterSettingsService adapterSettingsService;
	@Inject 
	private JiraDataService jiraDataService;
	@Inject 
	private TestLinkDataService testlinkDataService;
	@Inject 
	private SonarDataService sonarDataService;
	@Inject 
	private CubesDataService cubesDataService;
	@Inject 
	private GitlabDataService gitlabDataService;
	@Inject 
	private JenkinsDataService jenkinsDataService;
	private final List<TreeNode> allNodes = new LinkedList<>();


	/**
	 * Return all projects as JSON
	 * @return json string
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GET
	@Path("/projects")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getProjects() throws JsonGenerationException, JsonMappingException, IOException {
		List<Project> projects = treeNodeService.getAllProjects();
		String retValue = null;
		if (projects != null) {
			ObjectMapper mapper = new ObjectMapper();
			retValue = mapper.writeValueAsString(projects);
		}
		return retValue;
	}


	/**
	 * Return a JSON formatted project 
	 * @param name : full name of the project 
	 * @return json string 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException  
	 * 
	 * Demo Url :http://localhost:8080/uqasar/rest/api/projects/U-QASAR Platform Development 
	 */
	@GET
	@Path("/projects/{name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getProjectByName(@PathParam("name") String name) throws JsonGenerationException, JsonMappingException, IOException {
		Project project = treeNodeService.getProjectByName(name);
		String value = null;
		if (project != null) {
			ObjectMapper mapper = new ObjectMapper();
			value = mapper.writeValueAsString(project);
		}
		return value;
	}



	/**
	 * Get a specific treenode by its ID
	 * @param nodeId
	 * @return json string
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GET
	@Path("/treenodes/{nodeid}")
	@Produces({MediaType.APPLICATION_JSON})
	public String getTreeNodeById(@PathParam("nodeid") Long nodeId) throws JsonGenerationException, JsonMappingException, IOException {

		TreeNode node = treeNodeService.getById(nodeId);
		String value = null;
		if (node != null) {
			ObjectMapper mapper = new ObjectMapper();
			value = mapper.writeValueAsString(node);
		}

		return value;
	}



	/**
	 * Get all QModels
	 * @return json string
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GET
	@Path("/qmodels")
	@Produces({MediaType.APPLICATION_JSON})
	public String getQModels() throws JsonGenerationException, JsonMappingException, IOException {

		List<QModel> qmodels = qmTreeNodeService.getAllQModels();
		String retValue = null;
		if (qmodels != null) {
			ObjectMapper mapper = new ObjectMapper();
			retValue = mapper.writeValueAsString(qmodels);
		}

		return retValue;    	
	}


	/**
	 * Return a JSON formatted QModel 
	 * @param name : full name of the QModel 
	 * @return json string 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * Demo Url : http://localhost:8080/uqasar/rest/api/qmodels/Quality Model A 
	 */
	@GET
	@Path("/qmodels/{name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getQModelByName(@PathParam("name") String name) throws JsonGenerationException, JsonMappingException, IOException {

		QMTreeNode qMTreeNode = qmTreeNodeService.getByName(QMTreeNode.class, name);
		String value = null; 
		if (qMTreeNode != null) {
			ObjectMapper mapper = new ObjectMapper();
			value = mapper.writeValueAsString(qMTreeNode);
		}

		return value;
	}

	
	
	/**
	 * Get a measurements belonging to a specific adapter
	 * @param adapterId Id of the adapter whose data shall be acquired
	 * @return json string
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GET
	@Path("/measurements/{adapterid}")
	@Produces({MediaType.APPLICATION_JSON})
	public String getAdapterData(@PathParam("adapterid") Long adapterId) throws JsonGenerationException, JsonMappingException, IOException {
		
		String value = null;
		AdapterSettings settings = adapterSettingsService.getById(adapterId);
		if (settings != null) {
			MetricSource metricSource = settings.getMetricSource();

			// Handle the different cases of adapter types
			if (metricSource != null && metricSource == MetricSource.IssueTracker) {
				List<JiraMetricMeasurement> res = jiraDataService.getAllByAdapter(settings);
				if (res != null) {
					ObjectMapper mapper = new ObjectMapper();
					value = mapper.writeValueAsString(res);
					return value;
				}			
			} else if (metricSource != null && metricSource == MetricSource.TestingFramework) {
				List<TestLinkMetricMeasurement> res = testlinkDataService.getAllByAdapter(settings);
				if (res != null) {
					ObjectMapper mapper = new ObjectMapper();
					value = mapper.writeValueAsString(res);
					return value; 
				}
			} else if (metricSource != null && metricSource == MetricSource.StaticAnalysis) {
				List<SonarMetricMeasurement> res = sonarDataService.getAllByAdapter(settings);
				if (res != null) {
					ObjectMapper mapper = new ObjectMapper();
					value = mapper.writeValueAsString(res);
					return value; 
				}
			} else if (metricSource != null && metricSource == MetricSource.CubeAnalysis){
				List<CubesMetricMeasurement> res = cubesDataService.getAllByAdapter(settings);
				if (res != null) {
					ObjectMapper mapper = new ObjectMapper();
					value = mapper.writeValueAsString(res);
					return value; 
				}
			} else if (metricSource != null && metricSource == MetricSource.VersionControl) {
				List<GitlabMetricMeasurement> res = gitlabDataService.getAllByAdapter(settings);
				if (res != null) {
					ObjectMapper mapper = new ObjectMapper();
					value = mapper.writeValueAsString(res);
					return value; 				
				}
			} else if (metricSource != null && metricSource == MetricSource.ContinuousIntegration) {
				List<JenkinsMetricMeasurement> res = jenkinsDataService.getAllByAdapter(settings);
				if (res != null) {
					ObjectMapper mapper = new ObjectMapper();
					value = mapper.writeValueAsString(res);
					return value;
				}
			}
		}
		
		return value;
	}
	
	
	
	/**
	 * Return historical values for a treenode as JSON formatted string 
	 * @return json string
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * Demo Url : http://localhost:8080/uqasar/rest/api/historicvalues?projectname=U-QASAR Platform Development&treenodeid=123 
	 */
	@GET
	@Path("/historicvalues")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<String> getHistValueOFTreeNode(@QueryParam("projectname") String projectName,
			@QueryParam("treenodeid") Long treeNodeId) throws JsonGenerationException, JsonMappingException, IOException {

		List<String> histValues = new LinkedList<>();
		Project project = treeNodeService.getProjectByName(projectName);

		if (project != null) {
			ObjectMapper mapper = new ObjectMapper();

			collectChildrenNodes(project);

			for (TreeNode node : allNodes) {

				if (treeNodeId.compareTo(node.getId()) == 0) {
					if (node instanceof Metric) {
						for (HistoricValuesBaseIndicator hv : ((Metric) node).getHistoricValues())

							histValues.add(mapper.writeValueAsString(hv));
					} else if (node instanceof QualityIndicator) {
						for (HistoricValuesBaseIndicator hv : ((QualityIndicator) node).getHistoricValues())

							histValues.add(mapper.writeValueAsString(hv));
					} else if (node instanceof QualityObjective) {
						for (HistoricValuesBaseIndicator hv : ((QualityObjective) node).getHistoricValues())

							histValues.add(mapper.writeValueAsString(hv));
					}

				}

			}
		}

		return histValues;
	}
	

	/**
	 * Store a QA project
	 * @return
	 */
	@POST
	@Path("/projects")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addQAProject(InputStream input) {

		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			System.out.println("Data Received: " + sb.toString());

			ObjectMapper mapper = new ObjectMapper();
			Project p = mapper.readValue(sb.toString(), Project.class);
            Project proj = (Project) treeNodeService.create(p);
            System.out.println("Project " +proj.toString() + " created.");
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		// return HTTP response 200 in case of success
		return Response.status(200).entity(sb.toString()).build();
	}

	
	/**
	 * Store a QModel
	 * @return
	 */
	@POST 
	@Path("/qmodels")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addQModel(InputStream input) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			System.out.println("Data Received: " + sb.toString());

			ObjectMapper mapper = new ObjectMapper();
			QModel qm = mapper.readValue(sb.toString(), QModel.class);
            QModel qualityModel = (QModel) qmTreeNodeService.create(qm);
            System.out.println("Quality Model " +qualityModel.toString() + " created.");
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		// return HTTP response 200 in case of success
		return Response.status(200).entity(sb.toString()).build();
	}
	


	/**
	 * 
	 * @param node
	 */
	private void collectChildrenNodes(TreeNode node){

		if(node != null){
			for(TreeNode n : node.getChildren()){
				allNodes.add(n);

				//call this method recursively
				collectChildrenNodes(n);
			} 
		}
	}
}
