package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.config.ExtendedConfig;

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

    /**
     * User-configurable tab-click animation, set from the config screen's own
     * "config for the config" gear panel (ThemeSettingsPopup) and persisted to
     * ExtendedConfig.CLIENT_UI_TAB_ANIMATION_STYLE / _SPEED_PERCENT. "slide" is
     * the original fade+slide behavior below, "fade" keeps the fade but never
     * slides horizontally, and "none" skips the entrance animation entirely
     * (rows/chrome appear at full progress immediately) - independent of, and
     * layered on top of, the existing reduceMotion accessibility toggle.
     */
    private String tabAnimationStyle() {
        try {
            String s = ExtendedConfig.CLIENT_UI_TAB_ANIMATION_STYLE.get();
            return s == null ? "slide" : s;
        } catch (Exception e) {
            return "slide"; // config not loaded yet (e.g. very first frame) - default behavior
        }
    }

    /** The user's speed setting, clamped to the range ExtendedConfig itself enforces (10-400). */
    private int speedPercent() {
        int percent = 100;
        try {
            percent = ExtendedConfig.CLIENT_UI_TAB_ANIMATION_SPEED_PERCENT.get();
        } catch (Exception e) { /* default to 100% */ }
        if (percent < 10) percent = 10;
        if (percent > 400) percent = 400;
        return percent;
    }

    /**
     * Per-theme-family pacing on top of the user's own speed slider - e.g.
     * SERENE (Immortal Jade Court) plays noticeably slower than PULSE_FAST
     * (Celestial Neon) even at the same 100% speed setting, matching each
     * family's intended "feel" (see Theme.AnimFamily's doc comment). The
     * user's speed setting is still the outer multiplier applied afterward
     * in effectiveTransitionMs()/effectiveRowStaggerMs() - this only sets
     * each family's own baseline before that.
     */
    private float familyDurationMultiplier() {
        switch (currentFamily()) {
            case FLOWING: return 1.15f;
            case PULSE_FAST: return 0.65f;
            case STEADY: return 1.5f;
            case ERRATIC: return 0.85f;
            case SERENE: return 2.0f;
            case DRIFT: return 1.3f;
            case FORMATION:
            case STANDARD:
            default: return 1.0f;
        }
    }

    private float familyStaggerMultiplier() {
        switch (currentFamily()) {
            case STEADY: return 1.2f;
            case PULSE_FAST: return 0.8f;
            case FORMATION: return 1.8f; // strong per-row cascade - formation array assembling piece by piece
            case DRIFT: return 0.6f; // rows fade in closer together, not staggered far apart
            case FLOWING:
            case ERRATIC:
            case SERENE:
            case STANDARD:
            default: return 1.0f;
        }
    }

    /** Effective transition duration after applying the user's speed setting (100% = TRANSITION_MS) and the active theme's own pacing family. */
    private long effectiveTransitionMs() {
        long ms = (long) (TRANSITION_MS * familyDurationMultiplier()) * speedPercent() / 100L;
        return ms < 1L ? 1L : ms;
    }

    /**
     * Effective per-row stagger after applying the user's speed setting and the
     * active theme's own pacing family. Fixed at ROW_STAGGER_MS regardless of
     * speed used to be the actual bug behind "the speed slider doesn't seem to
     * do anything": for a full page of rows the stagger (up to 24 rows *
     * ROW_STAGGER_MS) dominates the total time far more than the single-row
     * fade duration does, so scaling only the fade and not the stagger left the
     * speed setting's effect barely perceptible. Both now scale together.
     */
    private long effectiveRowStaggerMs() {
        long ms = (long) (ROW_STAGGER_MS * familyStaggerMultiplier()) * speedPercent() / 100L;
        return ms < 0L ? 0L : ms;
    }

    /** Begin (or restart) the entry-list entrance animation. */
    public void startTransition() {
        transitionStartMs = System.currentTimeMillis();
    }

    /** True while the entrance animation is still playing. */
    public boolean isTransitioning() {
        if (reduceMotion || transitionStartMs == 0L) return false;
        if ("none".equals(tabAnimationStyle())) return false;
        return System.currentTimeMillis() - transitionStartMs < effectiveTransitionMs() + effectiveRowStaggerMs() * 24L;
    }

    /**
     * Entrance progress for the {@code visibleIndex}-th row currently on screen.
     * Returns 1 when there is no animation in flight, so callers can always
     * multiply by it unconditionally.
     */
    public float rowProgress(int visibleIndex) {
        if (reduceMotion || transitionStartMs == 0L) return 1.0f;
        if ("none".equals(tabAnimationStyle())) return 1.0f;
        int idx = visibleIndex;
        if (idx < 0) idx = 0;
        if (idx > 24) idx = 24; // cap the stagger so long lists still finish promptly
        long duration = effectiveTransitionMs();
        long elapsed = System.currentTimeMillis() - transitionStartMs - idx * effectiveRowStaggerMs();
        if (elapsed <= 0L) return 0.0f;
        if (elapsed >= duration) return 1.0f;
        return familyEase(elapsed / (float) duration);
    }

    /** Overall (non-staggered) entrance progress, for chrome that fades as a whole. */
    public float transitionProgress() {
        if (reduceMotion || transitionStartMs == 0L) return 1.0f;
        if ("none".equals(tabAnimationStyle())) return 1.0f;
        long duration = effectiveTransitionMs();
        long elapsed = System.currentTimeMillis() - transitionStartMs;
        if (elapsed <= 0L) return 0.0f;
        if (elapsed >= duration) return 1.0f;
        return familyEase(elapsed / (float) duration);
    }

    /** Horizontal offset (px) for a row at the given entrance progress. */
    public int slideOffset(float progress) {
        if (progress >= 1.0f) return 0;
        if (!"slide".equals(tabAnimationStyle())) return 0; // "fade" and "none" never slide
        // DRIFT is "pure fade, no slide" by design (see Theme.AnimFamily's doc) even
        // when the user's own style setting is "slide" - it overrides the slide axis
        // specifically, the same way "none" overrides the whole animation above.
        if (currentFamily() == Theme.AnimFamily.DRIFT) return 0;
        int px = (int) ((1.0f - progress) * ROW_SLIDE_PX);
        return px;
    }

    /**
     * Which animation "feel" is currently active - read from the active theme
     * (see Theme.AnimFamily's doc comment). Wrapped in its own try/catch, same
     * defensive pattern as tabAnimationStyle()/speedPercent() above, since
     * Theme.current can theoretically be read before the theme system finishes
     * loading (e.g. the very first frame of the very first screen open).
     */
    private Theme.AnimFamily currentFamily() {
        try {
            Theme.AnimFamily f = Theme.current.animFamily;
            return f == null ? Theme.AnimFamily.STANDARD : f;
        } catch (Exception e) {
            return Theme.AnimFamily.STANDARD;
        }
    }

    /**
     * Per-theme-family easing curve, dispatched from rowProgress()/
     * transitionProgress() instead of always calling easeOutCubic() directly.
     * This is the direct answer to "different themes gonna have different
     * animations... spacey feel, futuristic, old school, medieval..." - each
     * family gets a genuinely different motion curve, not just a recolored
     * version of the same cubic ease everything used to share. Clamped to a
     * bounded range (not strictly [0,1] - ERRATIC's slight overshoot is the
     * point) so a family's curve can never hand back a wildly out-of-range
     * value; downstream consumers (Theme.textAlpha, Theme.lerp) already clamp
     * their own inputs too, so this is defense in depth, not the only guard.
     */
    private float familyEase(float t) {
        float eased;
        switch (currentFamily()) {
            case FLOWING: eased = easeOutQuad(t); break;
            case STEADY: eased = easeOutQuad(t) * 0.94f + t * 0.06f; break; // slightly less snap than FLOWING
            case ERRATIC: eased = easeOutBack(t); break;
            case SERENE: eased = easeInOutSine(t); break;
            case DRIFT: eased = easeOutSine(t); break;
            case PULSE_FAST:
            case FORMATION:
            case STANDARD:
            default: eased = easeOutCubic(t); break;
        }
        if (eased < -0.2f) eased = -0.2f;
        if (eased > 1.2f) eased = 1.2f;
        return eased;
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

    /** Gentler ease-out than the cubic above - used by the FLOWING theme family (ink settling onto paper). */
    public static float easeOutQuad(float t) {
        if (t <= 0.0f) return 0.0f;
        if (t >= 1.0f) return 1.0f;
        float inv = 1.0f - t;
        return 1.0f - inv * inv;
    }

    /** Smooth sine ease-out - used by the DRIFT theme family (fading into being, no snap at all). */
    public static float easeOutSine(float t) {
        if (t <= 0.0f) return 0.0f;
        if (t >= 1.0f) return 1.0f;
        return (float) Math.sin(t * (Math.PI / 2.0));
    }

    /** Symmetric sine ease-in-out - used by the SERENE theme family (very gentle both ends, minimal motion). */
    public static float easeInOutSine(float t) {
        if (t <= 0.0f) return 0.0f;
        if (t >= 1.0f) return 1.0f;
        return (float) (-(Math.cos(Math.PI * t) - 1.0) / 2.0);
    }

    /**
     * Ease-out-back: overshoots past 1.0 before settling, the standard
     * "back" easing formula (constant below is the conventional c1 = 1.70158
     * used by every ease-out-back reference implementation - not tuned by
     * hand). Used by the ERRATIC theme family (Demonic Blood) for a slight
     * unstable snap. familyEase() clamps its result to [-0.2, 1.2] so the
     * overshoot this produces stays bounded.
     */
    public static float easeOutBack(float t) {
        if (t <= 0.0f) return 0.0f;
        if (t >= 1.0f) return 1.0f;
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float x = t - 1.0f;
        return 1.0f + c3 * x * x * x + c1 * x * x;
    }

    /** Smooth 0..1 ping-pong, handy for breathing/pulsing accents. */
    public static float pulse(long periodMs) {
        if (periodMs <= 0L) return 1.0f;
        double phase = (System.currentTimeMillis() % periodMs) / (double) periodMs;
        return (float) ((1.0 - Math.cos(phase * Math.PI * 2.0)) * 0.5);
    }
}
