package snowball049.roguelikemc.upgrade.presentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardScaleAnimatorTest {
    @Test
    void idleScaleIsFullSize() {
        CardScaleAnimator animator = new CardScaleAnimator();
        assertEquals(1f, animator.scale(), 0.001f);
        assertFalse(animator.isAnimating());
    }

    @Test
    void startsAtStartScale() {
        CardScaleAnimator animator = new CardScaleAnimator();
        animator.start();
        assertEquals(0.6f, animator.scale(), 0.001f);
        assertTrue(animator.isAnimating());
    }

    @Test
    void reachesFullSizeAtEnd() {
        CardScaleAnimator animator = new CardScaleAnimator();
        animator.start();
        animator.update(220f);
        assertEquals(1f, animator.scale(), 0.001f);
        assertFalse(animator.isAnimating());
    }

    @Test
    void clampsAfterDuration() {
        CardScaleAnimator animator = new CardScaleAnimator();
        animator.start();
        animator.update(10_000f);
        assertEquals(1f, animator.scale(), 0.001f);
    }

    @Test
    void resetReturnsToIdleFullSize() {
        CardScaleAnimator animator = new CardScaleAnimator();
        animator.start();
        animator.reset();
        assertEquals(1f, animator.scale(), 0.001f);
        assertFalse(animator.isAnimating());
    }
}
