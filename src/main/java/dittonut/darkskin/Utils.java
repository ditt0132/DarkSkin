package dittonut.darkskin;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
  public static boolean addItem(Player p, ItemStack item) { //TODO: change this somewhere used, on enchant gui return item
    HashMap<Integer, ItemStack> left = p.getInventory().addItem(item);
    if (!left.isEmpty()) {
      left.forEach((i, stack) -> p.getWorld().dropItem(p.getLocation(), stack));
      return false;
    } else return true;
  }

  /**
   * Nite: THIS DOESNT CLONE ITEMSTACK!!
  */
  public static ItemStack withAmount(ItemStack stack, int amount) {
    stack.setAmount(amount);
    return stack;
  }
}
