package snowball049.roguelikemc.upgrade.presentation;

/**
 * A single GUI flame particle. Pure parametric motion driven by age; no Minecraft dependencies
 * so it can be unit-tested from the JUnit (main) source set.
 */
public final class FlameParticle {
    private final float originX;
    private final float originY;
    private final float riseSpeed; // pixels per millisecond (upward => y decreases)
    private final Sway sway;
    private final float baseSize;  // pixels
    private final float lifetimeMs;
    private float ageMs;

    public FlameParticle(float originX, float originY, float riseSpeed, Sway sway, float baseSize, float lifetimeMs) {
        this.originX = originX;
        this.originY = originY;
        this.riseSpeed = riseSpeed;
        this.sway = sway;
        this.baseSize = baseSize;
        this.lifetimeMs = lifetimeMs;
        this.ageMs = 0f;
    }

    public void update(float dtMs) {
        ageMs += dtMs;
    }

    public boolean isDead() {
        return ageMs >= lifetimeMs;
    }

    public float ageMs() {
        return ageMs;
    }

    public float lifeFraction() {
        return Math.min(1f, ageMs / lifetimeMs);
    }

    public float x() {
        return originX + sway.amplitude() * (float) Math.sin(sway.frequency() * ageMs + sway.phase());
    }

    public float y() {
        return originY - riseSpeed * ageMs;
    }

    public float alpha() {
        return (float) Math.sin(Math.PI * lifeFraction());
    }

    public float size() {
        return baseSize * (1f - 0.5f * lifeFraction());
    }

    /**
     * Horizontal sway: amplitude (pixels), frequency (radians per millisecond), phase (radians).
     */
    public record Sway(float amplitude, float frequency, float phase) {
    }
}
