package org.simpmc.simppay.config.types.menu.card.anvil;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.bukkit.Material;
import org.simpmc.simppay.config.annotations.Folder;
import org.simpmc.simppay.config.types.data.menu.DisplayItem;

import java.util.List;

@Configuration
@Folder("menus")
public class CardSerialMenuConfig {
    @Comment("Title có hỗ trợ PlaceholderAPI")
    public String title = "<gradient:#E34949:#D8DB5C><bold>SimpPay</bold><white> Nhập Số Serial";

    public DisplayItem item = DisplayItem.builder()
            .material(Material.DIAMOND)
            .amount(1)
            .name("<green>Nhập Số Serial...")
            .lores(List.of(
                    "<color:#24d65d> Nhập số serial của thẻ cào!"
            ))
            .build();
}
