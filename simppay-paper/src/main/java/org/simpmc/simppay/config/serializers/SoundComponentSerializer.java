package org.simpmc.simppay.config.serializers;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.sound.Sound;
import org.simpmc.simppay.config.types.data.SoundConfig;

public class SoundComponentSerializer implements Serializer<Sound, SoundConfig> {

    @Override
    public SoundConfig serialize(Sound sound) {
        return new SoundConfig(
                sound.name(),
                sound.volume(),
                sound.pitch()
        );
    }

    @Override
    public Sound deserialize(SoundConfig s) {
        return Sound.sound(
                s.getKey(),
                Sound.Source.valueOf("MASTER"),
                s.getVolume(),
                s.getPitch()
        );
    }
}
