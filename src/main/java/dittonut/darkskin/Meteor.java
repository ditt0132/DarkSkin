package dittonut.darkskin;
//NOTE: Nom-standard DarkForest feature!

import java.io.File;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Player;
import org.bukkit.structure.Structure;

public class Meteor {
  private static Structure structure;
  private static Random r = new Random();

  static {
    // this will load nbt structure
    try {
      structure = Bukkit.getStructureManager().loadStructure(new File(DarkSkin.getInstance().getDataFolder(), "meteor.nbt"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void spawnMeteor() {
    //this method will ran every 10 sec -done
    //by scheduler in mainclass -done
    //this will have radom chance to continue -done
    if (RandomUtils.nextDouble(0.0, 1.0) < 0.1) { //10%
      Collection<? extends Player> players = Bukkit.getOnlinePlayers();
      if (players.isEmpty()) return;
      Player p = players.stream().skip(r.nextInt(players.size())).findFirst().orElseThrow();
      Location loc = p.getWorld().getHighestBlockAt(Utils.getRandomLocation(p, 10, 50)).getLocation();
      structure.place(loc, false, Utils.randomEnum(StructureRotation.class), Utils.randomEnum(Mirror.class), 0, 1.0f, r);
      // must be falling entity but im lazy
    }
    //this will select random player on servwr -done
    //and select random location around player, not ;very near -least 10, max 50
    //and spawn meteor entity (marker) with Motion
    //attach eventlistener and place it when landed
    //TODO: make meteor trail, meteor model, attach 
  }
}
