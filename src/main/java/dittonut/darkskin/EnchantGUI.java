package dittonut.darkskin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.Note.Tone;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

import static dittonut.darkskin.DarkSkin.mm;

public class EnchantGUI {
    private static final Random r = new Random();

    public static Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, 27, Enums.ENCHANT_GUI_TITLE);
        ItemStack item = new ItemStack(Enums.FILLER_ITEM);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(Enums.FILLER_MODEL);
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i == 4) continue;
            inv.setItem(i, item.clone());
        }
        return inv;
    }

    public static void click(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getSlot() == 26 && p.isOp()) {
            p.getInventory().addItem(Enums.getStardust());
            e.setCancelled(true);
        }
        if (e.getCurrentItem() != null
                && e.getCurrentItem().getItemMeta().hasCustomModelData()
                && e.getCurrentItem().getItemMeta().getCustomModelData() == Enums.FILLER_MODEL) {
            e.setCancelled(true);
        }
        if (!e.isShiftClick() && e.getSlot() == 4
                && e.getInventory().getItem(4) != null) {
            e.setCancelled(true);
            enchant(p, e.getInventory().getItem(4));
        }
    }

    /**
     * Enchants item.
     * Note: Stardust is consumed by this method
     *
     * @param e the player trying to upgrade
     * @param item   the item trying to upgrade
     */
    public static void enchant(HumanEntity e, ItemStack item) {
        if (!(e instanceof Player p)) return;
        if (!isEnchantable(item)) {
            p.sendMessage(mm.deserialize("<red>인챈트가 불가능한 아이템이에요!"));
            p.playNote(p.getLocation(), Instrument.DIDGERIDOO, Note.sharp(0, Tone.F)); //TODO: 이거 소리 옆에서들리는지, NOTE: NonStandard
            return;
        }
        if (!p.getInventory().containsAtLeast(Enums.getStardust(), 1)) {
            p.sendMessage(mm.deserialize("<gold>별가루<red>가 부족해요!"));
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 0.86f);
            return;
        }
        p.playSound(p.getLocation(), Sound.BLOCK_CHAIN_BREAK, 1.0f, 1.5f);
        p.getInventory().removeItem(Enums.getStardust());

        Set<Enchantment> current = item.getEnchantments().keySet();
        Map<Enchantment, Integer> changed = new HashMap<>();
        int count = current.size();

        item.getEnchantments().forEach((enchantment, integer) -> {
            item.removeEnchantment(enchantment);
        });

        if (count < Enums.MAX_ENCHANTMENTS && (count == 0 || r.nextInt(Enums.ENCHANT_ADD_CHANCE) == 0)) {
            count++;
        };

        for (int i = 0; i < count; i++) {
            if ((current.contains(Enchantment.BINDING_CURSE) || r.nextInt(1000) == 0)
                    && !changed.containsKey(Enchantment.BINDING_CURSE)) {
                changed.put(Enchantment.BINDING_CURSE, 1);
                continue;
            } else if ((current.contains(Enchantment.VANISHING_CURSE) || r.nextInt(1000) == 0)
                    && !changed.containsKey(Enchantment.VANISHING_CURSE)) {
                changed.put(Enchantment.VANISHING_CURSE, 1);
                continue;
            }
            Enchantment rand = randomEnchant(changed.keySet());
            changed.put(rand, r.nextInt(rand.getMaxLevel()) + 1);
        }

        item.addUnsafeEnchantments(changed);

//        Set<Enchantment> enchs = item.getEnchantments().keySet();
//        int size = enchs.size();
//
//        if (size == 0 || (r.nextInt(Enums.ENCHANT_ADD_CHANCE) == 0) && size < Enums.MAX_ENCHANTMENTS) size++;
//        Map<Enchantment, Integer> added = new HashMap<>();
//        item.getEnchantments().forEach((ench, lvl) -> item.removeEnchantment(ench));
//        for (int i = 0; i < size; i++) {
//            if (enchs.contains(Enchantment.BINDING_CURSE) || r.nextInt(1000) == 0)
//                added.put(Enchantment.BINDING_CURSE, 1);
//            else if (enchs.contains(Enchantment.VANISHING_CURSE) || r.nextInt(1000) == 0)
//                added.put(Enchantment.VANISHING_CURSE, 1);
//            else {
//                Enchantment re = randomEnchant(added.keySet());
//                added.put(re, RandomUtils.nextInt(1, re.getMaxLevel()));
//            }
//        }
    }

    private static boolean isEnchantable(ItemStack stack) {
        return Arrays.stream(Enchantment.values())
                .anyMatch(ench -> ench.canEnchantItem(stack));
    }

    private static @NotNull Enchantment randomEnchant(Set<Enchantment> filtered) {
        List<Enchantment> enchantments =
                Arrays.stream(Enchantment.values())
                        .filter(e -> !filtered.contains(e)).toList();
        return enchantments.isEmpty() ? Enchantment.DURABILITY : enchantments.get(r.nextInt(enchantments.size()));
    }
}
