package org.lovelysmp.lovelypay.config.types.data;

import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lovelysmp.lovelypay.data.milestone.MilestoneType;

import java.util.List;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MilestoneConfig {
    public MilestoneType type;
    public int amount;
    public BossBarConfig bossbar;
    public List<String> commands;

}
