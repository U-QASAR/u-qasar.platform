package eu.uqasar.web.pages.qmtree.qmodels.panels;

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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.Session;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.solder.logging.Logger;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.qmtree.QModelStatus;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.panels.QMBaseTreePanel;
import eu.uqasar.web.pages.qmtree.qmodels.QModelEditPage;

public class QModelViewPanel extends QMBaseTreePanel<QModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1088435851924101698L;
	
	private static final Logger logger = Logger.getLogger(QModelViewPanel.class);


	public QModelViewPanel(String id, IModel<QModel> model) {
		super(id, model);
		
		final QModel qmodel = model.getObject();
		logger.info("QModelViewPanel::QModelViewPanel" + qmodel.getName());
		
		add(new Label("name", new PropertyModel<>(qmodel, "name")));
		add(new Label("icon").add(new CssClassNameAppender(qmodel
				.getIconType().cssClassName())));
		// TODO only show if authorized to edit
		BookmarkablePageLink<QModelEditPage> editLink = new BookmarkablePageLink<>(
				"link.edit", QModelEditPage.class,
				QMBaseTreePage.forQModel(qmodel));
		add(editLink);

		add(new Label("nodeKey", new PropertyModel<>(model, "nodeKey")));
		add(new Label("edition", new PropertyModel<>(qmodel, "edition")));
		
		add(new Label("company", new PropertyModel<>(qmodel, "company")));
		
		SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(
				DateFormat.MEDIUM, Session.get().getLocale());
		PatternDateConverter pdc = new PatternDateConverter(df.toPattern(),
				true);
		add(new DateLabel("updateDate", new PropertyModel<Date>(model,
		"updateDate"), pdc));

		add(new Label("description", new PropertyModel<String>(model,
				"description")).setEscapeModelStrings(false));

		//isActive
		Image img = new Image("isActive") {
		    @Override
		    protected void onComponentTag(ComponentTag tag) {
		        super.onComponentTag(tag);
		        if (qmodel.getIsActive().equals(QModelStatus.Active)){
		        	tag.getAttributes().put("src",  "/uqasar/assets/img/imgv.gif");
			        tag.getAttributes().put("alt", "active");
		        } else {
		        	tag.getAttributes().put("src",  "/uqasar/assets/img/imgx.gif");
			        tag.getAttributes().put("alt", "inactive");
			    }
		        tag.getAttributes().put("style", "max-width:10%");
		    }
		};
		
		
		add(img);

	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

}
