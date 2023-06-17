package br.com.stenox.cxc.rule;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Rule {

    private final String text;
    private final String ruleType;

    @Setter
    private boolean active;

    public Rule(String text, String ruleType){
        this.text = text;
        this.ruleType = ruleType;
    }
}
