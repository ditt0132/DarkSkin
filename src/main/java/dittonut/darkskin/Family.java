package dittonut.darkskin;

import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.UUID;

public record Family(UUID ownerId, Collection<UUID> players, Component name) {
    
}
