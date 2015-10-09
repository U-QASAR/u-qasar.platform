package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class ContinuousIntegrationTool extends MetaData {

    public ContinuousIntegrationTool() {
    }

    public ContinuousIntegrationTool(String name) {
        super(name);
    }

}
