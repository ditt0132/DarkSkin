package dittonut.darkskin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class Variables {
    // this does not include riders, only the elytra one
    public static Set<UUID> patrolers = new HashSet<>();
    public static Set<UUID> rewarded = new HashSet<>();
    public static Map<UUID, Pair<Location, Integer>> beacons = new HashMap<>();
    public static Set<Chunk> forceloads = new HashSet<>();
}
