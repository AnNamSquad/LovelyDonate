package org.simpmc.simppay.config.serializers;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;

public class KeySerializer implements Serializer<Key, String> {
    @Override
    public String serialize(Key key) {
        return key.asString(); // minecraft:<resource> | Ex: minecraft:block.amethyst_block.resonate
    }

    @Override
    public Key deserialize(String s) {

        // split string by ':'
        @Subst("") String[] parts = s.split(":");
        return Key.key(parts[0], parts[1]); // custom key
    }
}
