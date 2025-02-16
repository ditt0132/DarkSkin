package dittonut.darkskin;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
  public static Random r = new Random();
  public static boolean addItem(HumanEntity p, ItemStack item) {
    HashMap<Integer, ItemStack> left = p.getInventory().addItem(item);
    if (!left.isEmpty()) {
      left.forEach((i, stack) -> p.getWorld().dropItem(p.getLocation(), stack));
      return false;
    } else return true;
  }

  /**
   * Note: THIS DOESN'T CLONE ITEMSTACK!!
  */
  public static ItemStack withAmount(ItemStack stack, int amount) {
    stack.setAmount(amount);
    return stack;
  }

public static <T> T getRandom(T[] array) {
    return array[r.nextInt(array.length)];
}

public static Location getRandomLocation(Player player, double min, double max) {
    Location loc = player.getLocation();
    World world = loc.getWorld();

    double angle = Math.random() * Math.PI * 2;
    double distance = min + Math.random() * (max - min);
    double x = loc.getX() + Math.cos(angle) * distance;
    double z = loc.getZ() + Math.sin(angle) * distance;
    double y = world.getHighestBlockYAt((int) x, (int) z) + 1;

    return new Location(world, x, y, z);
}

public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = r.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
