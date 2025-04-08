package snowball049.roguelikemc.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import snowball049.roguelikemc.util.RoguelikeMCPointUtil;

import java.util.List;

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
                RoguelikeMCPointUtil.addUpgradePoints((ServerPlayerEntity) user, itemStack.getCount());
                itemStack.decrement(itemStack.getCount());
            }
            else {
                RoguelikeMCPointUtil.addUpgradePoints((ServerPlayerEntity) user, 1);
                if(!user.getAbilities().creativeMode)
                    itemStack.decrement(1);
            }
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
