package dittonut.darkskin;

import static dittonut.darkskin.DarkSkin.mm;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;

import java.util.*;

public class Enums {
    // this does not include riders, only the elytra one

    /** no save */
    public static boolean enableEnd = false;
    public static Set<UUID> patrolers = new HashSet<>();
    public static Set<UUID> rewarded = new HashSet<>();
    // TODO HUGE REFACTOR, the UUID says the team owner's
    public static Map<UUID, Location> beacons = new HashMap<>();
    public static Set<Chunk> forceloads = new HashSet<>();
    public static Set<UUID> banned = new HashSet<>();



    // TODO: make private ItemStacks and methods only clone and return them. Item is generated by static block
    // TODO: needs to be finalized
    public static Component ENCHANT_GUI_TITLE = mm.deserialize("<#F40001><reset>마법 부여");
    public static Component PYLON_GUI_TITLE = mm.deserialize("<#F40002><reset>파일런");
    public static int MAX_ENCHANTMENTS = 10;
    public static int ENCHANT_ADD_CHANCE = 10; // 1/n
    public static Component STARDUST_NAME = mm.deserialize("<gold>별가루");
    public static Material FILLER_ITEM = Material.GRAY_STAINED_GLASS_PANE;

    // FIREWORK - 정찰용 폭죽
    public static NamespacedKey PDC_KEY = new NamespacedKey("darkforest", "custom");
    public static Component STARPIECE_NAME = mm.deserialize("<light_purple>별조각");
    public static int FILLER_MODEL = 1013145;
    public static Component OBSI_POTION_NAME = mm.deserialize("<light_purple>흑요석 물약");

    public static ItemStack getStardust() {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(STARDUST_NAME);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getStarpiece() {
        ItemStack item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(STARPIECE_NAME);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getFiller() { //TODO: change everything to use this
        ItemStack item = new ItemStack(FILLER_ITEM);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getFirework() {
        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) item.getItemMeta();
        meta.displayName(mm.deserialize("<red>정찰용 폭죽"));
        meta.lore(List.of(
                mm.deserialize("TODO") //TODO: 아이템 이름 각별보고 꾸미기
        ));
        meta.setPower(64);
        meta.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.STRING, "FIREWORK");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getObsipotion() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(40, 35, 54)); // #282336
        meta.displayName(OBSI_POTION_NAME);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 216000, 0), true);
        item.setItemMeta(meta);
        return item;
    }
}
