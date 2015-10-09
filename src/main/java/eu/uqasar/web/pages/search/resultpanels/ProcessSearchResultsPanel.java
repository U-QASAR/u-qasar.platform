package eu.uqasar.web.pages.search.resultpanels;

import eu.uqasar.model.process.Process;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.ProcessService;
import eu.uqasar.web.pages.processes.ProcessAddEditPage;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class ProcessSearchResultsPanel extends AbstractSearchResultsPanel<Process> {

    @Inject
    ProcessService service;

    public ProcessSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, Process.class);
    }

    @Override
    protected void populateDataViewItem(Item<Process> item) {
        final Process entity = item.getModelObject();
        BookmarkablePageLink<Process> link = new BookmarkablePageLink<>("link",
                ProcessAddEditPage.class, ProcessAddEditPage.linkToEdit(entity));
        link.add(new Label("name", Model.of(entity.getName())).
                setRenderBodyOnly(true));
        item.add(link);

        item.add(new Label("description", Model.of(entity.getDescription())));
        item.add(new Label("startdate", Model.of(entity.getStartDate())));
        item.add(new Label("enddate", Model.of(entity.getEndDate())));

    }

    @Override
    protected <S extends AbstractService<Process>> S getService() {
        return (S) service;
    }

}
