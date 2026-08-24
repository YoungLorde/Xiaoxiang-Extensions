package com.xiaoxiang.configext.client;

import java.util.List;

/**
 * Metadata for a single config entry, used by the custom config screen
 * to display rich descriptions, tooltips, and category information.
 */
public final class ConfigEntryInfo {
    /** The config key path, e.g. "realms.qiRefining.earlyMaxQi". */
    public final String key;
    /** Human-readable display name. */
    public final String displayName;
    /** Detailed multi-line description of what this config does. */
    public final String description;
    /** What happens when the value is increased. */
    public final String largerEffect;
    /** What happens when the value is decreased. */
    public final String smallerEffect;
    /** Top-level category tab name. */
    public final String topTab;
    /** Sub-tab name within the top tab. */
    public final String subTab;
    /** Optional group within the sub-tab. */
    public final String group;
    /** Keywords for search matching. */
    public final List<String> keywords;

    public ConfigEntryInfo(String key, String displayName, String description,
                           String largerEffect, String smallerEffect,
                           String topTab, String subTab, String group,
                           List<String> keywords) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.largerEffect = largerEffect;
        this.smallerEffect = smallerEffect;
        this.topTab = topTab;
        this.subTab = subTab;
        this.group = group;
        this.keywords = keywords;
    }

    /** Check if this entry matches a search query. */
    public boolean matches(String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.toLowerCase();
        if (displayName.toLowerCase().contains(q)) return true;
        if (key.toLowerCase().contains(q)) return true;
        if (description.toLowerCase().contains(q)) return true;
        if (subTab.toLowerCase().contains(q)) return true;
        if (topTab.toLowerCase().contains(q)) return true;
        for (String kw : keywords) {
            if (kw.toLowerCase().contains(q)) return true;
        }
        return false;
    }
}
