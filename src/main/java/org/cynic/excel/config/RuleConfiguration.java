package org.cynic.excel.config;

import java.util.List;

public class RuleConfiguration {
    private String name;
    private RuleConstraint constraint;
    private List<RuleValues> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(RuleConstraint constraint) {
        this.constraint = constraint;
    }

    public List<RuleValues> getValues() {
        return values;
    }

    public void setValues(List<RuleValues> values) {
        this.values = values;
    }
}
