package dittonut.darkskin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public enum UpgradeEquipment {
    SWORD(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    TRIDENT(Material.TRIDENT),
    AXE(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE),
    BOW(Material.BOW),
    BOOTS(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS),
    CHESTPLATE(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE),
    LEGGINGS(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS),
    HELMET(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET);

    private static final Map<Material, UpgradeEquipment> MATERIAL_MAP = new EnumMap<>(Material.class);

    static {
        for (UpgradeEquipment equipment : values()) {
            for (Material material : equipment.materials) {
                MATERIAL_MAP.put(material, equipment);
            }
        }
    }

    private final Material[] materials;

    UpgradeEquipment(Material... materials) {
        this.materials = materials;
    }

    public static UpgradeEquipment of(@NotNull Material material) {
        return MATERIAL_MAP.get(material);
    }

    public static UpgradeEquipment of(@NotNull ItemStack stack) {
        return of(stack.getType());
    }
}
