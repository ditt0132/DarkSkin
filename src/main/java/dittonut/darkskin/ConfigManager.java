package dittonut.darkskin;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.ObjectMapper.Factory;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
  private final File configFile;
  private final YamlConfigurationLoader loader;
  private CommentedConfigurationNode root;
  private final ObjectMapper<Config> mapper;
  private Config config;

  public ConfigManager(File dataFolder) throws IOException {
    this.configFile = new File(dataFolder, "config.yml");
    this.loader = YamlConfigurationLoader.builder().file(configFile).build();
    this.mapper = ObjectMapper.factory().get(Config.class);
    load();
  }

  public void load() {
    try {
      if (!configFile.exists()) {
        configFile.createNewFile();
      }
      root = loader.load();
      config = mapper.load(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void save() {
    try {
      mapper.save(config, root);
      loader.save(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Config getConfig() {
    return config;
  }
}
