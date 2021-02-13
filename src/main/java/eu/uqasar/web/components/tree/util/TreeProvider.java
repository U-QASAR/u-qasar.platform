package eu.uqasar.web.components.tree.util;

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


import eu.uqasar.model.tree.Project;
import java.util.Iterator;

import lombok.Setter;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.pages.tree.panels.filter.TreeFilterStructure;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.cdi.CdiContainer;

@Setter
public class TreeProvider implements ITreeProvider<TreeNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4350227275218539252L;

	@Inject
	private TreeNodeService treeNodeService;
	
	private TreeFilterStructure filter;
	
	public TreeProvider(TreeFilterStructure filter) {
		CdiContainer.get().getNonContextualManager().inject(this);
		this.filter = filter;
	}

	@Override
	public void detach() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<? extends TreeNode> getChildren(TreeNode node) {
		return node.getChildren().iterator();
	}

	@Override
	public Iterator<? extends TreeNode> getRoots() {
		List<Project> allProjects = treeNodeService.getAllProjectsOfLoggedInUser(filter);
		return allProjects.iterator();
	}

	@Override
	public boolean hasChildren(TreeNode node) {
		return !node.getChildren().isEmpty();
	}

	@Override
	public IModel<TreeNode> model(TreeNode node) {
		return new TreeModel(node, treeNodeService);
	}

	private static class TreeModel extends LoadableDetachableModel<TreeNode> {
		private static final long serialVersionUID = 1L;

		private final Long id;
		private final TreeNodeService service;

		public TreeModel(TreeNode foo, TreeNodeService service) {
			super(foo);
			id = foo.getId();
			this.service = service;
		}

		@Override
		protected TreeNode load() {
			return service.getById(id);
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TreeModel) {
				return ((TreeModel) obj).id.equals(id);
			}
			return false;
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}

}
