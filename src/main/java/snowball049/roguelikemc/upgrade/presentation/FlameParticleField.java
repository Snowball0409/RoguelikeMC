package snowball049.roguelikemc.upgrade.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Per-card flame particle pool. Handles spawn cadence, culling and the max cap.
 * Pure Java (no Minecraft dependencies) so it can be unit-tested from the main source set.
 */
public final class FlameParticleField {
    private static final int MAX_PARTICLES = 40;
    private static final float SPAWN_INTERVAL_MS = 20f;

    private final List<FlameParticle> particles = new ArrayList<>();
    private final Random random;
    private float spawnAccumulatorMs;
    private int colorArgb = 0xFFFFFFFF;

    public FlameParticleField(Random random) {
        this.random = random;
    }

    public FlameParticleField() {
        this(new Random());
    }

    public void setColor(int argb) {
        this.colorArgb = argb;
    }

    public int color() {
        return colorArgb;
    }

    public void update(float dtMs, boolean emit, float emitCenterX, float emitWidth, float baseY) {
        for (Iterator<FlameParticle> it = particles.iterator(); it.hasNext(); ) {
            FlameParticle p = it.next();
            p.update(dtMs);
            if (p.isDead()) {
                it.remove();
            }
        }

        if (emit) {
            spawnAccumulatorMs += dtMs;
            while (spawnAccumulatorMs >= SPAWN_INTERVAL_MS && particles.size() < MAX_PARTICLES) {
                spawnAccumulatorMs -= SPAWN_INTERVAL_MS;
                particles.add(spawnOne(emitCenterX, emitWidth, baseY));
            }
        } else {
            spawnAccumulatorMs = 0f;
        }
    }

    private FlameParticle spawnOne(float centerX, float width, float baseY) {
        float half = width / 2f;
        float originX = centerX + (random.nextFloat() * 2f - 1f) * half * 0.75f;
        float originY = baseY + random.nextFloat() * 3f;
        float riseSpeed = 0.16f + random.nextFloat() * 0.08f;
        float swayAmplitude = 2f + random.nextFloat() * 4f;
        float swayFrequency = 0.006f + random.nextFloat() * 0.006f;
        float swayPhase = random.nextFloat() * (float) (Math.PI * 2);
        float baseSize = 2f + random.nextFloat() * 2f;
        float lifetimeMs = 600f + random.nextFloat() * 300f;
        FlameParticle.Sway sway = new FlameParticle.Sway(swayAmplitude, swayFrequency, swayPhase);
        return new FlameParticle(originX, originY, riseSpeed, sway, baseSize, lifetimeMs);
    }

    public List<FlameParticle> particles() {
        return Collections.unmodifiableList(particles);
    }

    public boolean isEmpty() {
        return particles.isEmpty();
    }

    public void clear() {
        particles.clear();
        spawnAccumulatorMs = 0f;
    }
}
