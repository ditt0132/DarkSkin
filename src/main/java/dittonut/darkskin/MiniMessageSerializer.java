package dittonut.darkskin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;

public class MiniMessageSerializer extends ScalarSerializer<Component> {
  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  protected MiniMessageSerializer() {
    super(Component.class);
  }

  @Override
  public Component deserialize(Type type, Object obj) throws SerializationException {
    if (!(obj instanceof String text)) {
      throw new SerializationException("Expected a string for MiniMessage deserialization, but got: " + obj);
    }
    return MINI_MESSAGE.deserialize(text);
  }

  @Override
  protected Object serialize(Component item, Predicate<Class<?>> typeSupported) {
    return MINI_MESSAGE.serialize(item);
  }

  public static void register(org.spongepowered.configurate.ConfigurationOptions options) {
    options.serializers(builder -> builder.register(Component.class, new MiniMessageSerializer()));
  }
}
