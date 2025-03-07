package dittonut.darkskin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.papermc.paper.event.player.ChatEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import static dittonut.darkskin.DarkSkin.*;

public class Events implements Listener {
  private static final double MAX_DISTANCE = 25.0d;
  public static PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

  @EventHandler
  public void onFirework(PlayerInteractEvent e) {
    if (e.getItem() == null) return;
    if (!(e.getItem().getType() == Material.FIREWORK_ROCKET)) return;
    PersistentDataContainer pdc = e.getItem().getItemMeta().getPersistentDataContainer();
    if (!pdc.has(Config.get().PDC_KEY, PersistentDataType.STRING)
      || !(pdc.get(Config.get().PDC_KEY, PersistentDataType.STRING).equals("FIREWORK"))) return;
    AtomicReference<Player> lastPlayer = new AtomicReference<>(e.getPlayer());

    e.getPlayer().getLocation().getNearbyPlayers(8).stream()
      .filter(p -> !p.equals(e.getPlayer())) // 자기 제외
      .filter(p -> FamilyUtil.getTeam(p).equals(FamilyUtil.getTeam(e.getPlayer()))) // 같은 팀만
      .forEach(p -> {
        lastPlayer.get().addPassenger(p);
        lastPlayer.set(p);
      });
  }

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if (e.getPlayer().getLocation().add(0, -0.001, 0).getBlock().getType() == Material.AIR) return;
    // add players to delayed(1m) with player that landed together (loop nearby 8b, only teams)
  }

  @EventHandler
  public void onVillager(PlayerTradeEvent e) {
    if (e.getTrade().getResult().getType() == Material.ENCHANTED_BOOK) {
      e.setCancelled(true);
      e.getPlayer().sendMessage(mm.deserialize("<red>금지된 거래예요! [ENCHANTED_BOOK]"));
    }
  }

  @EventHandler
  public void onBreak(BlockBreakEvent e) {
//    if (e.getBlock().getType().name().contains("ORE")) {
//      e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), PylonGUI.getDailyReward());
//    } TODO: 상의 후
    if (e.getBlock().getType() == Material.BEACON) {

    }
  }

  @EventHandler
  public void onLoot(LootGenerateEvent e) {
    e.getLoot().replaceAll(i -> i.getType() == Material.ENCHANTED_BOOK ? PylonGUI.getDailyReward() : i); //If item is enchantedbook: set item to daily reward
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    Location location = e.getBlockPlaced().getLocation();
    // 만약 설치블록이 표지판이고 오버월드에 설치한게 아니거나 00기준 +- 25x25 범위 밖인 경우
    // TODO test this
    if (e.getBlockPlaced().getState() instanceof Sign &&
      (!location.getWorld().getName().equals("world") || !(Math.abs(location.getX()) <= 25 && Math.abs(location.getZ()) <= 25))) {
      e.getPlayer().sendMessage(mm.deserialize("<red>표지판은 0, 0 근처 25블록 이내에만 설치할 수 있어요!"));
      e.setCancelled(true);
    } else if (e.getBlockPlaced().getType() == Material.BEACON) { //TODO: test
      // 어지러워서 대충 정리하면:
      // 플레이어가 팀이 있고 가문장인가?
      // NO->설치불가
      // YES->
      // 이미 파일런이 있는가?
      // YES->설치불가
      // NO->설치후 저장
      if (FamilyUtil.hasTeam(e.getPlayer().getUniqueId())
        && FamilyUtil.getOwnerByMember(e.getPlayer()).getUniqueId().equals(e.getPlayer().getUniqueId())) {
        if (Config.get().beacons.containsKey(e.getPlayer().getUniqueId())) {
          e.getPlayer().sendMessage(mm.deserialize("<red>이미 파일런이 있어요!"));
          e.setCancelled(true);
          return;
        }
        e.getPlayer().sendMessage("파일런을 설치했어요!");
        Config.get().beacons.put(e.getPlayer().getUniqueId(), location);
      } else {
        e.getPlayer().sendMessage(mm.deserialize("<red>가문장만 설치할 수 있어요!"));
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onDeath(EntityDeathEvent e) {
    if (e.getEntityType() == EntityType.WARDEN) {
      e.getDrops().clear();
      e.getDrops().add(Config.getObsipotion());
    } else if (e.getEntityType() == EntityType.ENDER_DRAGON) {
      World end = Bukkit.getWorld("world_the_end");
      World overworld = Bukkit.getWorld("world");
      if (end == null || overworld == null)
        throw new IllegalStateException("World 'world_the_end' or 'world' is null!");
      end.sendMessage(mm.deserialize("드래곤이 죽었어요. 엔드 차원이 10분 뒤에 닫혀요!"));
      // TODO: 보상 뿌리기
      Config.enableEnd = false;

      Bukkit.getScheduler().runTaskLater(DarkSkin.getInstance(), () -> {
        end.getPlayers().forEach(p -> {
          // 닫힐 시 침대 또는 파일런 또는 월드 스폰으로 이동
          // TODO
          // FIXME 이거 작동을 안함.
          Location home = p.getBedSpawnLocation();
          if (home == null) home = Pylon.pylonLocationOf(p);
          if (home == null) home = overworld.getSpawnLocation();
          p.teleport(home);
        });
        //
      }, 12000L);
    }
  }

  @EventHandler
  public void onSpawn(EntitySpawnEvent e) {
    if (!(e.getEntityType() == EntityType.ENDER_DRAGON)) return;
    EnderDragon drgn = (EnderDragon) e.getEntity();
    drgn.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000.0);
    drgn.setHealth(1000.0);
  }

  @SuppressWarnings("deprecation") //나는 ASYNC가 싫어ㅓㅓ 컴파일러야 닥쳐^^
  @EventHandler
  public void onChat(ChatEvent e) {
    String msg = plainText.serialize(e.message());
    e.setCancelled(true);
    DarkSkin.getInstance().getLogger().info("<%s> ".formatted(e.getPlayer().getName())
      + msg);
    Location sl = e.getPlayer().getLocation();
    sl.getNearbyPlayers(25.0d).forEach(p -> {
      double dist = p.getLocation().distance(sl);
      p.sendMessage(mm.deserialize("<dark_green>[%dm]<reset> <"
          .formatted((int) dist))
        .append(e.getPlayer().displayName())
        .append(Component.text("> "))
        .append(e.message()));
    });

    if (msg.equals(quizAnswer)) {
      if (msg.matches("\\d+")) Bukkit.broadcast(e.getPlayer().displayName()
        .append(mm.deserialize("<reset>님이 퀴즈를 맞혔어요! 정답: <green>%s".formatted(quizAnswer))));
      else Bukkit.broadcast(e.getPlayer().displayName()
        .append(mm.deserialize("<reset>님이 타자대결을 이겼어요!")));
      quizAnswer = "";
      Utils.addItem(e.getPlayer(), PylonGUI.getDailyReward());
    }
  }

  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if (e.getView().title().equals(Config.get().ENCHANT_GUI_TITLE)) EnchantGUI.click(e);
    else if (e.getView().title().equals(Config.get().EXPSHOP_GUI_TITLE)) ExpShopGUI.click(e);
    else if (e.getView().title().equals(Config.get().PYLON_GUI_TITLE)) PylonGUI.click(e);
  }

  @EventHandler
  public void onUseContainer(InventoryOpenEvent e) {
    if (e.getInventory() instanceof EnchantingInventory) {
      e.setCancelled(true);
      Bukkit.getScheduler().runTask(DarkSkin.getInstance(),
        () -> e.getPlayer().openInventory(EnchantGUI.getInventory((Player) e.getPlayer())));
    } else if (e.getInventory() instanceof BeaconInventory) {
      e.setCancelled(true);
      Bukkit.getScheduler().runTask(DarkSkin.getInstance(),
        () -> e.getPlayer().openInventory(PylonGUI.getInventory((Player) e.getPlayer())));
    } else if (e.getView().title().equals(Component.text("DebugChest")) && e.getPlayer().isOp()) {
      e.setCancelled(true);
      Inventory inv = Bukkit.createInventory(e.getPlayer(), 54);
      List<ItemStack> i = new ArrayList<>();
      i.add(Config.getStardust());
      i.add(Config.getObsipotion());
      i.add(Config.getStarpiece());
      i.add(Config.getFirework());
      inv.addItem(i.toArray(new ItemStack[0]));
      Bukkit.getScheduler().runTask(DarkSkin.getInstance(), () -> e.getPlayer().openInventory(inv));
    } else if (e.getView().title().equals(Component.text("더티더티더티더티")) && e.getPlayer().isOp()) {
      e.setCancelled(true);
      Bukkit.getScheduler().runTask(DarkSkin.getInstance(),
        () -> e.getPlayer().openInventory(ExpShopGUI.getInventory((Player) e.getPlayer())));
    }
  }

  @EventHandler
  public void onCloseContainer(InventoryCloseEvent e) {
    if (e.getView().title().equals(Config.get().ENCHANT_GUI_TITLE)) {
      ItemStack stack = e.getInventory().getItem(4);
      if (stack == null) return;
      Utils.addItem(e.getPlayer(), stack);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) throws MineSkinException, DataRequestException {
    Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream()
      .filter(t -> t.getName().startsWith("dt.") && t.hasPlayer(e.getPlayer())).findFirst().orElse(null);
    if (team == null) return;
    String teamOwner = team.getName().substring(3);
    InputDataResult res = sr.getSkinStorage().findOrCreateSkinData(teamOwner).orElseThrow();
    sr.getPlayerStorage().setSkinIdOfPlayer(e.getPlayer().getUniqueId(), res.getIdentifier());
  }

  @EventHandler
  public void onPortal(PlayerPortalEvent e) {
    if (e.getTo().getWorld().getName().equals("world_the_end")) {
      if (!Config.enableEnd) {
        e.getPlayer().sendMessage(mm.deserialize("<red>엔드가 닫혀있어요!"));
        e.setCancelled(true);
        return;
      } else {
        // -25 to 25, 100, -25 to 25 random tp. Note: this mutates the location
        e.getTo().set(-25.0 + (r.nextDouble() * 50.0), 100.0, -25.0 + (r.nextDouble() * 50.0));
        e.getPlayer().addPotionEffect(new PotionEffect( //FIXME: 이거 1틱 뒤에 해야하나? 안먹음
          PotionEffectType.SLOW_FALLING,
          10,
          0,
          false,
          false,
          false));
        e.setCanCreatePortal(false);
      }
    } else if (!e.getTo().getWorld().getName().equals("world_nether")){
      Location loc = e.getTo().clone();
      World dest = e.getFrom().getWorld().getName().startsWith("nether_dt.")
        ? Bukkit.getWorld("world")
        : Bukkit.getWorld("nether_" + FamilyUtil.getTeam(e.getPlayer()).getName());
      System.out.println(dest.getName());
      loc.setWorld(dest); //포탈 돌아가는 로직 추가
      e.setTo(loc);
    }
  }
}
