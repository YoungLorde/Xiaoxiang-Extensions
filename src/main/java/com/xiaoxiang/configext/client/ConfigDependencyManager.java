package com.xiaoxiang.configext.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages config dependencies — rules that show/hide or enable/disable
 * certain config entries based on the values of other configs.
 *
 * Rule format: {"if":"pathA==value","then":"pathB","action":"show"}
 * Actions: "show" (show only when condition met), "hide" (hide when condition met),
 *          "enable" (enable editing when condition met), "disable" (disable when condition met)
 */
public class ConfigDependencyManager {

    public enum Action { SHOW, HIDE, ENABLE, DISABLE }

    public static class DependencyRule {
        public String conditionPath;
        public String conditionValue;
        public String operator; // == or !=
        public String targetPath;
        public Action action;

        public DependencyRule(String conditionPath, String conditionValue, String operator,
                              String targetPath, Action action) {
            this.conditionPath = conditionPath;
            this.conditionValue = conditionValue;
            this.operator = operator;
            this.targetPath = targetPath;
            this.action = action;
        }
    }

    private static final List<DependencyRule> rules = new ArrayList<>();

    /** Load dependency rules from config string. */
    public static void loadFromConfig() {
        rules.clear();
        try {
            String json = com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_CONFIG_DEPENDENCIES.get();
            if (json != null && !json.isEmpty() && !json.equals("[]")) {
                parseRules(json);
            }
        } catch (Exception e) {
            System.err.println("[ConfigExt] Failed to parse config dependencies: " + e.getMessage());
        }
    }

    /** Save rules to config. */
    public static void saveToConfig() {
        try {
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_CONFIG_DEPENDENCIES.set(rulesToJson());
        } catch (Exception e) { /* ignore */ }
    }

    /** Add a dependency rule. */
    public static void addRule(String conditionPath, String conditionValue, String operator,
                               String targetPath, Action action) {
        rules.add(new DependencyRule(conditionPath, conditionValue, operator, targetPath, action));
        saveToConfig();
    }

    /** Remove all rules targeting a specific config path. */
    public static void removeRulesForTarget(String targetPath) {
        rules.removeIf(r -> r.targetPath.equals(targetPath));
        saveToConfig();
    }

    /** Get all rules. */
    public static List<DependencyRule> getRules() {
        return rules;
    }

    /**
     * Check if a config entry should be visible based on dependency rules.
     * @param configPath The config path to check
     * @return true if the entry should be visible, false if a SHOW rule's condition is not met
     *         or a HIDE rule's condition is met
     */
    public static boolean shouldBeVisible(String configPath) {
        for (DependencyRule rule : rules) {
            if (!rule.targetPath.equals(configPath)) continue;
            boolean conditionMet = evaluateCondition(rule);
            if (rule.action == Action.SHOW && !conditionMet) return false;
            if (rule.action == Action.HIDE && conditionMet) return false;
        }
        return true;
    }

    /**
     * Check if a config entry should be editable based on dependency rules.
     * @param configPath The config path to check
     * @return true if editable, false if a DISABLE rule's condition is met or ENABLE rule's condition is not met
     */
    public static boolean shouldBeEditable(String configPath) {
        for (DependencyRule rule : rules) {
            if (!rule.targetPath.equals(configPath)) continue;
            boolean conditionMet = evaluateCondition(rule);
            if (rule.action == Action.ENABLE && !conditionMet) return false;
            if (rule.action == Action.DISABLE && conditionMet) return false;
        }
        return true;
    }

    /** Get the reason a config is hidden/disabled (for tooltip display). */
    public static String getDependencyReason(String configPath) {
        for (DependencyRule rule : rules) {
            if (!rule.targetPath.equals(configPath)) continue;
            boolean conditionMet = evaluateCondition(rule);
            if (rule.action == Action.SHOW && !conditionMet) {
                return "Hidden: requires " + rule.conditionPath + " " + rule.operator + " " + rule.conditionValue;
            }
            if (rule.action == Action.HIDE && conditionMet) {
                return "Hidden when " + rule.conditionPath + " " + rule.operator + " " + rule.conditionValue;
            }
            if (rule.action == Action.ENABLE && !conditionMet) {
                return "Locked: requires " + rule.conditionPath + " " + rule.operator + " " + rule.conditionValue;
            }
            if (rule.action == Action.DISABLE && conditionMet) {
                return "Locked when " + rule.conditionPath + " " + rule.operator + " " + rule.conditionValue;
            }
        }
        return null;
    }

    private static boolean evaluateCondition(DependencyRule rule) {
        String actualValue = ConfigValueAccessor.getValueString(rule.conditionPath);
        if (rule.operator.equals("==")) {
            return actualValue != null && actualValue.equals(rule.conditionValue);
        } else if (rule.operator.equals("!=")) {
            return actualValue != null && !actualValue.equals(rule.conditionValue);
        }
        return false;
    }

    // ── JSON parsing/serialization ──

    private static String rulesToJson() {
        if (rules.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rules.size(); i++) {
            if (i > 0) sb.append(",");
            DependencyRule r = rules.get(i);
            sb.append("{\"if\":\"").append(r.conditionPath).append(r.operator).append(r.conditionValue)
              .append("\",\"then\":\"").append(r.targetPath)
              .append("\",\"action\":\"").append(r.action.name().toLowerCase()).append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void parseRules(String json) {
        int i = 1; // skip [
        while (i < json.length() && json.charAt(i) != ']') {
            i = skipWhitespace(json, i);
            if (i >= json.length() || json.charAt(i) != '{') break;
            i++; // skip {
            String condition = "";
            String target = "";
            String action = "show";
            while (i < json.length() && json.charAt(i) != '}') {
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) == '}') break;
                if (json.charAt(i) != '"') break;
                i++;
                int keyStart = i;
                while (i < json.length() && json.charAt(i) != '"') i++;
                String key = json.substring(keyStart, i);
                i++; // skip closing quote
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) != ':') break;
                i++; // skip colon
                i = skipWhitespace(json, i);
                if (json.charAt(i) != '"') break;
                i++;
                int valStart = i;
                while (i < json.length() && json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\') i++;
                    i++;
                }
                String val = json.substring(valStart, i);
                i++; // skip closing quote
                if (key.equals("if")) condition = val;
                else if (key.equals("then")) target = val;
                else if (key.equals("action")) action = val;
                i = skipWhitespace(json, i);
                if (i < json.length() && json.charAt(i) == ',') i++;
            }
            if (i < json.length() && json.charAt(i) == '}') i++; // skip }
            // Parse condition
            if (!condition.isEmpty() && !target.isEmpty()) {
                String op = condition.contains("==") ? "==" : (condition.contains("!=") ? "!=" : "==");
                String[] parts = condition.split(op.equals("==") ? "==" : "!=", 2);
                if (parts.length == 2) {
                    Action act = Action.SHOW;
                    if (action.equals("hide")) act = Action.HIDE;
                    else if (action.equals("enable")) act = Action.ENABLE;
                    else if (action.equals("disable")) act = Action.DISABLE;
                    rules.add(new DependencyRule(parts[0], parts[1], op, target, act));
                }
            }
            i = skipWhitespace(json, i);
            if (i < json.length() && json.charAt(i) == ',') i++;
        }
    }

    private static int skipWhitespace(String s, int i) {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        return i;
    }
}
