package snowball049.roguelikemc.upgrade.runtime;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record TriggerEventContext(
        LivingEntity target,
        BlockState blockState,
        MerchantEntity merchant,
        int levelsGained,
        RegistryKey<World> worldKey,
        BlockPos blockPos,
        boolean wasPlayerPlaced
) {
    public static TriggerEventContext empty() {
        return new TriggerEventContext(null, null, null, 0, null, null, false);
    }

    public static TriggerEventContext forTarget(LivingEntity target) {
        return new TriggerEventContext(target, null, null, 0, null, null, false);
    }

    public static TriggerEventContext forMerchant(MerchantEntity merchant) {
        return new TriggerEventContext(null, null, merchant, 0, null, null, false);
    }

    public static TriggerEventContext forLevelGain(int levels) {
        return new TriggerEventContext(null, null, null, Math.max(0, levels), null, null, false);
    }

    public static TriggerEventContext forBlock(BlockState state, RegistryKey<World> worldKey, BlockPos pos) {
        return forBlock(state, worldKey, pos, false);
    }

    public static TriggerEventContext forBlock(
            BlockState state,
            RegistryKey<World> worldKey,
            BlockPos pos,
            boolean wasPlayerPlaced
    ) {
        return new TriggerEventContext(
                null,
                state,
                null,
                0,
                worldKey,
                pos == null ? null : pos.toImmutable(),
                wasPlayerPlaced
        );
    }
}
