package eu.uqasar.web.components.tree;

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


import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.components.tree.util.Tree;
import eu.uqasar.web.components.tree.util.TreeExpansion;
import eu.uqasar.web.components.tree.util.TreeProvider;
import eu.uqasar.web.pages.tree.panels.filter.TreeFilterStructure;

public abstract class TreePanel extends Panel {

	private static final long serialVersionUID = -2358690350800694195L;

	@Getter
	private final Tree tree;

	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> editLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newProjectLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newMetricLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newQualityIndicatorLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newQualityObjectiveLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> moveUpLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> moveDownLink;
	private final BootstrapAjaxLink<String> deleteLink;
	private final WebMarkupContainer newLinksContainer;
	
	private TreeProvider provider;

	public TreePanel(String id, TreeFilterStructure filter) {
		super(id);
		add(tree = createTree(filter));

		add(editLink = getEditLink());
		add(moveDownLink = getMoveDownLink());
		add(moveUpLink = getMoveUpLink());
		add(deleteLink = getNewDeleteLink());

		newLinksContainer = new WebMarkupContainer("container.tree.node.new");
		newLinksContainer.setOutputMarkupId(true);
		newLinksContainer.add(newProjectLink = getNewProjectLink());
		newLinksContainer
				.add(newQualityObjectiveLink = getNewQualityObjectiveLink());
		newLinksContainer
				.add(newQualityIndicatorLink = getNewQualityIndicatorLink());
		newLinksContainer.add(newMetricLink = getNewMetricLink());

		add(newLinksContainer);
		setOutputMarkupId(true);
	}
	
	public void updateFilter(TreeFilterStructure filter) {
		provider.setFilter(filter);
	}

	private Tree createTree(TreeFilterStructure filter) {
		provider = new TreeProvider(filter);
		return new Tree("tree", provider, new TreeExpansionModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onNodeClicked(AjaxRequestTarget target,
					IModel<TreeNode> node) {
				updateTreeLinksEnabling(target, node);
				TreePanel.this.onNodeClicked(target, node);
			}
		};
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		updateTreeLinksEnabling(null, tree.getSelectedNode());
	}

	private void updateTreeLinksEnabling(AjaxRequestTarget target,
			IModel<TreeNode> nodeModel) {
		// creating projects is always enabled!
		newProjectLink.setVisible(true);
		if (nodeModel != null && nodeModel.getObject() != null) {
			TreeNode node = nodeModel.getObject();
			if (node instanceof Project) {
				newMetricLink.setVisible(false);
				newQualityIndicatorLink.setVisible(false);
				newQualityObjectiveLink.setVisible(true);
				moveUpLink.setEnabled(false);
				moveDownLink.setEnabled(false);
			} else {
				moveUpLink.setEnabled(true);
				moveDownLink.setEnabled(true);
			}
			if (node instanceof QualityObjective) {
				newMetricLink.setVisible(false);
				newQualityIndicatorLink.setVisible(true);
				newQualityObjectiveLink.setVisible(true);
			}
			if (node instanceof QualityIndicator) {
				newMetricLink.setVisible(true);
				newQualityIndicatorLink.setVisible(true);
				newQualityObjectiveLink.setVisible(true);
			}
			if (node instanceof Metric) {
				newMetricLink.setVisible(true);
				newQualityIndicatorLink.setVisible(true);
				newQualityObjectiveLink.setVisible(true);
			}
			editLink.setEnabled(true);
			deleteLink.setEnabled(true);
		} else {
			editLink.setEnabled(false);
			deleteLink.setEnabled(false);
			moveUpLink.setEnabled(false);
			moveDownLink.setEnabled(false);
			newMetricLink.setVisible(false);
			newQualityIndicatorLink.setVisible(false);
			newQualityObjectiveLink.setVisible(false);
		}

		if (target != null) {
			target.add(newLinksContainer);
			target.add(editLink);
			target.add(deleteLink);
			target.add(moveDownLink);
			target.add(moveUpLink);
		}
	}

	private BootstrapAjaxLink<String> getNewDeleteLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.delete", Buttons.Type.Danger) {
			private static final long serialVersionUID = -3316027169473825950L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this.onDeleteClicked(target, tree.getSelectedNode());
			}
		};
		TooltipBehavior behavior = 
				new TooltipBehavior(
						new StringResourceModel("tooltip.tree.node.delete", 
								this, null));
		link.add(behavior);
		link.setIconType(IconType.trash);
		link.setOutputMarkupId(true);
		return link;
	}


	public void unselectNode() {
		getTree().unselect();
		updateTreeLinksEnabling(null, tree.getSelectedNode());
	}
	
	protected TreeNode getSelectedNode() {
		IModel<TreeNode> nodeModel = tree.getSelectedNode();
		return nodeModel != null && nodeModel.getObject() != null ? nodeModel
				.getObject() : null;
	}

	public abstract void onMoveUpClicked(AjaxRequestTarget target,
			IModel<TreeNode> node);

	public abstract void onMoveDownClicked(AjaxRequestTarget target,
			IModel<TreeNode> node);

	public abstract void onEditClicked(AjaxRequestTarget target,
			IModel<TreeNode> node);
	
	public abstract void onDeleteClicked(AjaxRequestTarget target,
			IModel<TreeNode> node);

	public abstract void onNewClicked(AjaxRequestTarget target,
			IModel<TreeNode> parent, Class<? extends TreeNode> newNodeType);

	public abstract void onNodeClicked(AjaxRequestTarget target,
			IModel<TreeNode> node);

	private class TreeExpansionModel extends
			AbstractReadOnlyModel<Set<TreeNode>> {
		private static final long serialVersionUID = -2882407032073702138L;

		@Override
		public Set<TreeNode> getObject() {
			return TreeExpansion.get();
		}
	}

}
