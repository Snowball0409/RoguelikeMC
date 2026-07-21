package snowball049.roguelikemc.upgrade.presentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlameParticleTest {
    private static final FlameParticle.Sway NO_SWAY = new FlameParticle.Sway(0f, 0f, 0f);

    @Test
    void risesUpwardAsItAges() {
        FlameParticle p = new FlameParticle(100f, 200f, 0.05f, NO_SWAY, 3f, 800f);
        float startY = p.y();
        p.update(400f);
        assertTrue(p.y() < startY, "y should decrease (rise) as particle ages");
        assertEquals(180f, p.y(), 0.001f);
    }

    @Test
    void alphaPeaksAtMidLifeAndIsZeroAtBirth() {
        FlameParticle p = new FlameParticle(0f, 0f, 0.05f, NO_SWAY, 3f, 800f);
        assertEquals(0f, p.alpha(), 0.001f);
        p.update(400f);
        assertEquals(1f, p.alpha(), 0.01f);
    }

    @Test
    void diesAfterLifetime() {
        FlameParticle p = new FlameParticle(0f, 0f, 0.05f, NO_SWAY, 3f, 800f);
        p.update(700f);
        assertFalse(p.isDead());
        p.update(200f);
        assertTrue(p.isDead());
    }

    @Test
    void swayMovesXHorizontally() {
        FlameParticle.Sway sway = new FlameParticle.Sway(5f, (float) (Math.PI / 2 / 100f), 0f);
        FlameParticle p = new FlameParticle(50f, 200f, 0f, sway, 3f, 800f);
        assertEquals(50f, p.x(), 0.01f);
        p.update(100f);
        assertEquals(55f, p.x(), 0.05f);
    }
}
