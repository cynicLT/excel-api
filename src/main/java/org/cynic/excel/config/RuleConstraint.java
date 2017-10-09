package org.cynic.excel.config;

import java.util.ArrayList;
import java.util.List;

public class RuleConstraint {
    private List<DataItem> data = new ArrayList<>();
    private String expression;

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
