package dittonut.darkskin;

import java.util.List;
import java.util.Set;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import org.w3c.dom.Text;

import static dittonut.darkskin.DarkSkin.mm;

public class ExpShopGUI {
  private static boolean hasLevel(Player p, int lvl) {
    return p.getLevel() >= lvl;
  }

  /**
   * Sells item to player.
   * Seller: SERVER
   * Buyer: Player p
   * @param p the player buying items
   * @param i the item. the method won't clone itemstack. itemstack's amount must be one (if else changed automatically)
   * @param count the amount of it
   * @param cost item's cost in xp levels
   */
  private static void sell(Player p, ItemStack i, int count, int cost) {
    if (i.getAmount() != 1) i = Utils.withAmount(i, 1);
    if (hasLevel(p, cost)) {
      p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.86f);
      p.setLevel(p.getLevel() - cost);
      for (int j = 0; j < count; j++) {
        Utils.addItem(p, i); // 토템 중첩 문제때문에 이렇게 비효율적이게 함
      }
    }
    else {
      p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 0.86f);
      p.sendMessage(mm.deserialize("<red>레벨이 부족해요!"));
    }
  }

  public static void click(InventoryClickEvent e) {
    if (!(e.getWhoClicked() instanceof Player p)) return;
    e.setCancelled(true);
    if (e.getSlot() == 10) sell(p, Config.getStardust(), 40, 40); //40렙-별가루40개
    else if (e.getSlot() == 12) sell(p, Config.getStarpiece(), 20, 40); //40렙-별조각20개
    else if (e.getSlot() == 14) sell(p, new ItemStack(Material.TOTEM_OF_UNDYING), 2, 40); //40렙-토템2개
    else if (e.getSlot() == 16) sell(p, new ItemStack(Material.BEACON), 1, 100); //100렙-신호기1개
  }

  public static Inventory getInventory(Player p) {
    Inventory inv = Bukkit.createInventory(p, 27, Config.get().EXPSHOP_GUI_TITLE);
    for (int i = 0; i < 27; i++) {
      if (Set.of(10, 12, 14, 16).contains(i)) continue;
      inv.setItem(i, Config.getFiller());
    }
    inv.setItem(10, shopItem(Config.getStardust(), 40, 40));
    inv.setItem(12, shopItem(Config.getStarpiece(), 20, 40));
    inv.setItem(14, shopItem(new ItemStack(Material.TOTEM_OF_UNDYING), 2, 40));
    inv.setItem(16, shopItem(new ItemStack(Material.BEACON), 1, 100));
    return inv;
  }

  /**
   * Important note: THIS DOES NOT CLONE ITEMSTACK!!
  */
  private static ItemStack shopItem(ItemStack stack, int count, int cost) {
    stack.setAmount(count);
    stack.lore(List.of(Component.text(""), mm.deserialize("<green>경험치: %d레벨".formatted(cost))
      .decoration(TextDecoration.ITALIC, false)));
    return stack;
  }
}
