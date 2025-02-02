package dittonut.darkskin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class Family {
  public static Team getByOwner(String ownerName) {
    return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(ownerName);
  }
  public void removeMember(Player removed) {
    team.removePlayer(removed);
  }
  public static void addMember() {

  }
}
