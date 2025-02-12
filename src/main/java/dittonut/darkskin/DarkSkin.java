package dittonut.darkskin;

import org.bukkit.*;
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
    public static final MiniMessage mm = MiniMessage.miniMessage();
    public static DarkSkin instance;
    public static ProtocolManager pm;
    public static SkinsRestorer sr;

    public static DarkSkin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        sr = SkinsRestorerProvider.get();
        pm = ProtocolLibrary.getProtocolManager();

        Bukkit.getPluginCommand("close-end").setExecutor(new Commands());
        Bukkit.getPluginCommand("open-end").setExecutor(new Commands());
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (!t.getName().startsWith("dt.")) return;
            if (Bukkit.getWorld("nether_" + t.getName()) != null) return;
            Bukkit.createWorld(new WorldCreator("nether_" + t.getName()).environment(World.Environment.NETHER).generator("minecraft:nether"));
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getOnlinePlayers().forEach(p -> {
            if (!Enums.patrolers.contains(p.getUniqueId()) && p.getInventory().contains(Material.ELYTRA)) {
                p.sendMessage(mm.deserialize("<red>금지된 아이템을 소지 중이에요! (ELYTRA)"));
                getLogger().info("[BanItem] %s have ELYTRA!".formatted(p.getName()));
                p.getInventory().remove(Material.ELYTRA);
            }
        }), 0L, 1200L);

        Bukkit.getScheduler().runTaskTimer(this, Pylon::updateBeacon, 0L, 1200L); //1분마다 신호기를 업데이트해요!
        Bukkit.getScheduler().runTaskTimer(this, Pylon::applyEffects, 0L, 100L); //5초마다 신호기 효과 적용!
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Enums.rewarded.clear();
            Bukkit.broadcast(mm.deserialize("일일 보상이 리셋됐어요!"));
        }, 0L, 72000L); //1시간마다 보상을 리셋해요!!

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.getWorld().getEnvironment() == Environment.NETHER)
                    .forEach(p -> p.setFireTicks(20));
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
