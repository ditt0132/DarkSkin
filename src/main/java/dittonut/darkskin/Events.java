package dittonut.darkskin;

import static dittonut.darkskin.DarkSkin.mm;
import static dittonut.darkskin.DarkSkin.sr;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;

public class Events implements Listener {
  @EventHandler
  public void onLoot(LootGenerateEvent e) {
    e.getLoot().replaceAll(i -> i.getType() == Material.ENCHANTED_BOOK ? PylonGUI.getDailyReward() : i); //If item is enchantedbook: set item to daily reward
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    Location location = e.getBlockPlaced().getLocation();
    if (e.getBlockPlaced().getState() instanceof Sign && !(Math.abs(location.getX()) <= 25 && Math.abs(location.getZ()) <= 25)) {

    }
  }

  @EventHandler
  public void onKill(EntityDeathEvent e) {
    if (e.getEntityType() == EntityType.WARDEN) {
      e.getDrops().clear();
      e.getDrops().add(Enums.getObsipotion());
    }
  }

  @EventHandler
  public void onChat(AsyncChatEvent e) {
        e.setCancelled(true);
        e.getPlayer().getLocation().getNearbyPlayers(25).forEach(p -> {
        p.sendMessage(mm.deserialize("<dark_green>[%.0f] <reset>"
          .formatted(p.getLocation().distance(e.getPlayer().getLocation())))
        //TODO: add name if needed
        .append(e.message()));
    });
    e.viewers().removeIf(p -> )
  }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().title().equals(Enums.ENCHANT_GUI_TITLE)) {
            EnchantGUI.click(e);
        }
    }
    
    @EventHandler
    public void onUseContainer(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(DarkSkin.getInstance(), () -> e.getPlayer().openInventory(EnchantGUI.getInventory((Player) e.getPlayer())));
        } else if (e.getView().title().equals(Component.text("DebugChest")) && e.getPlayer().isOp()) {
      e.setCancelled(true);
      Inventory inv = Bukkit.createInventory(e.getPlayer(), 54);
      List<ItemStack> i = new ArrayList<>();
      i.add(Enums.getStardust());
      i.add(Enums.getObsipotion());
      i.add(Enums.getStarpiece());
      inv.addItem(i.toArray(new ItemStack[0]));
      Bukkit.getScheduler().runTask(DarkSkin.getInstance(), () -> e.getPlayer().openInventory(inv));
    }
    }

    @EventHandler
    public void onCloseContainer(InventoryCloseEvent e) {
        if (e.getView().title().equals(Enums.ENCHANT_GUI_TITLE)) {
            ItemStack stack = e.getInventory().getItem(4);
            if (stack == null) return;
            e.getPlayer().getInventory().addItem(stack).forEach((i, item) ->
                    e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item));
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws MineSkinException, DataRequestException  {
        String teamOwner = Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream().filter(t -> t.getName().startsWith("dt.") && t.hasPlayer(e.getPlayer())).findFirst().orElseThrow().getName().substring(3);
        InputDataResult res = sr.getSkinStorage().findOrCreateSkinData(teamOwner).orElseThrow();
        sr.getPlayerStorage().setSkinIdOfPlayer(e.getPlayer().getUniqueId(), res.getIdentifier());
        
    }

  @EventHandler
  public void onPortal(PlayerPortalEvent e) {
    if (!e.getTo().getWorld().getName().equals("nether")) return;
    Location loc = e.getTo().clone();
    loc.setWorld(Bukkit.getWorld("nether_"+Family.getTeam(e.getPlayer()).getName().substring(3)));
    e.setTo(loc);
  }
  
}
