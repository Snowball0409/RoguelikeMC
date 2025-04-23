package snowball049.roguelikemc.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.util.List;
import java.util.Optional;

public class RoguelikeMCEventUtil {
    public static void tickPlayerEvent(MinecraftServer minecraftServer) {
        minecraftServer.getWorlds().forEach(world -> {
            if (world.getDifficulty() == Difficulty.PEACEFUL) { return; }
            for (ServerPlayerEntity player : world.getPlayers()) {
                if(player.getAbilities().creativeMode) return;
                RoguelikeMCPlayerData playerState = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

                // 遍歷升級中所有 provoked 設定
                playerState.getAllUpgrades().forEach(upgrade -> {
                    upgrade.actions().stream()
                            .filter(action -> action.type().equals("event"))
                            .filter(action -> action.value().size() == 2 && action.value().get(0).equals("provoked"))
                            .forEach(action -> {
                                Identifier provokedId = Identifier.tryParse(action.value().get(1));
                                Optional<EntityType<?>> targetType = Registries.ENTITY_TYPE.getOrEmpty(provokedId);
                                if (targetType.isEmpty()) return;

                                // 搜尋玩家周圍符合條件的生物
                                List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class,
                                        player.getBoundingBox().expand(16), // radius = 16
                                        mob -> mob.getTarget() == null && mob.getType() == targetType.get());

                                for (MobEntity mob : mobs) {
                                    mob.setTarget(player);
                                }
                            });
                });
            }
        });
    }
}
