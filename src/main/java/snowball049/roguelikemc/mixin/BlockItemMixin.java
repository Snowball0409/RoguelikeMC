package snowball049.roguelikemc.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snowball049.roguelikemc.upgrade.runtime.UpgradeTriggerRuntimeService;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At("RETURN")
    )
    private void roguelike$onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getWorld().isClient) {
            return;
        }
        if (!cir.getReturnValue().isAccepted()) {
            return;
        }
        if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
            return;
        }
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);
        UpgradeTriggerRuntimeService.onBlockPlacedByPlayer(
                player,
                context.getWorld().getRegistryKey(),
                pos,
                state
        );
    }
}
