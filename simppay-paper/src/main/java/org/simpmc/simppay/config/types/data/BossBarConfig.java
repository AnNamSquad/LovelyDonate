package org.simpmc.simppay.config.types.data;

import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BossBarConfig {
    public boolean enabled;
    public String title;
    public BossBar.Color color;
    public BossBar.Overlay style;

}
