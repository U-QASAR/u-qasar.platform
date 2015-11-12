package eu.uqasar.web.dashboard.widget.reportingwidget;

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
