package dittonut.darkskin;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Beacon;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Pylon {

    private static final Collection<PotionEffect> teamEffects = List.of(
            new PotionEffect(PotionEffectType.SPEED, 120, 0, true, true),
            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0, true, true),
            new PotionEffect(PotionEffectType.FAST_DIGGING, 120, 2, true, true)
    );
    private static final Collection<PotionEffect> enemyEffects = List.of(
            new PotionEffect(PotionEffectType.WEAKNESS, 120, 2, true, true),
            new PotionEffect(PotionEffectType.SLOW_DIGGING, 120, 2, true, true),
            new PotionEffect(PotionEffectType.GLOWING, 120, 0, true, true)
    );

    private static final ConcurrentHashMap<UUID, Location> beacons = new ConcurrentHashMap<>();

    // 초기화 메서드
    public static void init() {
        // 필요한 초기화 작업 수행
    }

    // Update beacon locations and effects every minute
    public static void updateBeacon() {
        beacons.forEach((pid, loc) -> {
            if (!(loc.getBlock() instanceof Beacon)) {
                beacons.remove(pid);
                return;
            }

            // Check if the beacon is below sea level
            if (loc.getY() <= loc.getWorld().getSeaLevel()) {
                forceLoadChunk(loc);
            } else {
                beacons.remove(pid);
                destroyBeacon(loc);
            }
        });
    }

    // Apply effects every 5 seconds
    public static void applyEffects() {
        beacons.forEach((pid, loc) -> {
            if (!(loc.getBlock().getState() instanceof Beacon b)) {
                beacons.remove(pid);
                return;
            }
            loc.getNearbyPlayers(b.getEffectRange()).forEach(p -> {
                if (FamilyUtil.getTeam(p).getName().equals(FamilyUtil.getTeam(pid).getName())) {
                    p.addPotionEffects(teamEffects);
                } else {
                    p.addPotionEffects(enemyEffects);
                }
            });
        });
    }

    // Get the location of a pylon for a given player
    public static @Nullable Location pylonLocationOf(OfflinePlayer p) {
        return beacons.get(FamilyUtil.getOwnerByMember(p).getUniqueId());
    }

    // Handle beacon destruction
    public static void destroyBeacon(Location loc) {
        loc.getBlock().setType(Material.AIR);
        // Unload the chunk when the beacon is destroyed
        unloadChunk(loc);
        // Send a message to Discord or any other platform as needed
        // Apply penalties if necessary
    }

    // Add beacon to the list upon installation
    public static void addBeacon(UUID pid, Location loc) {
        if (canPlaceBeacon(loc)) {
            beacons.put(pid, loc);
            forceLoadChunk(loc);
            // Additional logic for adding to list
        }
    }

    // Deactivate beacon
    public static void deactivateBeacon(Location loc) {
        if (loc.getBlock().getState() instanceof Beacon b) {
            b.setPrimaryEffect(null);
            b.setSecondaryEffect(null);
        }
    }

    // Prevent double beacon installation
    public static boolean canPlaceBeacon(Location loc) {
        return loc.getY() <= loc.getWorld().getSeaLevel() && !beacons.containsValue(loc);
    }

    // Force load the chunk where the beacon is located
    private static void forceLoadChunk(Location loc) {
        Chunk chunk = loc.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        chunk.setForceLoaded(true);
    }

    // Unload the chunk where the beacon was located
    private static void unloadChunk(Location loc) {
        Chunk chunk = loc.getChunk();
        if (chunk.isForceLoaded()) {
            chunk.setForceLoaded(false);
        }
        if (chunk.getEntities().length == 0) {
            chunk.unload();
        }
    }
}