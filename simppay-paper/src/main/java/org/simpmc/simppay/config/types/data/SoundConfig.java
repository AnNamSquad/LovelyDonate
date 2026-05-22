package org.simpmc.simppay.config.types.data;

import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SoundConfig {
    public Key key;
    public float volume;
    public float pitch;

    public Sound toSound() {
        return Sound.sound(
                key,
                Sound.Source.valueOf("MASTER"),
                volume,
                pitch
        );
    }
}
