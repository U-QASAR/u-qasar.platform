package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class IssueTrackingTool extends MetaData {

    public IssueTrackingTool() {
    }

    public IssueTrackingTool(String name) {
        super(name);
    }

}
