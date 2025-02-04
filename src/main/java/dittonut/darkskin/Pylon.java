package dittonut.darkskin;

import org.bukkit.block.Beacon;

public class Pylon {
  // see main class, called every 1min
  public static void updateBeacon() {
    Variables.beacons.forEach((pid, pair) -> {
      if (!(pair.getLeft().getBlock() instanceof Beacon b)) { //혹시 모름
        Variables.beacons.put(pid, null); return;
      }
      // todo: check pyramid level
      // update its range
    });
  }

  //see main class, called every 5 sec
  public static void applyEffects() {
    Variables.beacons.forEach((pid, pair) -> {
      pair.getLeft().getNearbyPlayers(pair.getRight()).
    })
  }
}
