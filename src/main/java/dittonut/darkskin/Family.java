package dittonut.darkskin;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class Family {
  public static Team getByOwner(String ownerName) {
    return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dt."+ownerName);
  }
  
  public static Team getTeam(UUID id) {
    return Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(Bukkit.getOfflinePlayer(id));
  }
  public static Team getTeam(Player p) {
    return getTeam(p.getUniqueId());
  }
}
