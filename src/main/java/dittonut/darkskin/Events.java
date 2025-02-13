package dittonut.darkskin;

import static dittonut.darkskin.DarkSkin.mm;
import static dittonut.darkskin.DarkSkin.sr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
import org.bukkit.scoreboard.Team;

public class Events implements Listener {

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

// 우리 귀여운 더티가 해줄거야
//    @EventHandler
//    public void onJump(PlayerJumpEvent e) {
//        //System.out.println("WTF");
//        if (e.getPlayer().getLocation().add(0, -0.001, 0).getBlock().isCollidable()) e.getPlayer().setAllowFlight(true);
//    }

// 더티더티가 해주겠죠
//    @EventHandler
//    public void onDamage(EntityDamageEvent e) {
//        if (!(e instanceof Player p)) return;
//        if (!(e.getCause() == EntityDamageEvent.DamageCause.FALL)) return;
//        if (p.getInventory().getBoots().isEmpty()) e.setCancelled(true); //여기에 아이템 강화 삽입
//    }
//이것도 더티가 해주겠죠
//    @EventHandler
//    public void onFlight(PlayerToggleFlightEvent e) {
//        Player p = e.getPlayer();
//        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
//        Vector direction = p.getLocation().getDirection();
//        Vector knockback = new Vector(direction.getX(), 0, direction.getZ()).normalize();//.multiply(1);
//        knockback.setY(0.60);
//        p.setVelocity(knockback);
//        p.setAllowFlight(false);
//        Bukkit.getScheduler().runTask(DarkSkin.getInstance(), () -> {
//            p.setFlying(false);
//            //e.getPlayer().setAllowFlight(true);
//        }); //
//    }

  private static final double MAX_DISTANCE_SQUARED = 25.0d * 25.0d;

  @EventHandler
  public void onVillager(PlayerTradeEvent e) {
    if (e.getTrade().getResult().getType() == Material.ENCHANTED_BOOK) {
      e.setCancelled(true);
      e.getPlayer().sendMessage(mm.deserialize("<red>금지된 거래예요! [ENCHANTED_BOOK]"));
    }
  }

  @EventHandler
  public void onBreak(BlockBreakEvent e) {
    if (e.getBlock().getType().name().contains("ORE")) {
      e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), PylonGUI.getDailyReward());
    }
  }

  @EventHandler
  public void onLoot(LootGenerateEvent e) {
    e.getLoot().replaceAll(i -> i.getType() == Material.ENCHANTED_BOOK ? PylonGUI.getDailyReward() : i); //If item is enchantedbook: set item to daily reward
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    Location location = e.getBlockPlaced().getLocation();
    if (e.getBlockPlaced().getState() instanceof Sign && !(Math.abs(location.getX()) <= 25 && Math.abs(location.getZ()) <= 25)) {
      e.getPlayer().sendMessage(mm.deserialize("<red>표지판은 0, 0 근처 25블록 이내에만 설치할 수 있어요!"));
      e.setCancelled(true);
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
      // 보상 뿌리기

      Bukkit.getScheduler().runTask(DarkSkin.getInstance(), () -> {
        end.getPlayers().forEach(p -> {
          // 죽을 시 침대 또는 파일런 또는 월드 스폰으로 이동
          Location home = p.getBedSpawnLocation();
          if (home == null) home = Pylon.pylonLocationOf(p);
          if (home == null) home = overworld.getSpawnLocation();
          p.teleport(home);
        });
        //
      });
    }
  }


  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player sender = event.getPlayer();
    event.viewers().removeIf(audience -> {
        if (!(audience instanceof Player player)) return false;
        return !player.getWorld().equals(sender.getWorld()) ||
          player.getLocation().distanceSquared(sender.getLocation()) > MAX_DISTANCE_SQUARED;
      }
    );

    event.renderer((source, sourceDisplayName, message, viewer) -> {
      if (!(viewer instanceof Player player)) return message;
      double distance = player.getLocation().distance(sender.getLocation());
      Component prefix = mm.deserialize(String.format("<dark_green>[%dm]</dark_green> ", (int) distance));
      return prefix.append(sender.displayName())
        .append(Component.text(": "))
        .append(message);
    });
  }

  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if (e.getView().title().equals(Config.get().ENCHANT_GUI_TITLE)) EnchantGUI.click(e);
    else if (e.getView().title().equals(Config.get().EXPSHOP_GUI_TITLE)) ExpShopGUI.click(e);
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
        ()-> e.getPlayer().openInventory(ExpShopGUI.getInventory((Player) e.getPlayer())));
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
    if (!e.getTo().getWorld().getName().equals("nether")) return;
    Location loc = e.getTo().clone();
    loc.setWorld(Bukkit.getWorld("nether_" + FamilyUtil.getTeam(e.getPlayer()).getName().substring(3)));
    e.setTo(loc);
  }
}
