package eu.uqasar.web.pages.tree.snapshot;

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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.historic.Snapshot;
import eu.uqasar.service.SnapshotService;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.snapshot.panel.SnapshotViewPanel;

/**
 *
 *
 */
public class SnapshotPage extends BasePage {
	private static final long serialVersionUID = 1404486356253398289L;

	@Inject
	SnapshotService snapshotService;
	
	private final Snapshot snap;

	private final SnapshotViewPanel panel;

	public SnapshotPage(PageParameters parameters) {
		super(parameters);

		if (parameters != null && parameters.get("snap-id") != null) {
			snap = snapshotService.getById(parameters.get("snap-id").toOptionalLong());
		} else{
			snap = new Snapshot();
		}
		
		// SubHeader with the Snapshot tittle 
		add(new Label("snapshotData", snap));
		
		// Adds the snapshot tree view panel 
		panel = new SnapshotViewPanel("subsetPanel", Model.of(snap));

		add(panel);

	}

}
