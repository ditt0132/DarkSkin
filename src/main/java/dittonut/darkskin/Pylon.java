package dittonut.darkskin;

import java.util.Collection;
import java.util.List;

import org.bukkit.block.Beacon;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
  // see main class, called every 1min
  public static void updateBeacon() {
    Variables.beacons.forEach((pid, pair) -> {
      if (!(pair.getLeft().getBlock() instanceof Beacon b)) { //혹시 몰라요 blockBreakEvent가 일 안할지
        Variables.beacons.put(pid, null); return;
      }
      // todo: check pyramid level
      // update its range
    });
  }

  //see main class, called every 5 sec
  public static void applyEffects() { // pid는 팀장의 UUID라는거 잊지말기!
    Variables.beacons.forEach((pid, pair) -> {
      pair.getLeft().getNearbyPlayers(pair.getRight()).forEach(p -> {
        if (Family.getTeam(p).getName().equals(Family.getTeam(pid).getName())) p.addPotionEffects(teamEffects);
        else p.addPotionEffects(enemyEffects);
      });
    });
  }
}
