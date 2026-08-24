package com.xiaoxiang.configext.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight toast-style notification system for the config screen.
 * Notifications appear in the top-right corner and fade out after a few seconds.
 */
public class NotificationSystem {
    public static class Notification {
        public final String text;
        public final int color;
        public final long createdMs;
        public static final long DURATION_MS = 3000;

        public Notification(String text, int color) {
            this.text = text;
            this.color = color;
            this.createdMs = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - createdMs > DURATION_MS;
        }

        public float getAlpha() {
            long elapsed = System.currentTimeMillis() - createdMs;
            if (elapsed < 300) return elapsed / 300f;           // fade in
            if (elapsed > DURATION_MS - 500) return Math.max(0, (DURATION_MS - elapsed) / 500f); // fade out
            return 1.0f;
        }
    }

    private static final List<Notification> active = new ArrayList<>();
    private static final int MAX_NOTIFICATIONS = 5;

    public static void show(String text, int color) {
        try {
            if (!com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_ENABLE_NOTIFICATIONS.get()) return;
        } catch (Exception e) { /* config not loaded yet */ }
        active.add(new Notification(text, color));
        while (active.size() > MAX_NOTIFICATIONS) active.remove(0);
    }

    public static void showInfo(String text) {
        show(text, 0xFF40C0FF);
    }

    public static void showSuccess(String text) {
        show("\u00A7a\u2713 " + text, 0xFF40FF40);
    }

    public static void showWarning(String text) {
        show("\u00A7e\u26A0 " + text, 0xFFFFE040);
    }

    public static void showError(String text) {
        show("\u00A7c\u2717 " + text, 0xFFFF4040);
    }

    public static List<Notification> getActive() {
        active.removeIf(Notification::isExpired);
        return active;
    }

    public static void clear() {
        active.clear();
    }
}
