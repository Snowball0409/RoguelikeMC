package snowball049.roguelikemc.data;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;

public final class RoguelikeMCAttribute {
    private RoguelikeMCAttribute() {
    }

    // Attribute IDs
    public static final Identifier EXPERIENCE_GAIN_ID = Identifier.of(RoguelikeMC.MOD_ID, "experience_gain");
    public static final Identifier DAMAGE_RATIO_ID = Identifier.of(RoguelikeMC.MOD_ID, "damage_ratio");
    public static final Identifier CRITICAL_CHANCE_ID = Identifier.of(RoguelikeMC.MOD_ID, "critical_chance");
    public static final Identifier CRITICAL_DAMAGE_ID = Identifier.of(RoguelikeMC.MOD_ID, "critical_damage");
    public static final Identifier THORNS_DAMAGE_ID = Identifier.of(RoguelikeMC.MOD_ID, "thorns_damage");

    // Pre-1.20.5 registration: Registry.register returns the attribute directly, no RegistryEntry wrapper.
    public static final EntityAttribute EXPERIENCE_GAIN = Registry.register(
            Registries.ATTRIBUTE,
            EXPERIENCE_GAIN_ID,
            new ClampedEntityAttribute(
                    "attribute.name.roguelikemc.experience_gain",
                    0.0D,
                    0.0D,
                    100.0D
            ).setTracked(true)
    );
    public static final EntityAttribute DAMAGE_RATIO = Registry.register(
            Registries.ATTRIBUTE,
            DAMAGE_RATIO_ID,
            new ClampedEntityAttribute(
                    "attribute.name.roguelikemc.damage_ratio",
                    0.0D,
                    -1.0D,
                    100.0D
            ).setTracked(true)
    );
    public static final EntityAttribute CRITICAL_CHANCE = Registry.register(
            Registries.ATTRIBUTE,
            CRITICAL_CHANCE_ID,
            new ClampedEntityAttribute(
                    "attribute.name.roguelikemc.critical_chance",
                    0.0D,
                    0.0D,
                    1.0D
            ).setTracked(true)
    );
    public static final EntityAttribute CRITICAL_DAMAGE = Registry.register(
            Registries.ATTRIBUTE,
            CRITICAL_DAMAGE_ID,
            new ClampedEntityAttribute(
                    "attribute.name.roguelikemc.critical_damage",
                    0.5D,
                    0.0D,
                    10.0D
            ).setTracked(true)
    );
    public static final EntityAttribute THORNS_DAMAGE = Registry.register(
            Registries.ATTRIBUTE,
            THORNS_DAMAGE_ID,
            new ClampedEntityAttribute(
                    "attribute.name.roguelikemc.thorn_damage",
                    0.0D,
                    0.0D,
                    10.0D
            ).setTracked(true)
    );
}
