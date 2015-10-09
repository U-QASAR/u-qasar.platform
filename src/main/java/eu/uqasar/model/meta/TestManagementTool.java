package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class TestManagementTool extends MetaData {

    public TestManagementTool() {
    }

    public TestManagementTool(String name) {
        super(name);
    }

}
