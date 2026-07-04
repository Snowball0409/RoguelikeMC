package snowball049.roguelikemc.upgrade.action.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

import java.util.List;

public final class AddLootTableEventHandler implements UpgradeEventHandler {
    @Override
    public String eventType() {
        return "add_loot_table";
    }

    @Override
    public void apply(UpgradeActionContext context) {
    }

    @Override
    public boolean matchesEntityKill(UpgradeActionContext context, LivingEntity target) {
        // Legacy normalized runtime payload: this positional event payload is a future
        // migration target once packet/runtime schema aligns with authored resources.
        List<String> value = context.action().value();
        if (value.size() < 3) {
            return false;
        }

        Identifier targetId = Registries.ENTITY_TYPE.getId(target.getType());
        return value.get(1).equals(targetId.toString());
    }

    @Override
    public void onEntityKill(UpgradeActionContext context, LivingEntity target, DamageSource source) {
        List<String> value = context.action().value();
        Identifier lootId = Identifier.tryParse(value.get(2));
        if (lootId == null) {
            return;
        }

        ServerWorld world = (ServerWorld) target.getWorld();
        LootTable table = world.getServer()
                .getReloadableRegistries()
                .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootId));

        LootContextParameterSet.Builder paramSetBuilder = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.THIS_ENTITY, target)
                .add(LootContextParameters.ORIGIN, target.getPos())
                .add(LootContextParameters.DAMAGE_SOURCE, source)
                .addOptional(LootContextParameters.ATTACKING_ENTITY, source.getAttacker());

        table.generateLoot(paramSetBuilder.build(LootContextTypes.ENTITY)).forEach(target::dropStack);
    }
}
