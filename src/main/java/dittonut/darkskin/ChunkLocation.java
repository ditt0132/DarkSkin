package dittonut.darkskin;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.Serializable;

class ChunkLocation implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String worldName;
  private final int x;
  private final int z;

  public ChunkLocation(Chunk chunk) {
    this.worldName = chunk.getWorld().getName();
    this.x = chunk.getX();
    this.z = chunk.getZ();
  }

  public Chunk toChunk(World world) {
    return world.getChunkAt(x, z);
  }

  public String getWorldName() {
    return worldName;
  }

  public int getX() {
    return x;
  }

  public int getZ() {
    return z;
  }
}