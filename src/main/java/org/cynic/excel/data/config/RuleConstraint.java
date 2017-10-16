package org.cynic.excel.data.config;

import java.util.ArrayList;
import java.util.List;

public class RuleConstraint {
    private List<DataItem> data = new ArrayList<>();
    private String expression;

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<DataItem> getData() {
        return data;
    }

    public String getExpression() {
        return expression;
    }
}
