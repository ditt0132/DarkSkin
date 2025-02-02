package dittonut.darkskin;
//NOTE: Nom-standard DarkForest feature!

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.structure.Structure;

public class Meteor {
  private static Structure structure;

  static {
    // this will load nbt structure
    try {
      structure = Bukkit.getStructureManager().loadStructure(new File(DarkSkin.getInstance().getDataFolder(), "meteor.nbt"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void spawnMeteor(Location loc) {
    //this method will ran every 10 sec
    //by scheduler in mainclass
    //this will have radom chance to continue
    //this will select random player on servwr
    //and select random location around player, not very near
    //and spawn meteor entity (marker) with Motion
    //attach eventistener
  }
}
