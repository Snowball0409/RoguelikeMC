package snowball049.roguelikemc.upgrade.presentation;

/**
 * Pure eased pop-in scale (0.6 -> 1.0 with slight overshoot) over a fixed duration.
 * No Minecraft dependencies so it can be unit-tested from the main source set.
 */
public final class CardScaleAnimator {
    private static final float DURATION_MS = 220f;
    private static final float START_SCALE = 0.6f;

    private float elapsedMs = -1f; // negative = idle / not started

    public void start() {
        elapsedMs = 0f;
    }

    public void reset() {
        elapsedMs = -1f;
    }

    public boolean isAnimating() {
        return elapsedMs >= 0f && elapsedMs < DURATION_MS;
    }

    public void update(float dtMs) {
        if (elapsedMs < 0f) {
            return;
        }
        elapsedMs += dtMs;
        if (elapsedMs > DURATION_MS) {
            elapsedMs = DURATION_MS;
        }
    }

    public float scale() {
        if (elapsedMs < 0f) {
            return 1f;
        }
        float t = Math.min(1f, elapsedMs / DURATION_MS);
        float eased = easeOutBack(t);
        return START_SCALE + (1f - START_SCALE) * eased;
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        float u = t - 1f;
        return 1f + c3 * u * u * u + c1 * u * u;
    }
}
