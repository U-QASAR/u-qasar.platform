package eu.uqasar.web.components.tree;

import java.util.Set;

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

	private final Tree tree;

	private final BootstrapAjaxLink<String> editLink;
	private final BootstrapAjaxLink<String> newProjectLink;
	private final BootstrapAjaxLink<String> newMetricLink;
	private final BootstrapAjaxLink<String> newQualityIndicatorLink;
	private final BootstrapAjaxLink<String> newQualityObjectiveLink;
	private final BootstrapAjaxLink<String> moveUpLink;
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

	private BootstrapAjaxLink<String> getEditLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.edit", Buttons.Type.Success) {
			private static final long serialVersionUID = 4604465575758878183L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this.onEditClicked(target, tree.getSelectedNode());
			}
		};
		TooltipBehavior behavior = 
				new TooltipBehavior(
						new StringResourceModel("tooltip.tree.node.edit", 
								this, null));
		link.add(behavior);
		link.setIconType(IconType.pencil);
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getMoveDownLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.move.down", Buttons.Type.Default) {
			private static final long serialVersionUID = -5402117100634166400L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this
						.onMoveDownClicked(target, tree.getSelectedNode());
			}
		};
		TooltipBehavior behavior = 
				new TooltipBehavior(
						new StringResourceModel("tooltip.tree.node.move.down", 
								this, null));
		link.add(behavior);
		link.setIconType(new IconType("chevron-sign-down"));
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getMoveUpLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.move.up", Buttons.Type.Default) {
			private static final long serialVersionUID = 339875927979390279L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this.onMoveUpClicked(target, tree.getSelectedNode());
			}
		};
		
		TooltipBehavior behavior = 
				new TooltipBehavior(
						new StringResourceModel("tooltip.tree.node.move.up", 
								this, null));
		link.add(behavior);
		link.setIconType(new IconType("chevron-sign-up"));
		link.setOutputMarkupId(true);
		return link;
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

	private BootstrapAjaxLink<String> getNewProjectLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.project", new StringResourceModel(
						"button.tree.new.project", this, null),
				Buttons.Type.Link) {
			private static final long serialVersionUID = -3891261827414844194L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						Project.class);
			}
		};
		link.setIconType(new IconType("sitemap"));
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getNewQualityObjectiveLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.objective", new StringResourceModel(
						"button.tree.new.objective", this, null),
				Buttons.Type.Link) {
			private static final long serialVersionUID = -3891261827414844194L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						QualityObjective.class);
			}
		};
		link.setIconType(IconType.tasks);
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getNewQualityIndicatorLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.indicator", new StringResourceModel(
						"button.tree.new.indicator", this, null),
				Buttons.Type.Link) {
			private static final long serialVersionUID = -3891261827414844194L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						QualityIndicator.class);
			}
		};
		link.setIconType(new IconType("dashboard"));
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getNewMetricLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.metric", new StringResourceModel(
						"button.tree.new.metric", this, null),
				Buttons.Type.Link) {
			private static final long serialVersionUID = -3891261827414844194L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						Metric.class);
			}
		};
		link.setIconType(IconType.signal);
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

	public Tree getTree() {
		return tree;
	}

	private class TreeExpansionModel extends
			AbstractReadOnlyModel<Set<TreeNode>> {
		private static final long serialVersionUID = -2882407032073702138L;

		@Override
		public Set<TreeNode> getObject() {
			return TreeExpansion.get();
		}
	}

}
