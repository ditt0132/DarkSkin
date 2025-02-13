package dittonut.darkskin;

import static dittonut.darkskin.DarkSkin.mm;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

@ConfigSerializable
public class Config {
  private static Config instance;
  private static YamlConfigurationLoader loader;
  private static File configFile;

  public static void load() {
    configFile = new File(DarkSkin.getInstance().getDataFolder(), "config.yml");
    loader = YamlConfigurationLoader.builder().file(configFile).build();

    try {
      CommentedConfigurationNode root = loader.load();
      instance = root.get(Config.class, new Config());
      save(); // 기본값을 유지
    } catch (IOException e) {
      System.err.println("An error occurred while loading this configuration: " + e.getMessage());
      if (e.getCause() != null) {
        e.getCause().printStackTrace();
      }
      Bukkit.getPluginManager().disablePlugin(DarkSkin.getInstance());
    }
  }

  public static void save() {
    try {
      CommentedConfigurationNode root = loader.load();
      root.set(Config.class, instance);
      loader.save(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Config get() {
    return instance;
  }

  public static boolean enableEnd = false;

  @Setting
  @Comment("GUI 이름. 유저가 따라할 수 없게 컬러 코드를 넣는걸 추천해요!")
  public Component ENCHANT_GUI_TITLE = mm.deserialize("<#F40001><reset>마법 부여");

  @Setting
  public Component PYLON_GUI_TITLE = mm.deserialize("<#F40002><reset>파일런");

  @Setting
  public Component EXPSHOP_GUI_TITLE = mm.deserialize("<#F40003><reset>경험치 상점");


  @Setting
  public int MAX_ENCHANTMENTS = 10;

  @Setting
  public int ENCHANT_ADD_CHANCE = 10;

  @Setting
  public Component STARDUST_NAME = mm.deserialize("<gold>별가루");

  @Setting
  public Material FILLER_ITEM = Material.GRAY_STAINED_GLASS_PANE;

  @Setting
  public int FILLER_MODEL = 1013145;


  @Setting
  public NamespacedKey PDC_KEY = new NamespacedKey("darkforest", "custom");

  @Setting
  public Component STARPIECE_NAME = mm.deserialize("<light_purple>별조각");



  @Setting
  public Component OBSI_POTION_NAME = mm.deserialize("<light_purple>흑요석 물약");

  @Setting
  @Comment("정찰자 목록 (UUID)")
  public Set<UUID> patrollers = new HashSet<>();

  @Setting
  @Comment("일일보상을 받은 유저 목록 (UUID)")
  public Set<UUID> rewarded = new HashSet<>();

  @Setting
  @Comment("파일런 위치 (팀장 UUID) -> 위치")
  public Map<UUID, Location> beacons = new HashMap<>();

  @Setting
  @Comment("로딩되는 청크 -- 사용되지 않음")
  public Set<Chunk> forceLoads = new HashSet<>();

  @Setting
  @Comment("죽은 사람들 목록 (UUID)")
  public Set<UUID> banned = new HashSet<>();

  public static ItemStack getStardust() {
    ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(get().STARDUST_NAME);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack getStarpiece() {
    ItemStack item = new ItemStack(Material.ECHO_SHARD);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(get().STARPIECE_NAME);
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack getFiller() {
    ItemStack item = new ItemStack(get().FILLER_ITEM);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(""));
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack getFirework() {
    ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
    FireworkMeta meta = (FireworkMeta) item.getItemMeta();
    meta.displayName(mm.deserialize("<red>정찰용 폭죽"));
    meta.lore(List.of(mm.deserialize("TODO")));
    meta.setPower(64);
    meta.getPersistentDataContainer().set(get().PDC_KEY, PersistentDataType.STRING, "FIREWORK");
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack getObsipotion() {
    ItemStack item = new ItemStack(Material.POTION);
    PotionMeta meta = (PotionMeta) item.getItemMeta();
    meta.setColor(Color.fromRGB(40, 35, 54));
    meta.displayName(get().OBSI_POTION_NAME);
    meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 216000, 0), true);
    item.setItemMeta(meta);
    return item;
  }
}
