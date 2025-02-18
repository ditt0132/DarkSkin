package dittonut.darkskin;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;

import java.io.IOException;
import java.util.Random;

public class DarkSkin extends JavaPlugin {
  public static final MiniMessage mm = MiniMessage.miniMessage();
  public static DarkSkin instance;
  public static ProtocolManager pm;
  public static SkinsRestorer sr;
  public static String quizAnswer;
  public static Random r = new Random();
  private ConfigManager configManager;

  public static DarkSkin getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;
    sr = SkinsRestorerProvider.get();
    pm = ProtocolLibrary.getProtocolManager();

//    try {
//      configManager = new ConfigManager(getDataFolder());
//    } catch (IOException e) {
//      getLogger().severe("설정 파일을 로드하는 중 오류 발생: " + e.getMessage());
//      Bukkit.getPluginManager().disablePlugin(this);
//      return;
//    }
    Config.load();

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
      if (!Config.get().patrollers.contains(p.getUniqueId()) && p.getInventory().contains(Material.ELYTRA)) {
        p.sendMessage(mm.deserialize("<red>금지된 아이템을 소지 중이에요! (ELYTRA)"));
        getLogger().info("[BanItem] %s have ELYTRA!".formatted(p.getName()));
        p.getInventory().remove(Material.ELYTRA);
      }
    }), 0L, 1200L);

    Bukkit.getScheduler().runTaskTimer(this, Pylon::updateBeacon, 0L, 1200L); //1분마다 신호기를 업데이트해요!
    Bukkit.getScheduler().runTaskTimer(this, Pylon::applyEffects, 0L, 100L); //5초마다 신호기 효과 적용!
    Bukkit.getScheduler().runTaskTimer(this, () -> {
      Config.get().rewarded.clear();
      Bukkit.broadcast(mm.deserialize("일일 보상이 리셋됐어요!"));
    }, 0L, 72000L); //1시간마다 보상을 리셋해요!!

    Bukkit.getScheduler().runTaskTimer(this, () ->
      Bukkit.getOnlinePlayers().stream()
        .filter(p -> p.getWorld().getEnvironment() == Environment.NETHER)
        .forEach(p -> p.setFireTicks(20)), 0L, 20L);

    Bukkit.getScheduler().runTaskTimer(this, () -> {
      System.out.println("quizstarted");
      if (Bukkit.getOnlinePlayers().isEmpty()) return;
      int idx = r.nextInt(3);
      if (idx == 0) {
        //TODO: 채팅 빨리치기 -- 아주 긴 인용구들? -- 햄릿 오픈소스로 된거 하나 불러오고 앞뒤 trim하고 15글자 이상 30글자 미만 나올떄까지 반복
        // TODO - TEST
        quizAnswer = RandomStringUtils.randomAscii(RandomUtils.nextInt(5, 15));
        Bukkit.broadcast(mm.deserialize("타자대결! <green>%s</green>\n채팅에 최대한 빠르게 입력해주세요!"
          .formatted(quizAnswer)));
        // TEST END
        quizAnswer = ""; //get from hamlet.txt
        Bukkit.broadcast(mm.deserialize("타자대결! <green>%s</green>\n채팅에 최대한 빠르게 입력해주세요!"
          .formatted(quizAnswer)));
      } //else -- 왜없엤냐면 테스트하려고
      if (idx == 1) {
        quizAnswer = RandomStringUtils.randomAscii(RandomUtils.nextInt(5, 15));
        Bukkit.broadcast(mm.deserialize("타자대결! <green>%s</green>\n채팅에 최대한 빠르게 입력해주세요!"
          .formatted(quizAnswer)));
      } else {
        int a = r.nextInt(100);
        int b = r.nextInt(100);
        Bukkit.broadcast(mm.deserialize("퀴즈! <green>%d × %d = ?</green>\n채팅에 정답을 입력해주세요!"
          .formatted(a, b)));
        quizAnswer = String.valueOf(a * b);
      }

      Bukkit.getScheduler().runTaskLater(this, () -> {
        if (quizAnswer.isEmpty()) return; //누가 이미 퀴즈를 맞추면 answer 지워짐
        quizAnswer = "";
        if (quizAnswer.matches("\\d+"))
          Bukkit.broadcast(mm.deserialize("퀴즈가 끝났어요! 정답: <green>%s".formatted(quizAnswer)));
        else Bukkit.broadcast(mm.deserialize("타자대결이 끝났어요! 그 누구도 성공하지 못했어요..."));
      }, 3000L); // 15초   TODO: 150초로 늘림 because 너무 빨리 끝남;;
    }, 0L, 6000L); //15분 뒤에 실행, 그 후 15분마다 퀴즈 출제! TODO test = 5분

    getLogger().info("Enabled!");
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    Pylon.updateBeacon(); //혹시 몰라요
    Config.save();
    getLogger().info("Disabled!");
  }

  public String getTeamName(Player player) {
    return Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream().filter(t -> t.hasPlayer(player)).findFirst().orElseThrow().getName();
  }
}
