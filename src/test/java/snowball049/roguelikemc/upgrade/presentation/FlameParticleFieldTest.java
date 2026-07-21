package snowball049.roguelikemc.upgrade.presentation;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlameParticleFieldTest {
    private static final float INTERVAL = 20f; // must match FlameParticleField.SPAWN_INTERVAL_MS

    @Test
    void spawnsOneParticlePerIntervalWhileEmitting() {
        FlameParticleField field = new FlameParticleField(new Random(1));
        for (int i = 0; i < 5; i++) {
            field.update(INTERVAL, true, 100f, 100f, 200f);
        }
        assertEquals(5, field.particles().size());
    }

    @Test
    void doesNotSpawnWhenNotEmitting() {
        FlameParticleField field = new FlameParticleField(new Random(1));
        field.update(1000f, false, 100f, 100f, 200f);
        assertTrue(field.isEmpty());
    }

    @Test
    void isCappedAtMax() {
        FlameParticleField field = new FlameParticleField(new Random(1));
        field.update(INTERVAL * 500f, true, 100f, 100f, 200f);
        assertEquals(40, field.particles().size(), "must not exceed MAX_PARTICLES");
    }

    @Test
    void existingParticlesFadeOutWhenEmissionStops() {
        FlameParticleField field = new FlameParticleField(new Random(1));
        for (int i = 0; i < 5; i++) {
            field.update(INTERVAL, true, 100f, 100f, 200f);
        }
        assertFalse(field.isEmpty());
        field.update(2000f, false, 100f, 100f, 200f); // longer than any lifetime
        assertTrue(field.isEmpty());
    }

    @Test
    void clearRemovesAllParticles() {
        FlameParticleField field = new FlameParticleField(new Random(1));
        field.update(INTERVAL, true, 100f, 100f, 200f);
        field.clear();
        assertTrue(field.isEmpty());
    }
}
