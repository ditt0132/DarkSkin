package dittonut.darkskin;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;

public class DarkSkin extends JavaPlugin {
  public static Marker fireDamage;
  public static DarkSkin instance;
    public static ProtocolManager pm;
    public static SkinsRestorer sr;
  public static DarkSkin getInstance() { return instance; }
  public static final MiniMessage mm = MiniMessage.miniMessage();
    @Override
    public void onEnable() {
        instance = this;
        fireDamage = (Marker) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), 0d, 0d, 0d), EntityType.MARKER);
    fireDamage.customName(Component.text("지옥의 열기"));
        for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (!t.getName().startsWith("dt.")) return;
            if (Bukkit.getWorld("nether_"+t.getName()) != null) return;
            Bukkit.createWorld(new WorldCreator("nether_"+t.getName()).environment(World.Environment.NETHER).generatorSettings("NETHER"));
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
        sr = SkinsRestorerProvider.get();
        pm = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        for (World world : Bukkit.getWorlds()) {
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
    }
        Bukkit.getScheduler().runTaskTimer(this, Pylon::updateBeacon, 0L, 1200L); //1분마다 신호기를 업데이트해요!
    Bukkit.getScheduler().runTaskTimer(this, () -> {
      
    }, 0L, 72000L); //1시간마다 보상을 리셋해요!!
    Bukkit.getScheduler().runTaskTimer(this, () -> {
      Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().getEnvironment() == Environment.NETHER).forEach(p -> {
        p.setFireTicks(20);
        p.damage(2, fireDamage);
      });
    }, 0L, 20L);
        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    Pylon.updateBeacon(); //혹시 몰라요
        getLogger().info("Disabled!");
    }

    public String getTeamName(Player player) {
    return Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream().filter(t -> t.hasPlayer(player)).findFirst().orElseThrow().getName();
    }
}
