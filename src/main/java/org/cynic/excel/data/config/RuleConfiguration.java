package org.cynic.excel.data.config;

import java.util.List;

public class RuleConfiguration {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setConstraint(RuleConstraint constraint) {
        this.constraint = constraint;
    }

    public void setValues(List<RuleValues> values) {
        this.values = values;
    }

    private RuleConstraint constraint;
    private List<RuleValues> values;

    public String getName() {
        return name;
    }

    public RuleConstraint getConstraint() {
        return constraint;
    }

    public List<RuleValues> getValues() {
        return values;
    }
}
