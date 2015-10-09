/**
 * 
 */
package eu.uqasar.web.pages.tree.snapshot;

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
	
	private Snapshot snap;

	private SnapshotViewPanel panel;

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
