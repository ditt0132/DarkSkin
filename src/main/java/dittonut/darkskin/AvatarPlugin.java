package dittonut.darkskin;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AvatarPlugin extends JavaPlugin {

    private final Map<UUID, ArmorStand> playerAvatars = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onEnable() {
        // 이벤트 리스너
        getServer().getPluginManager().registerEvents(new TeamListener(this), this);
        getLogger().info("AvatarPlugin has been enabled");
    }

    @Override
    public void onDisable() {
        // 비활성화 로직ㄱ
        for (ArmorStand avatar : playerAvatars.values()) {
            avatar.remove();
        }
        playerAvatars.clear();
        getLogger().info("AvatarPlugin has been disabled");
    }

    // 팀 이벤트 리스너 클래스
    class TeamListener implements Listener {

        private final AvatarPlugin plugin;

        public TeamListener(AvatarPlugin plugin) {
            this.plugin = plugin;
        }

        // 서버 접속 호출
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();

            // 소속 확인, 팀 아바타 적용
            Team team = FamilyUtil.getTeam(playerId);

            if (team != null) {
                String teamName = team.getName();
                applyAvatar(player, teamName);
            }

            // 접속하면 기존 아바타 제거
            ArmorStand avatar = playerAvatars.remove(playerId);
            if (avatar != null) {
                avatar.remove();
            }
        }

        // 나갈때 호출
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            layDownAvatar(player);
        }

        // 죽을때 호출
        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();
            dropPlayerItems(player);
            banPlayer(player);
        }

        // 뎀지 입을때 호출
        @EventHandler
        public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
            if (event.getEntity() instanceof ArmorStand) {
                ArmorStand avatar = (ArmorStand) event.getEntity();
                if (playerAvatars.containsValue(avatar)) {
                    if (event.getDamager() instanceof Player) {
                        avatar.setHealth(avatar.getHealth() - event.getDamage());
                    }
                }
            }
        }

        // 적용
        private void applyAvatar(Player player, String teamName) {
            // 로직 추가
        }

        // 아이템 드롭
        private void dropPlayerItems(Player player) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
            player.getInventory().clear();
        }

        // 48시간 벤
        private void banPlayer(Player player) {
            String username = player.getName();
            Bukkit.getBanList(BanList.Type.NAME).addBan(username, "사망하였습니다. 48시간동안 벤 당하셨습니다.", null, null);
            scheduler.schedule(() -> Bukkit.getBanList(BanList.Type.NAME).pardon(username), 48, TimeUnit.HOURS);
            player.kickPlayer("사망하였습니다. 48시간동안 벤 당하셨습니다.");
        }
    }

    // 플레이어 아바타 불러오기
    public void loadAvatarsByOwner(String ownerName) {
        Team team = FamilyUtil.getByOwner(ownerName);
        if (team != null) {
            for (String entry : team.getEntries()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(entry);
                if (player.isOnline()) {
                    layDownAvatar(player.getPlayer());
                }
            }
        }
    }

    // 나갈때 아바타 눕게 만들기
    private void layDownAvatar(Player player) {
        ArmorStand avatar = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        avatar.setCustomName(player.getName() + "'s Avatar");
        avatar.setCustomNameVisible(true);
        avatar.setGravity(false);
        avatar.setVisible(false);
        avatar.setHealth(20.0);

        playerAvatars.put(player.getUniqueId(), avatar);
    }
}