package eu.uqasar.model.meta;

import javax.persistence.Entity;

/**
 *
 *
 */
@Entity
public class SourceCodeManagementTool extends MetaData {

    public SourceCodeManagementTool() {
        
    }
    
    public SourceCodeManagementTool(String name) {
        super(name);
    }

}
