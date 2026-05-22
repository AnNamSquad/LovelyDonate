package org.simpmc.lovelypay.config.types;

import de.exlll.configlib.Configuration;
import net.kyori.adventure.bossbar.BossBar;
import org.simpmc.lovelypay.config.types.data.BossBarConfig;
import org.simpmc.lovelypay.config.types.data.MilestoneConfig;
import org.simpmc.lovelypay.data.milestone.MilestoneType;

import java.util.List;
import java.util.Map;

@Configuration
public class MocNapConfig {
    public Map<MilestoneType, List<MilestoneConfig>> mocnap = Map.of(
            MilestoneType.ALL, List.of(new MilestoneConfig(
                            MilestoneType.ALL,
                            100000,
                            new BossBarConfig(true, "Mốc Nạp Toàn Thời Gian 100k",
                                    BossBar.Color.YELLOW,
                                    BossBar.Overlay.PROGRESS),
                            List.of("tell %player_name% Chúc mừng %player_name% đã đạt Mốc Nạp Toàn Thời Gian 100k")),
                    new MilestoneConfig(
                            MilestoneType.ALL,
                            200000,
                            new BossBarConfig(true, "Mốc Nạp Toàn Thời Gian 200k",
                                    BossBar.Color.YELLOW,
                                    BossBar.Overlay.PROGRESS),
                            List.of("tell %player_name% Chúc mừng %player_name% đã đạt Mốc Nạp Toàn Thời Gian 200k"))
            )
    );

}
