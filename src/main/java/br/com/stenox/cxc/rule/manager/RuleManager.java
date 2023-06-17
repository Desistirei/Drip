package br.com.stenox.cxc.rule.manager;

import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.rule.Rule;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleManager {

    @Getter
    private final Map<Integer, Rule> rules;
    private int i = 1;

    private final Main plugin;

    public RuleManager(Main plugin){
        this.plugin = plugin;

        rules = Maps.newHashMap();
    }

    public void addRule(Rule rule){
        rules.put(i, rule);
        i++;
    }

    public void removeRule(int i){
        rules.remove(i);
    }

    public Rule getRule(int i){
        return rules.get(i);
    }

    public void loadRules(String ruleType){
        if (plugin.getRulesConfig().contains(ruleType)){
            for (String ruleText : plugin.getRulesConfig().getStringList(ruleType)) {
                Rule rule = new Rule(ruleText, ruleType);
                rule.setActive(true);

                addRule(rule);
            }
        }
    }

    public void saveRules() {
        Map<String, List<String>> rules = Maps.newConcurrentMap();

        for (Map.Entry<Integer, Rule> entry : this.rules.entrySet()) {
            List<String> current = rules.containsKey(entry.getValue().getRuleType()) ? rules.get(entry.getValue().getRuleType()) : new ArrayList<>();
            current.add(entry.getValue().getText());

            rules.put(entry.getValue().getRuleType(), current);
        }

        rules.forEach((key, value) -> {
            plugin.getRulesConfig().set(key, value);
        });

        try {
            plugin.getRulesConfig().save(new File(Main.getInstance().getDataFolder(), "rules.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
