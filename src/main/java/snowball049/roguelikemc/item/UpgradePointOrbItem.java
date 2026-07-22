package snowball049.roguelikemc.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import snowball049.roguelikemc.upgrade.point.UpgradePointService;

public class UpgradePointOrbItem extends Item {
    public UpgradePointOrbItem(){
        this(new Settings().maxCount(64).rarity(Rarity.RARE));
    }

    public UpgradePointOrbItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Add upgrade point to player
            if(user.isSneaking()) {
                UpgradePointService.addUpgradePoints((ServerPlayerEntity) user, itemStack.getCount());
                itemStack.decrement(itemStack.getCount());
            }
            else {
                UpgradePointService.addUpgradePoints((ServerPlayerEntity) user, 1);
                if(!user.getAbilities().creativeMode)
                    itemStack.decrement(1);
            }
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
