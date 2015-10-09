package eu.uqasar.web.dashboard.widget.reportingwidget;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.Namable;

public class Rule extends AbstractEntity implements Namable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String selectedAdditionalRule;

    private String selectedRule;

    public Rule(String selectedRule, String selectedAdditionalRule) {
        this.selectedRule = selectedRule;
        this.selectedAdditionalRule = selectedAdditionalRule;
    }

    @Override
    public String getUniqueName() {
        // TODO Auto-generated method stub
        return "TestClass";
    }

    public String getSelectedRule() {
        return selectedRule;
    }

    public void setSelectedRule(String selectedRule) {
        this.selectedRule = selectedRule;
    }

    public String getSelectedAdditionalRule() {
        return selectedAdditionalRule;
    }

    public void setSelectedAdditionalRule(String selectedAdditionalRule) {
        this.selectedAdditionalRule = selectedAdditionalRule;
    }

}
