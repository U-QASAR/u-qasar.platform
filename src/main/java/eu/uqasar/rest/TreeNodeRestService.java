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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.solder.logging.Logger;

import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.qualifier.Conversational;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

/**
 * Simple RESTful service for fetching TreeNode items' names used internally 
 * by the formula editor
 *
 */
@Path("/treenodes")
@Conversational
public class TreeNodeRestService implements Serializable {

	private static final long serialVersionUID = 1466280785737772722L;

	@Inject
	private Logger logger;

	@Inject
	private TreeNodeService treeNodeService;

	/**
	 * Return a JSON formatted list of TreeNode items' names of a parent node 
	 * @param parentId ID of the parent whose children are to be fetched
	 * @return
	 */
	@GET
	@Path("/{parentId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String[] getAllTreeNodeChildrenNames(@PathParam("parentId") Long parentId) {
		List<String> nodes = new ArrayList<>();

		TreeNode parentNode = treeNodeService.getById(parentId);
		for (TreeNode node : parentNode.getChildren()) {
			String str = node.getName();
			// Replace the whitespace with a separator and escape the string
			String processed = UQasarUtil.sanitizeName(str);
			logger.debug(processed);
			nodes.add(processed);
		}
		// return as array
		return nodes.toArray(new String[nodes.size()]);
	}
}
