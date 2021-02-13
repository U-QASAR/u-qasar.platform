package eu.uqasar.web.components.qmtree.util;

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

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree.State;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.settings.qmodel.QModelSettings;
import eu.uqasar.service.settings.QModelSettingsService;

public class QMTreeFolder extends Folder<QMTreeNode> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5703638374309845463L;

	private final AbstractTree<QMTreeNode> tree;

	@Inject
	private QModelSettingsService qmService;
	
	private QModelSettings settings;
	
	public QMTreeFolder(String id, AbstractTree<QMTreeNode> tree,
			IModel<QMTreeNode> model) {
		super(id, tree, model);
		this.tree = tree;
	}

	@Override
	protected Component newLabelComponent(String id, final IModel<QMTreeNode> model) {
		WebMarkupContainer labelContainer = new WebMarkupContainer(id);
		settings = qmService.get(new QModelSettings());
		
		Label idLabel;
		Label entityLabel;
		
		int qoIndex = 0;
		int qiIndex = 0;
		int qmetIndex = 0;
		
		if (model.getObject() instanceof QModel) {
			entityLabel = new Label("entityLabel", "");
			idLabel = new Label("idLabel", " ");
		} else if (model.getObject() instanceof QMQualityObjective) {
			entityLabel = new Label("entityLabel",  settings.getHigh() + " - ");
			qoIndex = (model.getObject()).getParent().getChildren().indexOf(model.getObject())+1;
			idLabel = new Label("idLabel", qoIndex + " ");
		} else if (model.getObject() instanceof QMQualityIndicator) {
			entityLabel = new Label("entityLabel", settings.getMedium() +" - ");
			qoIndex = (model.getObject()).getParent().getSiblings().indexOf(model.getObject().getParent())+1;
			qiIndex = (model.getObject()).getParent().getChildren().indexOf(model.getObject())+1;
			idLabel = new Label("idLabel", qoIndex + " "+ qiIndex + " ");
		} else {
			entityLabel = new Label("entityLabel", settings.getLow() +" - ");
			qoIndex = (model.getObject()).getParent().getParent().getSiblings().indexOf(model.getObject().getParent().getParent())+1;
			qiIndex = (model.getObject()).getParent().getSiblings().indexOf(model.getObject().getParent())+1;
			qmetIndex = (model.getObject()).getParent().getChildren().indexOf(model.getObject())+1;
			idLabel = new Label("idLabel", qoIndex + " "+ qiIndex + " "+ qmetIndex + " ");
		}
		
		Label label = new Label("labelText", newLabelModel(model));
		Label icon = (Label) new Label("icon").add(new AttributeAppender(
				"class", model.getObject().getIconType().cssClassName()));

		//alarm when the node is incomplete in quality model
		Image incomplete = new Image("incomplete") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				if (!model.getObject().isCompleted()){
					tag.getAttributes().put("src",  "/uqasar/assets/img/exclamation.jpg");
					tag.getAttributes().put("alt", "incomplete");
					tag.getAttributes().put("title", "incomplete");
					tag.getAttributes().put("style", "max-width:3%");
				} else {
					tag.getAttributes().put("src",  "/uqasar/assets/img/noexclamation.jpg");
					tag.getAttributes().put("display", "none");
					tag.getAttributes().put("style", "max-width:0%");
				}
				

			}
		};
				
		
		labelContainer.add(incomplete);
		labelContainer.add(label);
		labelContainer.add(icon);
		labelContainer.add(idLabel);
		labelContainer.add(entityLabel);
		
		return labelContainer;
	}

	@Override
	protected boolean isClickable() {
		return true;
	}

	/**
	 * Delegates to others methods depending wether the given model is a folder,
	 * expanded, collapsed or selected.
	 * 
	 * @see ITreeProvider#hasChildren(Object)
	 * @see AbstractTree#getState(Object)
	 * @see #isSelected()
	 * @see #getOpenStyleClass()
	 * @see #getClosedStyleClass()
	 * @see #getOtherStyleClass(Object)
	 * @see #getSelectedStyleClass()
	 */
	@Override
	protected String getStyleClass() {
		QMTreeNode t = getModelObject();

		String styleClass = "";

		if (t instanceof QMQualityObjective) {
			styleClass = getQualityObjectiveStyle((QMQualityObjective) t);
		} else if (t instanceof QMQualityIndicator) {
			styleClass = getQualityIndicatorStyle((QMQualityIndicator) t);
		} else if (t instanceof QMMetric) {
			styleClass = getMetricStyle((QMMetric) t);
		}

		if (isSelected()) {
			styleClass += " " + getSelectedStyleClass();
		}
		styleClass += " " + t.getClass().getSimpleName().toLowerCase();
		return styleClass;
	}

	protected String getStyle(QMTreeNode node) {
		if (tree.getProvider().hasChildren(node)) {
			if (tree.getState(node) == State.EXPANDED) {
				return getOpenStyleClass();
			} else {
				return getClosedStyleClass();
			}
		} else {
			return getOtherStyleClass(node);
		}
	}

	private String getMetricStyle(QMMetric node) {
		return getOtherStyleClass(node);
	}

	private String getQualityIndicatorStyle(QMQualityIndicator node) {
		if (tree.getState(node) == State.EXPANDED) {
			return getOpenStyleClass();
		} else {
			return getClosedStyleClass();
		}
	}

	private String getQualityObjectiveStyle(QMQualityObjective node) {
		if (tree.getState(node) == State.EXPANDED) {
			return getOpenStyleClass();
		} else {
			return getClosedStyleClass();
		}
	}

}
