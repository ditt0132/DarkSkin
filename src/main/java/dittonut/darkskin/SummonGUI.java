package dittonut.darkskin;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import static dittonut.darkskin.DarkSkin.mm;


// FIXME: 기능 구현하다 맘. 수동으로 뽑죠 그냥
public class SummonGUI {

    public static void click(InventoryClickEvent e) {

    }

    public static Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, 27);
        for (int i = 0; i < 27; i++) {
            if (Set.of(10, 12, 14, 16).contains(i)) continue;
            inv.setItem(i, Config.getFiller());
        }
        inv.setItem(10, shopItem(Config.getStardust(), 40, 40));
        inv.setItem(10, shopItem(Config.getStarpiece(), 20, 40));
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
