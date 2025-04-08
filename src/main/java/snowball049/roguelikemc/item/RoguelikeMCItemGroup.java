package snowball049.roguelikemc.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public final class RoguelikeMCItemGroup {
    public static final ItemGroup INSTANCE = FabricItemGroup.builder()
            .icon(RoguelikeMCItems.UPGRADE_POINT_ORB::getDefaultStack)
            .displayName(Text.translatable("itemGroup.roguelikemc.item_group"))
            .entries(((displayContext, entries) -> {
                entries.add(RoguelikeMCItems.UPGRADE_POINT_ORB);
            }))
            .build();
}
