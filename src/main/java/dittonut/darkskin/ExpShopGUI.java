package dittonut.darkskin;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import static dittonut.darkskin.DarkSkin.mm;

public class ExpShopGUI {
  private static boolean hasLevel(Player p, int lvl) {
    return p.getLevel() >= lvl;
  }

  private static void sell(Player p, ItemStack i, int count, int cost) {
    if (hasLevel(p, cost)) {
      p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.86f);
      Utils.addItem(p, Utils.withAmount(i, count));
    }
    else {
      p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 0.5f, 0.86f); //TODO: Check this is for player, or everyone near
      p.sendMessage(mm.deserialize("<red>레벨이 부족해요!"));
    }
  }

  public static void click(InventoryClickEvent e) {
    if (!(e.getWhoClicked() instanceof Player p)) return;
    if (e.getSlot() == 10) sell(p, Enums.getStardust(), 40, 40); //40렙-별가루40개
    else if (e.getSlot() == 12) sell(p, Enums.getStarpiece(), 20, 40); //40렙-별조각20개
    else if (e.getSlot() == 14) sell(p, new ItemStack(Material.TOTEM_OF_UNDYING), 2, 40); //40렙-토템2개
    else if (e.getSlot() == 16) sell(p, new ItemStack(Material.BEACON), 1, 100); //100렙-신호기1개
  }

  public static Inventory getInventory(Player p) {
    Inventory inv = Bukkit.createInventory(p, 27);
    for (int i = 0; i < 27; i++) {
      if (Set.of(10, 12, 14, 16).contains(i)) continue;
      inv.setItem(i, Enums.getFiller());
    }
    inv.setItem(10, shopItem(Enums.getStardust(), 40, 40));
    inv.setItem(10, shopItem(Enums.getStarpiece(), 20, 40));
    inv.setItem(10, shopItem(new ItemStack(Material.TOTEM_OF_UNDYING), 2, 40));
    inv.setItem(10, shopItem(new ItemStack(Material.BEACON), 1, 100));
    return inv;
  }

  /**
   * Important note: THIS DOES NOT CLONE ITEMSTACK!!
  */
  private static ItemStack shopItem(ItemStack stack, int count, int cost) {
    stack.setAmount(count);
    stack.lore(List.of(Component.text(""), mm.deserialize("<green>경험치: %d레벨".formatted(cost))));
    return stack;
  }
}
