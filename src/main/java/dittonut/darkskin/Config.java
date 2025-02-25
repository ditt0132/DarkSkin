package dittonut.darkskin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
  // TODO: test this
  private static Config instance;
  private static final MiniMessage mm = MiniMessage.miniMessage();
  private final File file;
  private final FileConfiguration config;

  // NO SAVE
  public static boolean enableEnd = false;

  public Component ENCHANT_GUI_TITLE = mm.deserialize("<#F40001><reset>마법 부여");
  public Component PYLON_GUI_TITLE = mm.deserialize("<#F40002><reset>파일런");
  public Component EXPSHOP_GUI_TITLE = mm.deserialize("<#F40003><reset>경험치 상점");
  public int MAX_ENCHANTMENTS = 10;
  public int ENCHANT_ADD_CHANCE = 10;
  public Component STARDUST_NAME = mm.deserialize("<gold>별가루");
  public Material FILLER_ITEM = Material.GRAY_STAINED_GLASS_PANE;
  public int FILLER_MODEL = 1013145;
  public NamespacedKey PDC_KEY = new NamespacedKey("darkforest", "custom");
  public Component STARPIECE_NAME = mm.deserialize("<light_purple>별조각");
  public Component OBSI_POTION_NAME = mm.deserialize("<light_purple>흑요석 물약");
  public Set<UUID> patrollers = new HashSet<>();
  public Set<UUID> rewarded = new HashSet<>();
  public Map<UUID, Location> beacons = new HashMap<>();
  public Set<Chunk> forceLoads = new HashSet<>(); //TODO: update!
  public Set<UUID> banned = new HashSet<>();

  private Config() {
    this.file = new File(DarkSkin.getInstance().getDataFolder(), "config.yml");
    this.config = YamlConfiguration.loadConfiguration(file);
  }

  public static Config get() {
    if (instance == null) {
      throw new IllegalStateException("Config is not loaded yet!");
    }
    return instance;
  }

  public static void load() {
    if (instance != null) {
      Bukkit.getLogger().severe("Config is already loaded!");
      Bukkit.getPluginManager().disablePlugin(DarkSkin.getInstance());
      return;
    }
    instance = new Config();
    instance.loadValues();
    instance.startAutoSave();
  }

  private void loadValues() {
    ENCHANT_GUI_TITLE = mm.deserialize(config.getString("enchant_gui_title", "<#F40001><reset>마법 부여"));
    PYLON_GUI_TITLE = mm.deserialize(config.getString("pylon_gui_title", "<#F40002><reset>파일런"));
    EXPSHOP_GUI_TITLE = mm.deserialize(config.getString("expshop_gui_title", "<#F40003><reset>경험치 상점"));
    MAX_ENCHANTMENTS = config.getInt("max_enchantments", 10);
    ENCHANT_ADD_CHANCE = config.getInt("enchant_add_chance", 10);
    STARDUST_NAME = mm.deserialize(config.getString("stardust_name", "<gold>별가루"));
    FILLER_ITEM = Material.valueOf(config.getString("filler_item", "GRAY_STAINED_GLASS_PANE"));
    FILLER_MODEL = config.getInt("filler_model", 1013145);
    PDC_KEY = new NamespacedKey("darkforest", "custom");
    STARPIECE_NAME = mm.deserialize(config.getString("starpiece_name", "<light_purple>별조각"));
    OBSI_POTION_NAME = mm.deserialize(config.getString("obsi_potion_name", "<light_purple>흑요석 물약"));

    patrollers.clear();
    for (String uuid : config.getStringList("patrollers")) {
      patrollers.add(UUID.fromString(uuid));
    }

    rewarded.clear();
    for (String uuid : config.getStringList("rewarded")) {
      rewarded.add(UUID.fromString(uuid));
    }

    banned.clear();
    for (String uuid : config.getStringList("banned")) {
      banned.add(UUID.fromString(uuid));
    }

    beacons.clear();
    ConfigurationSection beaconsMap = config.getConfigurationSection("beacons");
    for (Map.Entry<String, Object> entry : config.getConfigurationSection("beacons").getValues(false).entrySet()) {
      String key = entry.getKey();
      Location loc = new Location(
        Bukkit.getWorld(beaconsMap.getString(key+".world")),
        beaconsMap.getDouble(key+"x"),
        beaconsMap.getDouble(key+"y"),
        beaconsMap.getDouble(key+"z")
      );
      beacons.put(UUID.fromString(key), loc);
    }
  }

  public static void save() {
    if (instance == null) return;
    Bukkit.getScheduler().runTaskAsynchronously(DarkSkin.getInstance(), () -> instance.saveValues());
  }

  private void saveValues() {
    config.set("enchant_gui_title", mm.serialize(ENCHANT_GUI_TITLE));
    config.set("pylon_gui_title", mm.serialize(PYLON_GUI_TITLE));
    config.set("expshop_gui_title", mm.serialize(EXPSHOP_GUI_TITLE));
    config.set("max_enchantments", MAX_ENCHANTMENTS);
    config.set("enchant_add_chance", ENCHANT_ADD_CHANCE);
    config.set("stardust_name", mm.serialize(STARDUST_NAME));
    config.set("filler_item", FILLER_ITEM.name());
    config.set("filler_model", FILLER_MODEL);
    config.set("starpiece_name", mm.serialize(STARPIECE_NAME));
    config.set("obsi_potion_name", mm.serialize(OBSI_POTION_NAME));

    List<String> patrollerList = new ArrayList<>();
    for (UUID uuid : patrollers) {
      patrollerList.add(uuid.toString());
    }
    config.set("patrollers", patrollerList);

    List<String> rewardedList = new ArrayList<>();
    for (UUID uuid : rewarded) {
      rewardedList.add(uuid.toString());
    }
    config.set("rewarded", rewardedList);

    List<String> bannedList = new ArrayList<>();
    for (UUID uuid : banned) {
      bannedList.add(uuid.toString());
    }
    config.set("banned", bannedList);

    ConfigurationSection beaconsMap = config.getConfigurationSection("beacons");
    for (Map.Entry<UUID, Location> entry : beacons.entrySet()) {
      String key = entry.getKey().toString();
      Location loc = entry.getValue();
      beaconsMap.set(key+".world", loc.getWorld().getName());
      beaconsMap.set(key+".x", loc.x());
      beaconsMap.set(key+".y", loc.y());
      beaconsMap.set(key+".z", loc.z());
    }

    try {
      config.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

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

  private void startAutoSave() {
    Bukkit.getScheduler().runTaskTimerAsynchronously(DarkSkin.getInstance(), Config::save, 6000L, 6000L);
  }
}
