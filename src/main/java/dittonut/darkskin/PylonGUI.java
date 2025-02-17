package dittonut.darkskin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

import static dittonut.darkskin.DarkSkin.mm;

public class PylonGUI {
    private static final Random r = new Random();
    private static final CommandSender quietSender = Bukkit.createCommandSender((ignored) -> {});

    /**
     * Note: this not automatically add players to rewarded list caller must do it
     */
    public static ItemStack getDailyReward() {
        int amount = 1;
// 이 버튼 누르면 보상주고 그 리스트에 추가됨
        // 보상 확률: 기본적으로 확정한개
        // 65% 별가루 나머지 별조각
        // 별가루: (독립적 확률이 돌아감)
        //   50% 별가루한개더
        //   25% 별가루한개
        //별조각:
        //   45% 벌조각한개
        //   15% 별조각한개더
        //
        // 10% 별조각/별가루랜덤 10개 더
        // 이건 서브.. 메인은 지상 메테오 파밍
        if (RandomUtils.nextDouble(0.0, 1.0) < 0.1) amount += 10; //10%
        if (RandomUtils.nextDouble(0.0, 1.0) < 0.65) { //65% 별가루
            if (RandomUtils.nextDouble(0.0, 1.0) < 0.5) amount++; //50%
            if (RandomUtils.nextDouble(0.0, 1.0) < 0.25) amount++; //25%
            return Utils.withAmount(Config.getStardust(), amount);
        } else { //35% 별조각
            if (RandomUtils.nextDouble(0.0, 1.0) < 0.35) amount++; //35%
            if (RandomUtils.nextDouble(0.0, 1.0) < 0.15) amount++; //15%
            return Utils.withAmount(Config.getStarpiece(), amount);
        }
    }

    public static Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, 27, Config.get().PYLON_GUI_TITLE);
        ItemStack item = new ItemStack(Config.get().FILLER_ITEM);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(Config.get().FILLER_MODEL);
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i == 4) continue;
            inv.setItem(i, item.clone());
        }
        return inv;
    }

    public static void spawnClick(InventoryClickEvent e) {

    }

//    public static void reviveClick(InventoryClickEvent e) {
////        //The gui must have 9 slots, and skulls with player skin. can get name from skullowner
////        if (e.getCurrentItem().getType() == Material.PLAYER_HEAD
////                && e.getCurrentItem().getItemMeta() instanceof SkullMeta meta) {
////            //: consume here
////            ItemStack item = e.getCurrentItem(); //note: set needed
////            DeathManager.revive(meta.getOwningPlayer());
////            // show message, totem effect
////            Bukkit.getScheduler().runTask(DarkSkin.getInstance(), () -> e.getView().close());
////        }
//        // 더티가 해주겠지? revivegui <uuid>
//
//    }

    public static void click(InventoryClickEvent e) { //TODO: IMPORTANT! check about patrol firework
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getCurrentItem() != null
                && e.getCurrentItem().getItemMeta().hasCustomModelData()
                && e.getCurrentItem().getItemMeta().getCustomModelData() == Config.get().FILLER_MODEL) {
        } else if (e.getSlot() == 10) { //소환,부활,경험치상점,별조각 플탐보상
            //소헌 letsgo
            //Player selection required btw
            //see event handling at this#spawnClick method
            //TODO: make this
            //TODO: Find how many needed, it will be on playing
        } else if (e.getSlot() == 12) {
            // TODO: 더티가 해줄거야
            Bukkit.dispatchCommand(quietSender, "revivegui %s".formatted(e.getWhoClicked().getUniqueId().toString()));
        } else if (e.getSlot() == 14) {
            Bukkit.getScheduler().runTask(DarkSkin.getInstance(), () -> p.openInventory(ExpShopGUI.getInventory(p)));
        } else if (e.getSlot() == 16) {
            if (!Config.get().rewarded.contains(p.getUniqueId()))
                Utils.addItem(p, getDailyReward());
            else {
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 0.86f);
                p.sendMessage(mm.deserialize("<red>이미 접속 보상을 받았어요!"));
            }
        }
        e.setCancelled(true);
    }

    private static boolean isEnchantable(ItemStack stack) {
        return Arrays.stream(Enchantment.values())
                .anyMatch(ench -> ench.canEnchantItem(stack));
    }

    private static @NotNull Enchantment randomEnchant(Enchantment... filtered) {
        List<Enchantment> enchantments =
                Arrays.stream(Enchantment.values())
                        .filter(e -> !Arrays.asList(filtered).contains(e)).toList(); // I think return shouldnt null..
        return enchantments.get(r.nextInt(enchantments.size()));
    }

    private static boolean containsAtLeast(Inventory inventory, Material mat, int amount) {
        return inventory.all(mat).values().stream()
                .mapToInt(ItemStack::getAmount)
                .sum() >= amount;
    }
}
