package snowball049.roguelikemc.gameplay.death;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

public final class DecayItemStacks {
    private DecayItemStacks() {
    }

    public static boolean shouldDecay(double decayChance, double roll) {
        return roll < decayChance;
    }

    public static int randomCount(int min, int max, Random random) {
        if (max < min) {
            throw new IllegalArgumentException("max must be >= min");
        }
        return random.nextInt(max - min + 1) + min;
    }

    public static ItemStack create(Item item, int min, int max, Random random) {
        ItemStack stack = item.getDefaultStack();
        stack.setCount(randomCount(min, max, random));
        return stack;
    }

    public static ItemStack capCountToSource(ItemStack source, ItemStack decayed) {
        decayed.setCount(Math.min(source.getCount(), decayed.getCount()));
        return decayed;
    }
}
