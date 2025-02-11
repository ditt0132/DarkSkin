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
    Enums.beacons.forEach((pid, loc) -> {
      if (!(loc.getBlock() instanceof Beacon b)) { //혹시 몰라요 blockBreakEvent가 일 안할지
        Enums.beacons.put(pid, null); return;
      }
      // todo: make beacon ranges chunk load. do it as updates
    });
  }

  //see main class, called every 5 sec
  public static void applyEffects() { // pid는 팀장의 UUID라는거 잊지말기!
    Enums.beacons.forEach((pid, loc) -> {
      if (!(loc.getBlock().getState() instanceof Beacon b)) { //
        Enums.beacons.put(pid, null); //위에 이미 같은 로직이 있긴 해도 해두면 좋으니까요
        return;
      }
      loc.getNearbyPlayers(b.getEffectRange()).forEach(p -> {
        if (FamilyUtil.getTeam(p).getName().equals(FamilyUtil.getTeam(pid).getName())) p.addPotionEffects(teamEffects);
        else p.addPotionEffects(enemyEffects);
      });
    });
  }
}
