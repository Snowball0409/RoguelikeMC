package snowball049.roguelikemc.upgrade.action.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

import java.util.List;

public final class SetEquipmentEventHandler implements UpgradeEventHandler {
    @Override
    public String eventType() {
        return "set_equipment";
    }

    @Override
    public void apply(UpgradeActionContext context) {
        // Legacy normalized runtime payload: semantic authored fields have already been
        // flattened into value[] before this handler executes.
        List<String> value = context.action().value();
        try {
            int slotIndex = Integer.parseInt(value.get(1));
            String nbtString = value.get(2);
            NbtCompound nbt = !nbtString.isEmpty() ? StringNbtReader.parse(nbtString) : new NbtCompound();
            if (!context.player().getInventory().armor.get(slotIndex).isEmpty()
                    && !context.player().getInventory().armor.get(slotIndex).getItem().equals(
                    ItemStack.fromNbt(nbt).getItem())) {
                context.player().dropItem(context.player().getInventory().armor.get(slotIndex), false);
                context.player().sendMessage(Text.translatable("message.roguelikemc.drop_equipment"), false);
            }
            context.player().getInventory().armor.set(
                    slotIndex,
                    ItemStack.fromNbt(nbt)
            );
        } catch (CommandSyntaxException e) {
            RoguelikeMC.LOGGER.warn("{}:{}", e.getClass(), e.getMessage());
        }
    }

    @Override
    public void remove(UpgradeActionContext context) {
        int slotIndex = Integer.parseInt(context.action().value().get(1));
        context.player().getInventory().armor.set(slotIndex, ItemStack.EMPTY);
    }

    @Override
    public int tickInterval() {
        return 20;
    }

    @Override
    public boolean matchesPeriodicTick(UpgradeActionContext context) {
        // Future packet/runtime schema convergence should replace this positional check.
        List<String> value = context.action().value();
        return value.size() > 2 && value.get(2).isEmpty();
    }
}
