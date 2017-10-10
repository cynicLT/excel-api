package org.cynic.excel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties
public class RulesConfiguration {
    private List<RuleConfiguration> rules = new ArrayList<>();

    public List<RuleConfiguration> getRules() {
        return rules;
    }

    public void setRules(List<RuleConfiguration> rules) {
        this.rules = rules;
    }
}



