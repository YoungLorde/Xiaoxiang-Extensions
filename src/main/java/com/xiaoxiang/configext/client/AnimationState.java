package com.xiaoxiang.configext.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiny frame-driven animation helper for the config screen.
 *
 * Provides two things:
 *
 *  1. Per-widget hover progress. {@link #hover(String, boolean)} returns an eased
 *     0..1 value that ramps over {@link #HOVER_MS} instead of snapping, so tab /
 *     sub-tab / util-button highlights fade in and out.
 *
 *  2. A tab-switch entrance transition. {@link #startTransition()} is called when
 *     the active top tab, sub-tab or group changes; the entry list render pass then
 *     asks {@link #rowProgress(int)} for a per-row 0..1 value and fades + slides the
 *     new rows in. GuiGraphics has no off-screen buffer, so this is deliberately an
 *     entrance animation for the new content only - there is no cross-fade of the
 *     old content.
 *
 * All timing is wall-clock based (System.currentTimeMillis) so it is independent of
 * the render tick rate. Everything degrades to "instantly finished" when the user
 * has Reduce Motion enabled.
 */
public final class AnimationState {

    /** Hover highlights ramp over this many milliseconds. */
    public static final long HOVER_MS = 120L;
    /** Entry-list entrance animation duration. */
    public static final long TRANSITION_MS = 170L;
    /** Per-row stagger so rows cascade in rather than all appearing at once. */
    public static final long ROW_STAGGER_MS = 12L;
    /** How far (px) a row slides in from the left during the entrance animation. */
    public static final int ROW_SLIDE_PX = 8;
    /** Safety cap: forget hover state if it grows unreasonably (screen resize churn). */
    private static final int MAX_TRACKED = 512;

    private final Map<String, Float> hoverProgress = new HashMap<>();

    private long lastFrameMs = 0L;
    private float frameRate = 0f; // fraction of HOVER_MS elapsed this frame
    private long transitionStartMs = 0L;
    private boolean reduceMotion = false;

    /**
     * Must be called once at the top of every render pass, before any other method.
     * Advances the internal clock and records whether animations are suppressed.
     */
    public void beginFrame(boolean reduceMotionEnabled) {
        this.reduceMotion = reduceMotionEnabled;
        long now = System.currentTimeMillis();
        if (lastFrameMs == 0L) lastFrameMs = now;
        long dt = now - lastFrameMs;
        if (dt < 0L) dt = 0L;
        if (dt > 250L) dt = 250L; // don't jump after a long stall (world load, alt-tab)
        lastFrameMs = now;
        frameRate = dt / (float) HOVER_MS;
        if (hoverProgress.size() > MAX_TRACKED) hoverProgress.clear();
    }

    /**
     * Eased hover progress for one widget.
     *
     * @param key     stable identity for the widget, e.g. "toptab:Sects"
     * @param hovered whether the mouse is over it right now
     * @return 0 (not hovered) .. 1 (fully hovered), eased
     */
    public float hover(String key, boolean hovered) {
        if (reduceMotion) return hovered ? 1.0f : 0.0f;
        Float stored = hoverProgress.get(key);
        float cur = stored != null ? stored : (hovered ? 0.0f : 0.0f);
        float target = hovered ? 1.0f : 0.0f;
        if (cur < target) {
            cur += frameRate;
            if (cur > target) cur = target;
        } else if (cur > target) {
            cur -= frameRate;
            if (cur < target) cur = target;
        }
        hoverProgress.put(key, cur);
        return easeOutCubic(cur);
    }

    /** Drop all remembered hover state (used when the tab set changes). */
    public void clearHover() {
        hoverProgress.clear();
    }

    /** Begin (or restart) the entry-list entrance animation. */
    public void startTransition() {
        transitionStartMs = System.currentTimeMillis();
    }

    /** True while the entrance animation is still playing. */
    public boolean isTransitioning() {
        if (reduceMotion || transitionStartMs == 0L) return false;
        return System.currentTimeMillis() - transitionStartMs < TRANSITION_MS + ROW_STAGGER_MS * 24L;
    }

    /**
     * Entrance progress for the {@code visibleIndex}-th row currently on screen.
     * Returns 1 when there is no animation in flight, so callers can always
     * multiply by it unconditionally.
     */
    public float rowProgress(int visibleIndex) {
        if (reduceMotion || transitionStartMs == 0L) return 1.0f;
        int idx = visibleIndex;
        if (idx < 0) idx = 0;
        if (idx > 24) idx = 24; // cap the stagger so long lists still finish promptly
        long elapsed = System.currentTimeMillis() - transitionStartMs - idx * ROW_STAGGER_MS;
        if (elapsed <= 0L) return 0.0f;
        if (elapsed >= TRANSITION_MS) return 1.0f;
        return easeOutCubic(elapsed / (float) TRANSITION_MS);
    }

    /** Overall (non-staggered) entrance progress, for chrome that fades as a whole. */
    public float transitionProgress() {
        if (reduceMotion || transitionStartMs == 0L) return 1.0f;
        long elapsed = System.currentTimeMillis() - transitionStartMs;
        if (elapsed <= 0L) return 0.0f;
        if (elapsed >= TRANSITION_MS) return 1.0f;
        return easeOutCubic(elapsed / (float) TRANSITION_MS);
    }

    /** Horizontal offset (px) for a row at the given entrance progress. */
    public int slideOffset(float progress) {
        if (progress >= 1.0f) return 0;
        int px = (int) ((1.0f - progress) * ROW_SLIDE_PX);
        return px;
    }

    /** Number of pixels a widget grows by on each side at the given hover progress. */
    public int hoverExpand(float progress, int maxPixels) {
        return Math.round(progress * maxPixels);
    }

    /** Standard ease-out curve used everywhere in this class. */
    public static float easeOutCubic(float t) {
        if (t <= 0.0f) return 0.0f;
        if (t >= 1.0f) return 1.0f;
        float inv = 1.0f - t;
        return 1.0f - inv * inv * inv;
    }

    /** Smooth 0..1 ping-pong, handy for breathing/pulsing accents. */
    public static float pulse(long periodMs) {
        if (periodMs <= 0L) return 1.0f;
        double phase = (System.currentTimeMillis() % periodMs) / (double) periodMs;
        return (float) ((1.0 - Math.cos(phase * Math.PI * 2.0)) * 0.5);
    }
}
