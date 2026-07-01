package snowball049.roguelikemc.gameplay.death;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeathInventoryServiceTest {
    private ServerPlayerEntity player;
    private PlayerInventory inventory;

    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @BeforeEach
    void setUp() {
        player = Mockito.mock(ServerPlayerEntity.class);
        inventory = new PlayerInventory(player);
        Mockito.when(player.getInventory()).thenReturn(inventory);
        DeathInventoryService.setRandomSupplierForTesting(() -> 0.0);
        DeathInventoryService.setDecayedItemSupplierForTesting(() -> new ItemStack(Items.ROTTEN_FLESH, 2));
    }

    @AfterEach
    void tearDown() {
        DeathInventoryService.resetSuppliersForTesting();
    }

    @Test
    void decayInventoryReplacesNonEmptySlotsWhenRollSucceeds() {
        inventory.setStack(0, new ItemStack(Items.DIAMOND, 5));
        inventory.setStack(1, new ItemStack(Items.APPLE, 1));

        DeathInventoryService.decayInventory(player);

        assertEquals(Items.ROTTEN_FLESH, inventory.getStack(0).getItem());
        assertEquals(2, inventory.getStack(0).getCount());
        assertEquals(Items.ROTTEN_FLESH, inventory.getStack(1).getItem());
    }

    @Test
    void clearArmorAndWeaponsRemovesEquipmentSlotsOnly() {
        inventory.armor.set(0, new ItemStack(Items.DIAMOND_HELMET));
        inventory.setStack(0, new ItemStack(Items.DIAMOND_SWORD));
        inventory.setStack(1, new ItemStack(Items.APPLE, 3));
        inventory.offHand.set(0, new ItemStack(Items.SHIELD));

        DeathInventoryService.clearArmorAndWeapons(player);

        assertTrue(inventory.armor.get(0).isEmpty());
        assertTrue(inventory.getStack(0).isEmpty());
        assertEquals(Items.APPLE, inventory.getStack(1).getItem());
        assertTrue(inventory.offHand.get(0).isEmpty());
    }

    @Test
    void decayArmorAndWeaponsTargetsArmorAndWeaponSlots() {
        inventory.armor.set(0, new ItemStack(Items.DIAMOND_HELMET));
        inventory.setStack(0, new ItemStack(Items.DIAMOND_SWORD));
        inventory.setStack(1, new ItemStack(Items.APPLE, 3));

        DeathInventoryService.decayArmorAndWeapons(player);

        assertEquals(Items.ROTTEN_FLESH, inventory.armor.get(0).getItem());
        assertEquals(Items.ROTTEN_FLESH, inventory.getStack(0).getItem());
        assertEquals(Items.APPLE, inventory.getStack(1).getItem());
    }
}
