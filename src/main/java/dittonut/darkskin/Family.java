package dittonut.darkskin;

import net.kyori.adventure.text.Component;

import java.net.URL;
import java.util.Collection;
import java.util.UUID;

/**
 * Family.
 * @param ownerId UUID of owner
 * @param id ID of team including "dt." ex: dt.DittoNut
 * @param webhook
 */
public record Family(UUID ownerId, String id, URL webhook) {
    
}
