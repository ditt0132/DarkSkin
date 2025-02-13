package dittonut.darkskin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static dittonut.darkskin.DarkSkin.mm;

public class Commands implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!sender.isOp()) return true;
    if (label.equals("open-end")) {
      Bukkit.broadcast(mm.deserialize("[SYSTEM] 엔드 차원이 오픈되었어요!"));
      Config.enableEnd = true;
    } else if (label.equals("close-end")) {
      Bukkit.broadcast(mm.deserialize("[SYSTEM] 엔드 차원이 수동으로 닫혔어요!"));
      Config.enableEnd = false;
    }
    return true;
  }
}
